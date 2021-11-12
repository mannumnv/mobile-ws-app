package com.appdevelopers.app.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdevelopers.app.ws.security.AppProperties;

@SpringBootApplication
//@ComponentScan("com.appdevelopers.app.ws")
public class MobileAppWsApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// TODO Auto-generated method stub
		return application.sources(MobileAppWsApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(MobileAppWsApplication.class, args);
		// System.out.println(10/0);
	}
	@Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
	  return new BCryptPasswordEncoder();
  }
	
	//Here we created Application context but it need to be available as bean as well 
	//other wise it will give null pointer exception
	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean(name = "AppProperties")
	public AppProperties getAppProperties() {
		// important to return new instance of app properties
		return new AppProperties();
	}
}
