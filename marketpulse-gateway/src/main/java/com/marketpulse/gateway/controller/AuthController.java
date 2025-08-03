package com.marketpulse.gateway.controller;

import com.marketpulse.gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) { 
        if(isValidUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtService.generateToken(loginRequest.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            response.put("message", "Login successful!");
            // Return 200 OK with the token
            return ResponseEntity.ok(response);
        } else {
            // Invalid credentials
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            
            // Return 401 Unauthorized
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        // Extract token
        String token = authHeader.substring(7);
        if (jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("message", "User information retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid token");
            return ResponseEntity.status(401).body(response);
        }
    }

    // Test endpoint to validate a token - GET /auth/validate
    // This is what other services can call to check if a token is valid
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(response);
        }
        
        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        // Validate the token using our JWT service
        if (jwtService.isTokenValid(token)) {
            // Token is valid, extract username
            String username = jwtService.extractUsername(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);
            response.put("message", "Token is valid!");
            
            return ResponseEntity.ok(response);
            
        } else {
            // Token is invalid
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Invalid or expired token");
            
            return ResponseEntity.status(401).body(response);
        }
    }

    // Simple method to check if username/password is valid
    // In a real app, this would check against a database
    private boolean isValidUser(String username, String password) {
        // For testing purposes, accept any username with password "password123"
        // In production, you'd check against a real user database
        return password.equals("password123");
    }
}

// This class represents the login request from frontend
class LoginRequest {
    private String username;
    private String password;
    
    // Getters and setters (required for JSON deserialization)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
