package com.uway.booking.security.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uway.booking.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		System.out.println("loadUserByUsername" + email);
		
		com.uway.booking.model.User user = userRepository.findByEmail(email);
		
		System.out.println(user);
		System.out.println(user.getFirstName());

		return new User(user.getEmail(), user.getPassword(), new ArrayList<GrantedAuthority>());

	}
}