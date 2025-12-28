package com.paypal.paypal_user_service.service;

import com.paypal.paypal_user_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
}
