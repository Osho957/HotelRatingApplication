package com.osho.userservice.controller;

import com.osho.userservice.entities.User;
import com.osho.userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final  UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    // TODO: modify with dto class after completion
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    int retryCount = 1;
    @GetMapping("/{userId}")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallBack")
//    @Retry(name = "ratingHotelService",fallbackMethod = "ratingHotelFallBack")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallBack")
    public ResponseEntity<User> getSingleUser(@PathVariable String userId){
        logger.info("retry count is {}",retryCount);
        retryCount++;
        User user = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /*
          the return type should be similar as of the method from which the circuit breaker fallback is called
     */

    public ResponseEntity<User> ratingHotelFallBack(String userId, Exception exception){
//        logger.info("Fallback is executed because service is down :  {} ", exception.getMessage());
        User user = User.builder()
                .userId("faulty_user_989")
                .name("Faulty User")
                .email("faulty@gmail.com")
                .about("Returning this because some services are down")
                .build();
        return new ResponseEntity<>(user,HttpStatus.BAD_REQUEST);
    }

/*
      TODO : UPDATE AND DELETE ENDPOINTS
 */
}
