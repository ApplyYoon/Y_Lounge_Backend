package com.example.demo.controller;

import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")

public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Autowired
    private com.example.demo.util.JwtUtil jwtUtil; // Use full class name to avoid import conflict if any

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body,
            @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null || !jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            return ResponseEntity.status(401).body("Unauthorized: Please login to create a room");
        }
        String username = jwtUtil.extractUsername(token);

        String name = body.get("name");
        String type = body.get("type"); // "VOICE" or "TEXT"

        if (roomRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        // Pass the host username to the constructor
        Room newRoom = new Room(name, type != null ? type : "VOICE", username);
        Room savedRoom = roomRepository.save(newRoom);
        return ResponseEntity.ok(savedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id,
            @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null || !jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String requestingUser = jwtUtil.extractUsername(token);

        return roomRepository.findById(id).map(room -> {
            if (!requestingUser.equals(room.getHostUsername())) {
                return ResponseEntity.status(403).body("Forbidden: Only the host can delete this room");
            }
            roomRepository.delete(room);
            return ResponseEntity.ok("Room deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Map<String, String> body,
            @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null || !jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String requestingUser = jwtUtil.extractUsername(token);
        String newName = body.get("name");

        return roomRepository.findById(id).map(room -> {
            if (!requestingUser.equals(room.getHostUsername())) {
                return ResponseEntity.status(403).body("Forbidden: Only the host can edit this room");
            }
            room.setName(newName);
            roomRepository.save(room);
            return ResponseEntity.ok(room);
        }).orElse(ResponseEntity.notFound().build());
    }
}
