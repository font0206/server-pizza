package com.pizza.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pizza.model.cart.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	@Query(value = "select * from orders where user_id = ?1",nativeQuery = true)
	public List<Order> findAllByUserId(Long userId);
}
