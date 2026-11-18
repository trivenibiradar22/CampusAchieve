
package com.example.campusachieve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.campusachieve.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						// Public endpoints
						.requestMatchers("/api/auth/login").permitAll().requestMatchers("/api/users/register")
						.permitAll()

						.requestMatchers(HttpMethod.GET, "/api/users/*/profile").permitAll()

						.requestMatchers("/api/qr/**").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/public/certificates/**").permitAll()

						// Public HTML pages and static resources
						.requestMatchers("/", "/index.html", "/login.html", "/register.html", "/profile.html",
								"/dashboard.html", "/faculty-dashboard.html", "/css/**", "/js/**", "/images/**")
						.permitAll().requestMatchers(HttpMethod.GET, "/api/public/users/*/achievements").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/public/certificates/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/users/*/profile").permitAll()
						.requestMatchers("/public-profile.html").permitAll()
						.requestMatchers("/public-profile.html", "/api/public/**").permitAll()
						// Faculty and Admin: approve or reject achievements
						.requestMatchers("/api/achievements/*/approve", "/api/achievements/*/reject")
						.hasAnyRole("FACULTY", "ADMIN")

						// Faculty endpoints
						.requestMatchers("/api/faculty/**").hasRole("FACULTY")

						// Add achievements
						.requestMatchers(HttpMethod.POST, "/api/achievements/user/*")
						.hasAnyRole("STUDENT", "FACULTY", "ADMIN")

						// Update achievements
						.requestMatchers(HttpMethod.PUT, "/api/achievements/*").hasAnyRole("STUDENT", "ADMIN")

						// Delete achievements
						.requestMatchers(HttpMethod.DELETE, "/api/achievements/*").hasAnyRole("STUDENT", "ADMIN")

						// Update own profile
						.requestMatchers(HttpMethod.PUT, "/api/users/*/profile")
						.hasAnyRole("STUDENT", "FACULTY", "ADMIN")

						// Admin-only user management
						.requestMatchers("/api/users/**").hasRole("ADMIN")

						// Faculty and Admin: dashboard statistics
						.requestMatchers("/api/dashboard/statistics").hasAnyRole("FACULTY", "ADMIN")

						// All other endpoints require authentication
						.anyRequest().authenticated())

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
