package com.pizza.service;

import java.util.List;

import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Item;
import com.pizza.model.cart.ItemPK;

public interface CartService {
	public Cart addCart(Cart cart);
	public Item saveItem(Item item);
	public Item getItem(ItemPK itemId);
	public boolean removeItem(ItemPK itemId);
	public boolean removeItems(List<Item> items);
	public boolean deleteCart(Long cartId);
	public Cart getCart(Long cartId);
	public Cart getCartByUserId(Long userId);
}
