
package com.server.uidesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UiDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiDeskApplication.class, args);
	}

}
