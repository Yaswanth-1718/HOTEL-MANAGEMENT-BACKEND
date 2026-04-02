package com.example.demo.controller;

import com.example.demo.model.Room;
import com.example.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173") 
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    // 1. GET ALL ROOMS
    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // 2. ADD ROOM
    @PostMapping
    public Room addRoom(@RequestBody Room room) {
        // Ensure new rooms default to Available if not specified
        if (room.getStatus() == null) room.setStatus("Available");
        return roomRepository.save(room);
    }

    // 3. UPDATE ROOM (New: Required for Check-Out)
    // Matches React API: API.put(`/rooms/${id}`, data)
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        return roomRepository.findById(id).map(room -> {
            room.setRoomNumber(roomDetails.getRoomNumber());
            room.setType(roomDetails.getType());
            room.setPrice(roomDetails.getPrice());
            room.setStatus(roomDetails.getStatus()); // This flips "Occupied" to "Available"
            
            Room updatedRoom = roomRepository.save(room);
            return ResponseEntity.ok(updatedRoom);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. DELETE ROOM
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomRepository.deleteById(id);
    }
}