package com.osho.userservice.service;

import com.osho.userservice.entities.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    List<User> getAllUser();
    User getUser(String userId);

    // create delete and update end point
}
