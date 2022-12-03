package app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.entity.Pizza;
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
	
}
