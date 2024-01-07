package com.osho.userservice;

import com.osho.userservice.entities.Rating;
import com.osho.userservice.feign.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	private RatingService ratingService;

	@Test
	void contextLoads() {
	}

	/*
	   test to check if feign client for rating service is working or not
	 */
	@Test
	void createRating(){
		Rating rating = Rating.builder()
				.rating(9)
				.userId("userid")
				.hotelId("hotel id")
				.feedback("Testing feign client")
				.build();
		ratingService.createRating(rating);
	}
}
