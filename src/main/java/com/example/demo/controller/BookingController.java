package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import jakarta.servlet.http.HttpServletRequest; // NEW
import jakarta.servlet.http.HttpSession;      // NEW
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173") 
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    // 1. GET ALL
    @GetMapping
    public List<Booking> getAll() { 
        return bookingService.findAll(); 
    }

    // 2. CREATE
    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody Booking b) {
        try {
            if (b.getStatus() == null) b.setStatus("Confirmed");
            Booking savedBooking = bookingService.saveBooking(b);
            return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. UPDATE / CHECK OUT
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        try {
            Booking updated = bookingService.updateBookingStatus(id, bookingDetails.getStatus());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 4. DELETE (Cancellations)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            bookingService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: LOGOUT HANDLER
    // This will be called from your React Navbar.jsx
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // getSession(false) prevents creating a new session if one doesn't exist
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Destroys the session on the server
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}