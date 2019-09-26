package javaspring.training.polls.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javaspring.training.polls.exception.AppException;
import javaspring.training.polls.model.Role;
import javaspring.training.polls.model.RoleName;
import javaspring.training.polls.model.User;
import javaspring.training.polls.payload.ApiResponse;
import javaspring.training.polls.payload.JwtAuthenticationResponse;
import javaspring.training.polls.payload.LoginRequest;
import javaspring.training.polls.payload.SignUpRequest;
import javaspring.training.polls.repository.RoleRepository;
import javaspring.training.polls.repository.UserRepository;
import javaspring.training.polls.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	JwtTokenProvider tokenProvider;
	
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsernameOrEmail(), 
						loginRequest.getPassword()
				)
		);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		
		if(Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
			return new ResponseEntity(
						new ApiResponse(false, "Username is already taken!"),
						HttpStatus.BAD_REQUEST);
		}
		
		if(Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
			return new ResponseEntity(
						new ApiResponse(false, "Email Adress already in use!"),
						HttpStatus.BAD_REQUEST);
		}
		
		//Creating user's account
		User user = new User(
				signUpRequest.getName(), 
				signUpRequest.getUsername(), 
				signUpRequest.getEmail(), 
				signUpRequest.getPassword());
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(
				()-> new AppException("User Role not set"));
			
		user.setRoles(Collections.singleton(userRole));
		
		User result = userRepository.save(user);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/api/users/{username}")
				.buildAndExpand(result.getUsername()).toUri();
		
		return ResponseEntity.created(location).body(
				new ApiResponse(true, "User registered successfully"));
	}
}
