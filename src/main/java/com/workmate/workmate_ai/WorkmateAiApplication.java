package com.workmate.workmate_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WorkmateAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkmateAiApplication.class, args);
	}
}