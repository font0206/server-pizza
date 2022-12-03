package app.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import app.entity.Pizza;
import app.payload.ListProductId;
import app.payload.PizzaUpdateDto;
import app.service.PizzaService;

@RestController
@RequestMapping("api/pizzas")
public class PizzaController {
	@Autowired
	private PizzaService service;
	
	@GetMapping()
	@PreAuthorize("hasRole('USER')")
	public List<Pizza> getAllPizza(){
		return service.getAllPizza();
	}
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Pizza> getOnePizza(@PathVariable Long id) {
		return new ResponseEntity<>(service.getOnePizza(id), HttpStatus.OK);
	}
	@PostMapping()
	public Pizza addOrUpdatePizza(@RequestBody Pizza pz) {
		return service.addOrUpdatePizza(pz);
	}
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean deletePizza(@PathVariable Long id) {
		boolean check = service.deletePizza(id);
		return check;
	}

	@PutMapping
	public boolean addOrUpdatePizza(@RequestBody PizzaUpdateDto listUpdate) {
		if(service.updateProducts(listUpdate.getProducts()).size() > 0)
			return true;
		return false;
	}
	
	@PostMapping("/ids")
	public List<Pizza> getPizzaByIds(@RequestBody ListProductId ids) {
		return service.findByListId(ids.getIds());
	}
}
