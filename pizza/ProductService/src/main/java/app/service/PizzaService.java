package app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.entity.Pizza;

@Service
public interface PizzaService {
	Pizza getOnePizza(Long id);
	List<Pizza> getAllPizza();
	Pizza addOrUpdatePizza(Pizza pz);
	boolean deletePizza(Long id);
	List<Pizza> updateProducts(List<Pizza> products);
	List<Pizza> findByListId(List<Long> ids);
}
