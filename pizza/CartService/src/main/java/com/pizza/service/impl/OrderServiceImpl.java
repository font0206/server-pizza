package com.pizza.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
import com.pizza.exception.BadRequestException;
import com.pizza.exception.PizzaApiException;
import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Item;
import com.pizza.model.cart.ItemPK;
import com.pizza.model.cart.Order;
import com.pizza.model.cart.OrderDetail;
import com.pizza.model.cart.OrderDetailPK;
import com.pizza.repository.CartItemRepository;
import com.pizza.repository.CartRepository;
import com.pizza.repository.OrderDetailRepository;
import com.pizza.repository.OrderRepository;
import com.pizza.security.JwtTokenProvider;
import com.pizza.service.CartService;
import com.pizza.service.OrderService;
import com.pizza.utils.payload.ListProductId;
import com.pizza.utils.payload.PizzaUpdateDto;
import com.pizza.utils.payload.ProductDto;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CartItemRepository itemRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${app.host-product}")
	public String hostProduct;
	@Override
	@Transactional
	public Order addOrder(Cart cart) {
		Order order = Order.builder()
				.userId(cart.getUserId())
				.totalPrice(cart.getTotalPrice())
				.build();
		Order od = orderRepository.save(order);
		List<Item> listItem = cart.getItems();
		List<OrderDetail> listOrderDetail = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		
		for(int i =0; i< listItem.size(); ++i) {
			Item item = listItem.get(i);
			OrderDetail orderDetail = OrderDetail.builder()
					.order(od)
					.productId(item.getProductId())
					.quantity(item.getQuantity())
					.price(item.getPrice())
					.build();
			ids.add(item.getProductId());
			listOrderDetail.add(orderDetail);
		}
		List<OrderDetail> list = orderDetailRepository.saveAll(listOrderDetail);
		od.setOrderDetails(list);
		ListProductId listProductId = new ListProductId();
		listProductId.setIds(ids);
		List<ProductDto> pdtos = getListProductByIds(listProductId);
		for(int i =0; i< listOrderDetail.size(); ++i) {
			int qty = listOrderDetail.get(i).getQuantity();
			int oldQty = pdtos.get(i).getQuantity();
			if(oldQty - qty < 0)
				throw new BadRequestException("khong du so luong");
			pdtos.get(i).setQuantity(oldQty - qty);
			
		}
		PizzaUpdateDto pdtos1 = new PizzaUpdateDto();
		pdtos1.setProducts(pdtos);
		updateProducts(pdtos1);
		itemRepository.deleteByCart(cart.getId());
		List<Item> items1 = cart.getItems();
		cart.setTotalPrice(0);
		Cart cartNew = new Cart(cart.getId(),cart.getUserId(),0,items1);
		cartRepository.save(cartNew);
		return od;
	}

	
	public List<ProductDto> getListProductByIds(ListProductId id){
		HttpHeaders headers = new HttpHeaders();
		System.out.println("IDS "+id.getIds().size());
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + jwtTokenProvider.getToken());
		Gson gson = new Gson();
		HttpEntity<String> pizza = new HttpEntity<String>(gson.toJson(id),headers);
		System.out.println("request "+ pizza.toString());
		ProductDto[] dtoArray= restTemplate.postForObject("http://"+hostProduct+":4000/api/pizzas/ids", pizza, ProductDto[].class);
		List<ProductDto> result = Arrays.asList(dtoArray);
		return result;
	}
	
	public boolean updateProducts( PizzaUpdateDto pdto){
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Bearer " + jwtTokenProvider.getToken());
		HttpEntity<PizzaUpdateDto> pizza = new HttpEntity<PizzaUpdateDto>(pdto,headers);
		try {
			restTemplate.put("http://"+hostProduct+":4000/api/pizzas", pizza, PizzaUpdateDto.class);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	@Override
	public List<OrderDetail> saveOrderDetails(List<OrderDetail> orders) {
		
		return null;
	}

	@Override
	public boolean removeOrderDetail(OrderDetailPK detailPK) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteOrder(Long orderId) {
		 orderRepository.deleteById(orderId);
		return true;
	}

	@Override
	public Order getOrder(Long orderId) {
		return orderRepository.findById(orderId).get();
	}

	@Override
	public List<Order> findAllByUserId(Long userId) {
		
		return orderRepository.findAllByUserId(userId);
	}

	@Override
	public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
		return null;
	}



}
