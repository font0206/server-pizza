package com.pizza.service.impl;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pizza.model.user.User;
import com.pizza.security.JwtTokenProvider;
import com.pizza.security.UserPrincipal;
import com.pizza.service.CustomUserDetailsService;


@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService, CustomUserDetailsService {


	@Autowired
	public JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public RestTemplate restTemplate;
	
	@Value("${app.host-auth}")
	public String host;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      headers.add("Authorization","Bearer " +jwtTokenProvider.getToken());
	      HttpEntity  entity = new HttpEntity<>(headers);
	      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://"+host+":8081/api/auth/findByUsernameOrEmail")
	    	        .queryParam("usernameOrEmail", usernameOrEmail);
	      UserDetails user =  restTemplate.exchange(builder.toUriString(),HttpMethod.GET, entity, UserDetails.class).getBody();
		System.out.println(user.toString());
		return user;
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Long id) {
		HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      headers.add("Authorization","Bearer " +jwtTokenProvider.getToken());
	      HttpEntity entity = new HttpEntity(headers);
	      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://"+host+":8081/api/auth/loadUserById")
	    	        .queryParam("id", id);
	      
	      User user =  restTemplate.exchange(builder.toUriString(),HttpMethod.GET, entity, User.class).getBody();
	      System.out.println("load "+ user.toString());
	      
		return UserPrincipal.create(user);
	}
}
