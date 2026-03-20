package com.webprogramming.roomreserve.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webprogramming.roomreserve.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
