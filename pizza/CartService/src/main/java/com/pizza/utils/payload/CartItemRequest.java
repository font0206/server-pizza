package com.pizza.utils.payload;

import lombok.Data;

@Data
public class CartItemRequest {
	private Long productId;
	private int quantity;
	private double price;
}
