package com.example.SoftUniGameStore.service;

import com.example.SoftUniGameStore.model.dto.UserLoginDto;
import com.example.SoftUniGameStore.model.dto.UserRegisterDto;
import com.example.SoftUniGameStore.model.entity.User;

public interface UserService {
    void registerUser(UserRegisterDto userRegisterDto);

    void loginUser(UserLoginDto userLoginDto);

    void logoutUser();

    boolean isUserLoggedIn();

    boolean isUserAdmin();

    User getLoggedInUser();

    void buyItems();
}