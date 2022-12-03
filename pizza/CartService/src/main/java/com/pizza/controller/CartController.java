package com.pizza.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pizza.exception.AppException;
import com.pizza.exception.PizzaApiException;
import com.pizza.exception.ResourceNotFoundException;
import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Item;
import com.pizza.model.cart.ItemPK;
import com.pizza.model.cart.Order;
import com.pizza.model.cart.OrderDetail;
import com.pizza.model.role.Role;
import com.pizza.model.role.RoleName;
import com.pizza.model.user.User;
import com.pizza.security.CurrentUser;
import com.pizza.security.JwtTokenProvider;
import com.pizza.security.UserPrincipal;
import com.pizza.service.CartService;
import com.pizza.service.OrderService;
import com.pizza.utils.payload.ApiResponse;
import com.pizza.utils.payload.CartItemRequest;
import com.pizza.utils.payload.JwtAuthenticationResponse;
import com.pizza.utils.payload.LoginRequest;
import com.pizza.utils.payload.SignUpRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;


@RestController
@RequestMapping("/api/carts")
public class CartController {
	@Autowired
	CartService cartService;
	@Autowired
	OrderService orderService;
//	@GetMapping("/{id}")
//	public ResponseEntity<Cart> getCart(@PathVariable Long id) {
//		Cart cart = cartService.getCart(id);
//		return ResponseEntity.ok(cart);
//	}
	@GetMapping()
	@PreAuthorize("hasRole('USER')")
	@RateLimiter(name = "pizza")
	public ResponseEntity<Cart> getCartByUserId(@CurrentUser UserPrincipal principal) {
		Cart cart = cartService.getCartByUserId(principal.getId());
		System.out.println(cart);
		if(cart ==null) {
			Cart cart2 = new Cart();
			cart2.setUserId(principal.getId());
			cart2.setTotalPrice(0);
			cart2.setItems(null);
			cartService.addCart(cart2);
			
		}
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/items")
	@PreAuthorize("hasRole('USER')")
	@Retry(name = "pizza")
	public ResponseEntity<Item> saveOrUpdate(@RequestBody CartItemRequest item, @CurrentUser UserPrincipal principal) {
		System.out.println("retry "+ (count++));
		try {
	        System.out.println("time retry "+ Thread.currentThread().getName() + "...running  " +
	                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	        
	    } catch (Exception e){
	        System.out.println(e.getLocalizedMessage());
	    }
		Cart cart = cartService.getCartByUserId(principal.getId());
		Item i = null;
		if(cart != null) {
			Item item2 = new Item(cart, item.getProductId(), item.getQuantity());
			i = cartService.saveItem(item2);
		}
		else {
			Cart cart2 = new Cart();
			cart2.setUserId(principal.getId());
			cart2.setTotalPrice(0);
			cart2.setItems(null);
			cartService.addCart(cart2);
			Item item2 = new Item(cart2, item.getProductId(), item.getQuantity());
			i = cartService.saveItem(item2);
		}
		return ResponseEntity.ok(i);
	}
	
	@DeleteMapping("/items")
	@PreAuthorize("hasRole('USER')")
	public void removeItem(@RequestBody Item item, @CurrentUser UserPrincipal principal) {
		Cart cart = cartService.getCartByUserId(principal.getId());
		cartService.removeItem(new ItemPK(cart.getId(), item.getProductId()));
	}
	int count = 1;
	@PostMapping("/orders")
	@CircuitBreaker(name = "pizza")
//	@RateLimiter(name = "pizza")
	public Order orderPizza(@CurrentUser UserPrincipal principal) {
		System.out.println("retry "+ (count++));
		try {
	        System.out.println(Thread.currentThread().getName() + "...running  " +
	                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	        
	    } catch (Exception e){
	        System.out.println(e.getLocalizedMessage());
	    }
			
		Cart cart = cartService.getCartByUserId(principal.getId());
		if(cart == null)
			throw new ResourceNotFoundException("CART", "cart of user id", principal.getId());
//		neu co cart
		if(cart.getItems().size() == 0)
			throw new ResourceNotFoundException("ITEMS", "not found any items of cart ", cart.getId());
		
//		neu co item
//		chua set id
		Order or = orderService.addOrder(cart);
//		cartService.removeItems(cart.getItems());
		return or;
	}
	@GetMapping("/orders")
	public List<Order> getOrdersByUser(@CurrentUser UserPrincipal principal) {
		Long id = principal.getId();
		System.out.println("long "+id);
		return orderService.findAllByUserId(id);
	}
	
	@GetMapping("/items")
	public Item getItem(@RequestBody ItemPK id) {
		Item item = cartService.getItem(id);
		return item;
	}
	
}
