package com.pizza;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.pizza.security.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;


@SpringBootApplication
@OpenAPIDefinition
public class AuthPizzaApp {
	public static void main(String[] args) {
		SpringApplication.run(AuthPizzaApp.class, args);
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
}
