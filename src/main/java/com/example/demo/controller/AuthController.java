package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://hotel-management-frontend-mjbr.onrender.com") 
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest, HttpServletRequest request) {
        // DEBUG: Check what the frontend is sending
        System.out.println("Login attempt for user: " + loginRequest.getUsername());

        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username or Password cannot be empty");
        }

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User foundUser = userOptional.get();
            
            // DEBUG: Check if passwords match
            System.out.println("User found in database. Comparing passwords...");

            if (foundUser.getPassword().equals(loginRequest.getPassword())) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", foundUser.getUsername());
                System.out.println("Login successful for: " + foundUser.getUsername());
                return ResponseEntity.ok("Login successful");
            } else {
                System.out.println("Password mismatch for user: " + foundUser.getUsername());
            }
        } else {
            System.out.println("User not found in database: " + loginRequest.getUsername());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User newUser) {
        System.out.println("Registration attempt for: " + newUser.getUsername());

        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        }
        
        userRepository.save(newUser);
        System.out.println("User registered successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); 
            System.out.println("Session invalidated.");
        }
        return ResponseEntity.ok("Session invalidated successfully");
    }
}