package com.farmacyfood.fridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FridgeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FridgeServiceApplication.class, args);
    }
}
