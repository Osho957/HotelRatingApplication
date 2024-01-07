package com.osho.userservice.service.impl;

import com.osho.userservice.entities.Hotel;
import com.osho.userservice.entities.Rating;
import com.osho.userservice.entities.User;
import com.osho.userservice.exception.ResourceNotFoundException;
import com.osho.userservice.feign.HotelService;
import com.osho.userservice.repository.UserRepository;
import com.osho.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final HotelService hotelService;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found In Database !"));
        //  TODO: use web client here instead of rest template also move urls to config
        String url = "http://RATING-SERVICE/ratings/users/" + user.getUserId();
        Rating[] userRating = restTemplate.getForObject(url, Rating[].class);
        logger.info("ratings {} ", (Object) userRating);
        assert userRating != null;
        List<Rating> ratings = Arrays.stream(userRating).peek(rating -> {
    /*  String hotelApi = "http://HOTEL-SERVICE/hotel/"+rating.getHotelId();
        Hotel hotel = hotelResponseEntity.getBody();
        logger.info("Response status code: {} ",hotelResponseEntity.getStatusCode());
    */
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            rating.setHotel(hotel);
        }).collect(Collectors.toList());
        user.setRatingList(ratings);
        return user;
    }
}
