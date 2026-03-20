package com.webprogramming.roomreserve.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.webprogramming.roomreserve.entities.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
