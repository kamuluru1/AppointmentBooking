package com.webprogramming.roomreserve.components;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.webprogramming.roomreserve.entities.User;
import com.webprogramming.roomreserve.repositories.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed an admin user if it doesn't exist
        if (userRepository.findByEmail("admin@roomreserve.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@roomreserve.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            
            System.out.println("====== SYSTEM ADMIN CREATED ======");
            System.out.println("Email: admin@roomreserve.com");
            System.out.println("Password: admin123");
            System.out.println("==================================");
        }
    }
}