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
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                
                // Admin specific [cite: 13, 14, 15, 16, 17]
                .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Shared Endpoints [cite: 18, 19, 20, 21, 22]
                .requestMatchers(HttpMethod.GET, "/api/rooms/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/bookings/**").hasAnyRole("USER", "ADMIN")
                
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
