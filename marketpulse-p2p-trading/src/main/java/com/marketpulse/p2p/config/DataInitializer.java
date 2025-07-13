package com.marketpulse.p2p.config;

import com.marketpulse.p2p.model.User;
import com.marketpulse.p2p.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if we already have users
        if (userRepository.count() == 0) {
            // Create some test users
            User user1 = new User();
            user1.setUsername("user1");
            user1.setEmail("user1@example.com");
            user1.setFullName("Test User 1");
            user1.setStatus("ACTIVE");
            user1.setCreatedAt(LocalDateTime.now());
            user1.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user1);
            
            User user2 = new User();
            user2.setUsername("user2");
            user2.setEmail("user2@example.com");
            user2.setFullName("Test User 2");
            user2.setStatus("ACTIVE");
            user2.setCreatedAt(LocalDateTime.now());
            user2.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user2);
        }
    }
} 