package com.webprogramming.roomreserve.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webprogramming.roomreserve.dto.LoginDto;
import com.webprogramming.roomreserve.dto.RegisterDto;
import com.webprogramming.roomreserve.entities.User;
import com.webprogramming.roomreserve.repositories.UserRepository;
import com.webprogramming.roomreserve.security_config.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Instantiate the repository used to save the security context
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        if (registerDto.getRole() != null && registerDto.getRole().equalsIgnoreCase("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    // Note the addition of HttpServletResponse here
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
                
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginDto.getEmail());
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Explicitly create, set, and save the SecurityContext
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                securityContextRepository.saveContext(context, request, response);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", "Login successful");
                responseBody.put("userId", user.getId());
                responseBody.put("name", user.getName());
                responseBody.put("role", user.getRole());
                
                return ResponseEntity.ok(responseBody);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
