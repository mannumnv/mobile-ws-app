package com.appdevelopers.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter{
	
	public AuthorizationFilter(AuthenticationManager authManager) {
		
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		//super.doFilterInternal(request, response, chain);
		
		String header = request.getHeader(SecurityConstant.HEADER_STRING);
		// here if header is null or does not start with given prefix condition
		if(header == null || !header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
			
			chain.doFilter(request, response);
			return;
			
		}
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}
		private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
			
			String token = request.getHeader(SecurityConstant.HEADER_STRING);
			
			if (token != null) {
	            
				token = token.replace(SecurityConstant.TOKEN_PREFIX, "");
				// parse the token.	
	            String user = Jwts.parser()
	                    .setSigningKey(SecurityConstant.getTokenSecret())
	                    .parseClaimsJws(token)
	                    .getBody()
	                    .getSubject();
	            
	            System.out.println("user " +user);

	            if (user != null) {
	                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
	            }
	            return null;
	        }
	        return null;
	    }
		}

