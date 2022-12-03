package com.pizza.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pizza.model.user.User;
import com.pizza.repository.UserRepository;
import com.pizza.security.UserPrincipal;
import com.pizza.service.CustomUserDetailsService;


@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService, CustomUserDetailsService {
	@Autowired
	private UserRepository userRepository;
	private static final String REDIS_CACHE_VALUE = "user";
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		UserDetails userCache = (UserDetails) redisTemplate.opsForHash().get(REDIS_CACHE_VALUE, usernameOrEmail);
		if(userCache != null) {
			System.out.println("get from cache redis");
			return userCache;
		}
		
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this username or email: %s", usernameOrEmail)));
		UserDetails userDB = UserPrincipal.create(user);
		try {
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getId(), userDB);
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getEmail(), userDB);
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getUsername(), userDB);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return userDB;
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Long id) {
		UserDetails userCache = (UserDetails) redisTemplate.opsForHash().get(REDIS_CACHE_VALUE, id);
		if(userCache != null) {
			System.out.println("get from cache redis");
			return userCache;
		}
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with id: %s", id)));
		UserDetails userDB = UserPrincipal.create(user);
		try {
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getId(), userDB);
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getEmail(), userDB);
			redisTemplate.opsForHash().put(REDIS_CACHE_VALUE, user.getUsername(), userDB);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return userDB;
	}
}
