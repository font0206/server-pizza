package com.pizza.model.cart;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemPK implements Serializable{
	 private Long cart;
	 private Long productId;
	@Override
	public String toString() {
		return "ItemPK [cart=" + cart + ", productId=" + productId + "]";
	}
	 
}
