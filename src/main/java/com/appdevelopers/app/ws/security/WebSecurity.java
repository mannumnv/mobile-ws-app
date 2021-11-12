package com.appdevelopers.app.ws.security;


import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdevelopers.app.ws.service.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter  {
	
	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	

	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		//super();
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	 		
	 
//	@Override
//    protected void configure(HttpSecurity http) throws Exception {
//		 http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST , SecurityConstant.SIGN_UP_URL)
//        .permitAll()
//        .anyRequest().authenticated().and().addFilter(new AuthenticationFilter(authenticationManager()));
//        	
//		 }
	@Override
	public void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST ,SecurityConstant.SIGN_UP_URL)
        .permitAll().antMatchers(HttpMethod.GET ,SecurityConstant.VERIFICATION_EMAIL_URL)
        .permitAll().antMatchers(HttpMethod.POST ,SecurityConstant.PASSWORD_RESET_REQUEST_URL)
        .permitAll().antMatchers(HttpMethod.POST ,SecurityConstant.PASSWORD_RESET_URL)
       // .permitAll().antMatchers(SecurityConstant.H2_CONSOLE)
        .permitAll()
       .anyRequest().authenticated().and().addFilter(getAuthenticationFilter())
       .addFilter(new AuthorizationFilter(authenticationManager()))
       .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
		
		//http.headers().frameOptions().disable();
	}
	//Here session creation policy tells Spring security to that our rest api 
	//should be stateless and we do not want to create a session.
	//here we are creating AuthenticationFilter manually and bcz of that we cannot access service implementation
	//for that we have to autowired it so that we can inject in spring security.
	//for that we create springContext
	

	@Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    
            // enable in memory based authentication with a user named "user" and "admin"
            
    }

	// 403 forbidden error means we have to check the url(path) or the details that we provided is correct or not.
	// creating customize user authentication url
	public AuthenticationFilter getAuthenticationFilter() throws Exception{
		
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		return filter;
		
	}
	
}
