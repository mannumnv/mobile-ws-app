package com.appdevelopers.app.ws.security;

import com.appdevelopers.app.ws.SpringApplicationContext;

public class SecurityConstant {

	public static final long EXPIRATION_TIME = 864000000; // 10 days
	public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // 1 hour
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	 public static final String VERIFICATION_EMAIL_URL =
	 "/users/email-verification";
	 public static final String PASSWORD_RESET_REQUEST_URL =
	"/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final String H2_CONSOLE="/h2-console/**";
	// here we are getting token secret from properties file
	// public static final String TOKEN_SECRET = "jf9i4jgu83nf10";
	// NOTES:
	// to be able to access components that were created by spring framework like
	// application property
	// is a component. So for us to be able to access classes like this from the
	// security concerns, we will need to access
	// it via this Spring application context, which we have created earlier.
	// It is in our apps ws folder and this is the application context that we use
	// to access Beans, which
	// were created by Spring framework.

	// here we mark it as static so that we dont need to create new instance of Security Constant

	public static String getTokenSecret() {

		AppProperties appProperties = (AppProperties) SpringApplicationContext.getbean("AppProperties");
		return appProperties.getTokenSecret();

	}
}
