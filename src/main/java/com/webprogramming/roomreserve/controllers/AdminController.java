package com.webprogramming.roomreserve.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webprogramming.roomreserve.repositories.BookingRepository;
import com.webprogramming.roomreserve.repositories.RoomRepository;
import com.webprogramming.roomreserve.repositories.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalRooms", roomRepository.count());
        stats.put("totalUsers", userRepository.count());
        
        // You can expand this with custom @Query methods in your repositories 
        // to get specific trends (e.g., bookings from the last 7 days)
        
        return ResponseEntity.ok(stats);
    }
}
