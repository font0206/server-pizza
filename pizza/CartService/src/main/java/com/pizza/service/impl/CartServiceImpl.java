package com.pizza.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.pizza.exception.PizzaApiException;
import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Item;
import com.pizza.model.cart.ItemPK;
import com.pizza.repository.CartItemRepository;
import com.pizza.repository.CartRepository;
import com.pizza.security.JwtTokenProvider;
import com.pizza.service.CartService;
import com.pizza.utils.payload.ProductDto;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartItemRepository itemRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Cart addCart(Cart c) {
		Cart cart = cartRepository.saveAndFlush(c);
		return cart;
	}
	@Value("${app.host-product}")
	public String hostProduct;

	@Override
	public Item saveItem(Item i) {
		Long productId = i.getProductId();
		int quantity = i.getQuantity();
		Cart cart = i.getCart();
		System.out.println("cartt"+ cart.getId());

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization","Bearer " +jwtTokenProvider.getToken());
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://"+hostProduct+":4000/api/pizzas/".concat(String.valueOf(productId)));
		ProductDto pDto =  restTemplate.exchange(builder.toUriString(),HttpMethod.GET, entity, ProductDto.class).getBody();
		System.out.println("aaaaaaa "+pDto);
		if(pDto != null ) {
			i.setPrice(pDto.getPrice()); 
		}else {
			throw new PizzaApiException(HttpStatus.NOT_FOUND, "Pizza not found");
		}
		
		if(quantity  > pDto.getQuantity())
			throw new PizzaApiException(HttpStatus.NOT_MODIFIED, "quantity pizza exceeds quantity in store ");
		else {
			Gson gson = new Gson();
			int stock = pDto.getQuantity();
			ItemPK cartItemPK = new ItemPK(i.getCart().getId(), productId);
			Optional<Item> oldCartItem =  itemRepository.findById(cartItemPK);
			
			if(!oldCartItem.isPresent() ) {
				double oldPrice = cart.getTotalPrice();
				cart.setTotalPrice(oldPrice + quantity* pDto.getPrice());
				pDto.setQuantity(stock - quantity);
			}else {
				double oldPrice = cart.getTotalPrice();
				Item oldCI =  oldCartItem.get();
				cart.setTotalPrice(oldPrice - (oldCI.getQuantity()* oldCI.getPrice()) + (i.getQuantity()*i.getPrice()));
				stock = oldCI.getQuantity() + stock;
				pDto.setQuantity(stock - quantity);
			}
			System.out.println("PARSE "+gson.toJson(pDto));
			HttpEntity<ProductDto> pizza = new HttpEntity<ProductDto>(pDto,headers);
			
//			ProductDto result = restTemplate.postForEntity("http://localhost:4000/api/pizzas", pizza, ProductDto.class).getBody();
//			System.out.println("PRODUCT ISS: "+result.toString());
		}
		Item item = itemRepository.save(i);
		cartRepository.save(cart);
		return item;
	}

	@Override
	public boolean removeItem(ItemPK itemId) {
		try {
			
			Optional<Item> itemOptional = itemRepository.findById(itemId);
			if(itemOptional.isPresent()){
				Item item = itemOptional.get();
				Cart cart = itemOptional.get().getCart();
				double price = item.getPrice()* item.getQuantity();
				double oldTotalPrice = cart.getTotalPrice();
				itemRepository.deleteById(itemId);
				cart.setTotalPrice(oldTotalPrice - price);
				cartRepository.save(cart);
				return true;
			}
		
				
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean deleteCart(Long cartId) {
		try {
			cartRepository.deleteById(cartId);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	@Override
	public Cart getCart(Long cartId) {
		Cart cart = cartRepository.findById(cartId).get();
		return cart;
	}

	@Override
	public Cart getCartByUserId(Long userId) {

		Cart cart = cartRepository.findByUserId(userId);
		return cart;
	}

	@Override
	public boolean removeItems(List<Item> items) {
		// TODO Auto-generated method stub
		itemRepository.deleteAll(items);
		return true;
	}

	@Override
	public Item getItem(ItemPK itemId) {
		
		return itemRepository.findById(itemId).get();
	}

}
