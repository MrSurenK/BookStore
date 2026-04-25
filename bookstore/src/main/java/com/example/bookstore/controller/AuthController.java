package com.example.bookstore.controller;

import com.example.bookstore.customExceptions.BadRequestException;
import com.example.bookstore.dto.Res;
import com.example.bookstore.model.Roles;
import com.example.bookstore.model.User;
import com.example.bookstore.repo.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public AuthController(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<Res<Object>> register(@RequestBody RegisterRequest request){

		if(request.username == null || request.username.isBlank() || request.password == null || request.password.isBlank()){
			throw new BadRequestException("username and password are required");
		}

		if(userRepo.findByUsername(request.username).isPresent()){
			throw new BadRequestException("username already exists");
		}

		Roles role;
		try{
			role = Roles.valueOf(request.role == null ? "USER" : request.role.toUpperCase());
		}catch(Exception e){
			throw new BadRequestException("invalid role. Allowed: USER, MANAGER, OWNER");
		}

		User user = new User();
		user.setUsername(request.username);
		user.setPassword(passwordEncoder.encode(request.password));
		user.setRole(role);

		userRepo.save(user);

		return ResponseEntity.status(201).body(Res.success(null, "user created"));
	}

	// simple DTOs used by controller
	public static class RegisterRequest{
		public String username;
		public String password;
		public String role; // optional: USER, MANAGER, OWNER
	}


}
