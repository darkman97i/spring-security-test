package com.openkm.securitytest.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurationManagers {
	private static final Logger log = LoggerFactory.getLogger(SecurityConfigurationManagers.class);

	private static final String PROPERTY_KEY_AUTH_SUPERVISOR = "okm.authentication.supervisor";
	private static final String PROPERTY_KEY_AUTH_DATABASE = "okm.authentication.database";

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired
	private AbstractEnvironment env;

	/* Using the authenticationManagerBuilder */
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		// Supervisor
		if ("true".equals(env.getProperty(PROPERTY_KEY_AUTH_SUPERVISOR))) {
			String adminUser = "admin"; // Usually the okmAdmin user, but not always
			String passwd = "test";
			String roleAdmin = "ROLE_ADMIN"; // Must remove ROLE_ at the beginning because by default is set by inMemoryAuthentication

			if (roleAdmin.startsWith("ROLE_")) {
				roleAdmin = roleAdmin.substring(5);
			}

			authenticationManagerBuilder.inMemoryAuthentication()
					.withUser(adminUser)
					.password(passwordEncoder().encode(passwd))
					.roles(roleAdmin);
			log.info("*****************************************************");
			log.info("* Generated supervisor user: {}, password: {}", adminUser, passwd);
			log.info("*****************************************************");
		}
		// Internal database
		if ("true".equals(env.getProperty(PROPERTY_KEY_AUTH_DATABASE))) {
			authenticationManagerBuilder.jdbcAuthentication()
					.dataSource(dataSource)
					.passwordEncoder(new BCryptPasswordEncoder())
					.usersByUsernameQuery("select USR_ID, USR_PASSWORD, 1 from OKM_USER where USR_ID=? and USR_ACTIVE='T'")
					.authoritiesByUsernameQuery("select UR_USER, UR_ROLE from OKM_USER_ROLE where UR_USER=?");
		}
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		return authenticationManager;
	}

    /* Bean itself works */
//	@Bean
//	@Qualifier("InMemoryUserDetails")
//	public UserDetailsService InMemoryUserDetails() {
//		String adminUser = "admin"; // Usually the okmAdmin user, but not always
//		String roleAdmin = "ROLE_ADMIN"; // Must remove ROLE_ at the beginning because by default is set by inMemoryAuthentication
//
//		if (roleAdmin.startsWith("ROLE_")) {
//			roleAdmin = roleAdmin.substring(5);
//		}
//
//		String passwd = "test";
//		PasswordEncoder encoder = passwordEncoder();
//		log.info("*****************************************************");
//		log.info("* Generated supervisor user: {}, password: {}", adminUser, passwd);
//		log.info("*****************************************************");
//
//		UserDetails user = User.withUsername(adminUser)
//				.passwordEncoder(encoder::encode)
//				.password(passwd)
//				.roles(roleAdmin)
//				.build();
//		return new InMemoryUserDetailsManager(user);
//	}

	/* Setting default enconder for the application */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Set the default Password encoder what will be used by JdbcUserDetailsManager otherwise authentication does not works
	}

	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		// Remove the ROLE_ prefix
		return new GrantedAuthorityDefaults("");
	}
}
