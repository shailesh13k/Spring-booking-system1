package com.uway.booking.controller;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uway.booking.message.request.LoginForm;
import com.uway.booking.message.request.SignUpForm;
import com.uway.booking.message.response.JwtResponse;
import com.uway.booking.message.response.ResponseMessage;
import com.uway.booking.model.User;
import com.uway.booking.repository.UserRepository;
import com.uway.booking.security.jwt.JwtTokenUtil;
import com.uway.booking.service.EmailSenderService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	EmailSenderService emailSenderService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
		final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername(), userDetails.getAuthorities()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
		System.out.println(signUpRequest.getEmail());

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getMobile());
		//System.out.println(encoder.encode(signUpRequest.getPassword()));
		userRepository.save(user);
		
	try {
		emailSenderService.sendEmail(user.getEmail(), "Welcome to UWay software", user.getFirstName() + " " + user.getLastName());
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

		return new ResponseEntity<>(
				new ResponseMessage("User " + signUpRequest.getFirstName() + " is registered successfully!"),
				HttpStatus.OK);
	}

}