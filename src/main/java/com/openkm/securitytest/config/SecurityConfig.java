package com.openkm.securitytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	@Order(18)
	public SecurityFilterChain MiscWebSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.cors().and()
				.headers().frameOptions().disable()
				.and()
				.authorizeRequests()
				.antMatchers("/", "/index.html").permitAll()
				.antMatchers("/management.html").hasAnyAuthority("ROLE_ADMIN")
				.anyRequest().fullyAuthenticated()
				.and()
				.formLogin()
//				.loginPage("/login")
//				.failureUrl("/login?error=1")
				.permitAll()
				.and()
				.logout()
				.logoutSuccessUrl("/index.html")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
				.permitAll();
		return http.build();
	}
}
