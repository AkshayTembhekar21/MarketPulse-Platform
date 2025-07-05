package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {

    @GetMapping("/")
    public String home() {
        return "Polygon Stream Service is running.";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
