package com.urbaneats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Required to enable scheduling
public class UrbanEatsDeliveryAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrbanEatsDeliveryAppApplication.class, args);
	}

}
