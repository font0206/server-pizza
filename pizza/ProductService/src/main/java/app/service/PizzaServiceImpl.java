package app.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entity.Pizza;
import app.exception.ResourceNotFoundException;
import app.repository.PizzaRepository;
@Service
@Transactional
public class PizzaServiceImpl implements PizzaService {
	@Autowired
	private PizzaRepository repo;
	@Override
	public Pizza getOnePizza(Long id) {
		
		
		return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pizza", "id", id));
	}
	@Override
	public List<Pizza> getAllPizza() {
		
		return repo.findAll();
	}
	@Override
	public Pizza addOrUpdatePizza(Pizza pz) {
		return repo.save(pz);
	}
	@Override
	public boolean deletePizza(Long id) {
		try {
			repo.deleteById(id);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	@Override
	public List<Pizza> updateProducts(List<Pizza> products) {
		
		List<Pizza> list = repo.saveAll(products);
		return list;
	}
	@Override
	public List<Pizza> findByListId(List<Long> ids) {

			
			
		return repo.findAllById(ids);
	}

}
