package com.marketpulse.p2p.repository;

import com.marketpulse.p2p.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
} 