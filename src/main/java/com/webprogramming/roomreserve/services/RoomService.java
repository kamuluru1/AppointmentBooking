package com.webprogramming.roomreserve.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webprogramming.roomreserve.entities.Room;
import com.webprogramming.roomreserve.repositories.RoomRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room createRoom(Room room) {
        room.setCreatedAt(LocalDateTime.now());
        if (room.getStatus() == null) {
            room.setStatus("available");
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = getRoomById(id);
        room.setName(roomDetails.getName());
        room.setType(roomDetails.getType());
        room.setCapacity(roomDetails.getCapacity());
        room.setLocation(roomDetails.getLocation());
        room.setStatus(roomDetails.getStatus());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
