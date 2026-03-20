package com.webprogramming.roomreserve.repositories;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webprogramming.roomreserve.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId); // View own bookings [cite: 21]
    
    // Validates overlapping times to prevent double-booking [cite: 49, 50]
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status = 'booked' " +
           "AND (b.startTime < :endTime AND b.endTime > :startTime)")
    int countConflictingBookings(@Param("roomId") Long roomId, 
                                 @Param("startTime") LocalDateTime startTime, 
                                 @Param("endTime") LocalDateTime endTime);
}
