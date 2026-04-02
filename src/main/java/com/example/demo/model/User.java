package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "app_users") // 'users' is a reserved keyword in some SQL versions; 'app_users' is safer
@Data
@NoArgsConstructor  // Necessary for JPA to create the entity from the database
@AllArgsConstructor // Useful for creating users in your tests or DataLoader
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;

 // Inside User.java, if @Data fails, add these manually:
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}