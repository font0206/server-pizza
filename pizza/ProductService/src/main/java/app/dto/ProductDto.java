package app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
	private Long id;
	private String name;
	private String info;
	private double price;
	private int quantity;
}
