package com.appdevelopers.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//it need to be component  so that it can autowired some object that we need to read the property file
@Component
public class AppProperties {
	
	//For environment object to be available in our appproperties we will need to autowired it
	@Autowired
	private Environment env;
	
	public String getTokenSecret() {
		
		return env.getProperty("token");
	}
	
	
}
