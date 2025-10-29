package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LivestreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivestreamApplication.class, args);
		System.out.println("Livestream Application Started");
	}

}
