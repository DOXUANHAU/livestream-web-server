package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LivestreamApplication {

    public static void main(String[] args) {
        System.out.println("Starting server...");
        SpringApplication.run(LivestreamApplication.class, args);
    }

}
