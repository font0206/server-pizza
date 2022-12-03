package com.pizza.model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pizza.model.role.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@NoArgsConstructor
public class User {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String firstName;

	private String lastName;

	private String username;

	private String password;

	private String email;

	private List<Role> roles;





	public User(String firstName, String lastName, String username, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
	}










	public List<Role> getRoles() {

		return roles == null ? null : new ArrayList<>(roles);
	}

	public void setRoles(List<Role> roles) {

		if (roles == null) {
			this.roles = null;
		} else {
			this.roles = Collections.unmodifiableList(roles);
		}
	}

}
