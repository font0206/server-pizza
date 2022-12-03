package com.pizza.utils.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
	@NotBlank
	private String usernameOrEmail;

	@NotBlank
	private String password;
}
