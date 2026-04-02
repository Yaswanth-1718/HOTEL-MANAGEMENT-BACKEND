package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // This is the most important import for the error

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // If this line is missing, the AuthController will show errors!
    Optional<User> findByUsername(String username);
}