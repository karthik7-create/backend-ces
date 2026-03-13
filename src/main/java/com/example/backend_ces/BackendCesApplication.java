package com.example.backend_ces;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendCesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendCesApplication.class, args);
	}

}
