package com.pizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pizza.model.role.Role;
import com.pizza.model.role.RoleName;


public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
