package com.ntnu.idatt2105;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.ntnu.idatt2105"})
public class Idatt2105Application {
	public static void main(String[] args) {
		SpringApplication.run(Idatt2105Application.class, args);
	}

}

