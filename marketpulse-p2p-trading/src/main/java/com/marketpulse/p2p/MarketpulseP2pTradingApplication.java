package com.marketpulse.p2p;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketpulseP2pTradingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketpulseP2pTradingApplication.class, args);
    }
}