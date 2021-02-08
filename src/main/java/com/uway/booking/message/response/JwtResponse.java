package com.uway.booking.message.response;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String userName;
	private Collection<? extends GrantedAuthority> authorities;

	public JwtResponse(String accessToken, String userName, Collection<? extends GrantedAuthority> authorities) {
		this.token = accessToken;
		this.userName = userName;
		this.authorities = authorities;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}