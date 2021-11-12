package com.appdevelopers.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appdevelopers.app.ws.SpringApplicationContext;
import com.appdevelopers.app.ws.service.UserService;
import com.appdevelopers.app.ws.shared.dto.UserDto;
import com.appdevelopers.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {

		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		// return super.attemptAuthentication(request, response);
		try {

			// contentType = request.getHeader("Accept");

			UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(),
					UserLoginRequestModel.class);

			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())
//                            creds.getEmail(),
//                            creds.getPassword(),
//                            new ArrayList<>())
					

			);

		} catch (IOException e) {

			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String userName = ((User) authResult.getPrincipal()).getUsername();

		String token = Jwts.builder().setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstant.getTokenSecret()).compact();
		// UserService userService =
		// (UserService)SpringApplicationContext.getBean("userServiceImpl");
		// UserDto userDto = userService.getUser(userName);

		UserService userService = (UserService) SpringApplicationContext.getbean("userServiceImpl");
		UserDto userDto = userService.getUser(userName);
		response.addHeader(SecurityConstant.HEADER_STRING, SecurityConstant.TOKEN_PREFIX + token);
		response.addHeader("UserID", userDto.getUserId());
		// response.addHeader("UserID", userDto.getUserId());

	}

//	so there will be able to authorize this user using the access token and then if authorization is successful,
//	it will use the user I.D. to perform the operation we have requested, which is to update user details.
//	So in this video, we're going to implement user authorization filter a filter that will read the
//	header value and will extract the Json Web token and will perform user authorization using these JSON
//	left.

}
