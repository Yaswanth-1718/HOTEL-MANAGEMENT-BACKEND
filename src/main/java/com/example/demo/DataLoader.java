package com.example.demo;

import com.example.demo.model.Room;
import com.example.demo.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDatabase(RoomRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Room("101", "Single", 1000.0, "Available"));
                repository.save(new Room("102", "Double", 1500.0, "Available"));
                repository.save(new Room("103", "Deluxe", 2000.0, "Available"));
                repository.save(new Room("104", "Suite", 3000.0, "Available"));
                repository.save(new Room("105", "Luxury", 5000.0, "Available"));
                System.out.println("✅ Default Rooms Loaded");
            }
        };
    }
}