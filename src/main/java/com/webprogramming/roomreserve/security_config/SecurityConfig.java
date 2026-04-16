package com.webprogramming.roomreserve.security_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Added CORS configuration here
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                
                // Admin specific
                .requestMatchers(HttpMethod.POST, "/api/rooms", "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/rooms", "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/rooms", "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Shared Endpoints
                .requestMatchers(HttpMethod.GET, "/api/rooms/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/bookings/**").hasAnyRole("USER", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
