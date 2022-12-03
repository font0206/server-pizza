package com.pizza.model.cart;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "items")
@IdClass(ItemPK.class)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Item {
	@Id
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "cartId")
	private Cart cart;
	@Id
	private Long productId;
	private int quantity;
	private double price;
	
	
	public ItemPK getId() {
		return new ItemPK(cart.getId(), productId);
	}
	public void setId(ItemPK id) {
		this.productId = id.getProductId();
		this.cart.setId(id.getCart());
	}
	public Item(Cart cart, Long productId, int quantity) {
		this.cart = cart;
		this.productId = productId;
		this.quantity = quantity;
	}
	public Item(Long productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "Item [cart=" + cart + ", productId=" + productId + ", quantity=" + quantity + ", price=" + price + "]";
	}
	
}
