package com.loizenai.jwtauthentication.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loizenai.jwtauthentication.model.User;
import com.loizenai.jwtauthentication.repository.UserRepository;

@RestController
@RequestMapping("/api/details")
public class BookingController {

	@Autowired
	private UserRepository repository;

	@PostMapping("/me")
	@CrossOrigin(origins = "http://localhost:3000")
	User registration(@RequestBody User user) {
		System.out.println("From register method" + user.getFirstName());
		// return new User("Rahul","Ram","rahulram03@gmail.com","0432657525");
		// //repository.save(newuser);
		//return repository.save(user);
		return null;
	}

	@PostMapping("/user/login")
	@CrossOrigin(origins = "http://localhost:3000")
	User login(@RequestBody String email, String password) {
		System.out.println("From login method" + email);
		//User user = repository.findByEmail(email);
		System.out.println("User exists:" + repository.existsByEmail(email));
		return null;
	}

	@GetMapping("/getalluser")
	List<User> getAllUser(@RequestBody User user) {
		System.out.println("all From register method" + user.getFirstName());
		return new ArrayList<User>();//
		// repository.save(newuser);
	}

	// Single item

	@GetMapping("/users/{id}")
	User one(@PathVariable Long id) {
		try {
			//return repository.findById(id).orElseThrow(() -> new Exception("id" + id));
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

