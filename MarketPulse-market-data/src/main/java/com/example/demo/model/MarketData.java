package com.example.demo.model;

import lombok.Data;

@Data
public class MarketData {
    private String ticker;
    private double price;
    private long timestamp;

    // Add more fields as per Polygon.io message schema

    // You can add Jackson annotations here if you plan to deserialize JSON to this class
}
