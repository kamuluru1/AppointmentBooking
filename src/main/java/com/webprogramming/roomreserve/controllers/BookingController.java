package com.webprogramming.roomreserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webprogramming.roomreserve.dto.BookingRequest;
import com.webprogramming.roomreserve.entities.Booking;
import com.webprogramming.roomreserve.services.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookRoom(@RequestBody BookingRequest dto) {
        try {
            Booking booking = bookingService.createBooking(
                dto.getUserId(), dto.getRoomId(), dto.getStartTime(), dto.getEndTime()
            );
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        try {
            bookingService.cancelBooking(id, userId);
            return ResponseEntity.ok("Booking canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
