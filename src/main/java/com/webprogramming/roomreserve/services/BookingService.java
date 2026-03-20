package com.webprogramming.roomreserve.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webprogramming.roomreserve.entities.Booking;
import com.webprogramming.roomreserve.entities.Room;
import com.webprogramming.roomreserve.entities.User;
import com.webprogramming.roomreserve.repositories.BookingRepository;
import com.webprogramming.roomreserve.repositories.RoomRepository;
import com.webprogramming.roomreserve.repositories.UserRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Booking createBooking(Long userId, Long roomId, LocalDateTime start, LocalDateTime end) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        int conflicts = bookingRepository.countConflictingBookings(roomId, start, end);
        if (conflicts > 0) {
            throw new IllegalStateException("Time slot is already booked.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus("booked");
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
            
        if (!booking.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized cancellation attempt");
        }

        booking.setStatus("canceled");
        bookingRepository.save(booking);
    }
}
