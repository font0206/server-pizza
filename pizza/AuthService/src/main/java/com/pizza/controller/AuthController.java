package com.pizza.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pizza.exception.AppException;
import com.pizza.exception.PizzaApiException;
import com.pizza.model.role.Role;
import com.pizza.model.role.RoleName;
import com.pizza.model.user.User;
import com.pizza.repository.RoleRepository;
import com.pizza.repository.UserRepository;
import com.pizza.security.CurrentUser;
import com.pizza.security.JwtTokenProvider;
import com.pizza.security.UserPrincipal;
import com.pizza.service.CustomUserDetailsService;
import com.pizza.utils.payload.ApiResponse;
import com.pizza.utils.payload.JwtAuthenticationResponse;
import com.pizza.utils.payload.LoginRequest;
import com.pizza.utils.payload.SignUpRequest;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final String USER_ROLE_NOT_SET = "User role not set";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private static final String REDIS_CACHE_VALUE = "user";
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtTokenProvider.generateToken(authentication);
		System.out.println("xoa cache token");
		redisTemplate.opsForHash().delete(REDIS_CACHE_VALUE, jwtTokenProvider.getUserIdFromJWT(jwt));
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
			throw new PizzaApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
		}

		if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
			throw new PizzaApiException(HttpStatus.BAD_REQUEST, "Email is already taken");
		}

		String firstName = signUpRequest.getFirstName().toLowerCase();

		String lastName = signUpRequest.getLastName().toLowerCase();

		String username = signUpRequest.getUsername().toLowerCase();

		String email = signUpRequest.getEmail().toLowerCase();

		String password = passwordEncoder.encode(signUpRequest.getPassword());

		User user = new User(firstName, lastName, username, email, password);

		List<Role> roles = new ArrayList<>();
		
		
		if (userRepository.count() == 0) {
			roles.add(roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
			roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
		} else {
			roles.add(roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
		}

		user.setRoles(roles);

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
				.buildAndExpand(result.getId()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
	}
	
	@GetMapping("/findByUsernameOrEmail")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserDetails> findByUsernameOrEmail(@RequestParam(value = "usernameOrEmail") String usernameOrEmail, @CurrentUser UserPrincipal currentUser) {
		UserDetails userIdentityAvailability = customUserDetailsService.loadUserByUsername(usernameOrEmail);
		return new ResponseEntity<UserDetails>(userIdentityAvailability, HttpStatus.OK);
	}

	@GetMapping("/loadUserById")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<User> loadUserById(@RequestParam(value = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		User user = new User(currentUser.getFirstName()	, currentUser.getLastName(), currentUser.getUsername(), currentUser.getEmail(), currentUser.getPassword());
		user.setId(currentUser.getId());
		List<Role> roles = new ArrayList<>();
		for( GrantedAuthority r : currentUser.getAuthorities()) {
			Role rl = new Role(RoleName.valueOf(RoleName.class, r.getAuthority()));
			roles.add(rl);
		}
		user.setRoles(roles);
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
}
