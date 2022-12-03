package com.pizza.utils.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class ListProductId {
	List<Long> ids;
}
