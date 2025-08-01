package com.marketpulse.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketpulseGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketpulseGatewayApplication.class, args);
	}

}
