package com.pizza.utils.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PizzaUpdateDto {
	List<ProductDto> products;
}
