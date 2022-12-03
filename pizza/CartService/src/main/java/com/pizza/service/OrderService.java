package com.pizza.service;

import java.util.List;

import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Order;
import com.pizza.model.cart.OrderDetail;
import com.pizza.model.cart.OrderDetailPK;

public interface OrderService {
	public Order addOrder(Cart cart);
	public OrderDetail saveOrderDetail(OrderDetail orderDetail);
	public List<OrderDetail> saveOrderDetails(List<OrderDetail> orders);
	public boolean removeOrderDetail(OrderDetailPK detailPK);
	public boolean deleteOrder(Long orderId);
	public Order getOrder(Long orderId);
	public List<Order> findAllByUserId(Long userId);
	
	
}
