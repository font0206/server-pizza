package com.pizza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pizza.model.cart.Cart;
import com.pizza.model.cart.Item;
import com.pizza.model.cart.ItemPK;

@Repository
public interface CartItemRepository extends JpaRepository<Item, ItemPK>{
	@Modifying
	@Query(value = "delete from items where cart_id = ?",nativeQuery = true)
	public void deleteByCart(Long id);
	
}
