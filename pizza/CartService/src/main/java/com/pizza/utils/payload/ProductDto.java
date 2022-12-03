package com.pizza.utils.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable{
	private Long id;
	private String name;
	private String info;
	private int quantity;
	private double price;
}
