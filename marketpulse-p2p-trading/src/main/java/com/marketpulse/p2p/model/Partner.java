package com.marketpulse.p2p.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    @Id
    private Long id;
    private Long userId;
    private Long partnerId;
    private String status; // e.g., 'active', 'pending', 'blocked'
} 