package com.example.pet_care_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PetCareBookingApplication {

	public static void main(String[] args) {

		SpringApplication.run(PetCareBookingApplication.class, args);
	}

}
