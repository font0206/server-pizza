package com.pizza;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.web.client.RestTemplate;

import com.pizza.security.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;


@SpringBootApplication
@OpenAPIDefinition
@EntityScan(basePackageClasses = { CartPizzaApp.class, Jsr310Converters.class })
public class CartPizzaApp {
	public static void main(String[] args) {
		SpringApplication.run(CartPizzaApp.class, args);
	}
	
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
