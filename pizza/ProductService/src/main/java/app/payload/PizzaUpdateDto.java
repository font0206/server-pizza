package app.payload;

import java.util.List;

import app.entity.Pizza;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PizzaUpdateDto {
	List<Pizza> products;
}
