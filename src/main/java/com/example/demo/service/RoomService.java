package com.example.demo.service;

import com.example.demo.model.Room;
import com.example.demo.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repo;

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    // Add Room
    public Room saveRoom(Room room) {
        return repo.save(room);
    }

    // Get All Rooms
    public List<Room> getAllRooms() {
        return repo.findAll();
    }

    // Get Room by ID
    public Room getRoomById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    // Delete Room
    public void deleteRoom(Long id) {
        repo.deleteById(id);
    }
}