package com.example.SoftUniGameStore.service.impl;

import com.example.SoftUniGameStore.model.dto.UserLoginDto;
import com.example.SoftUniGameStore.model.dto.UserRegisterDto;
import com.example.SoftUniGameStore.model.entity.User;
import com.example.SoftUniGameStore.repository.UserRepository;
import com.example.SoftUniGameStore.service.UserService;
import com.example.SoftUniGameStore.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private User loggedInUser;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public void registerUser(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            System.out.println("Passwords don't match.");
            return;
        }

        if (!checkIsDataValid(userRegisterDto)) {
            return;
        }

        User user = modelMapper.map(userRegisterDto, User.class);

        if (this.userRepository.count() == 0) {
            user.setAdmin(true);
        }

        this.userRepository.save(user);

        System.out.println(user.getFullName() + " was registered");
    }

    @Override
    public void loginUser(UserLoginDto userLoginDto) {
        if (!checkIsDataValid(userLoginDto)) {
            return;
        }

        User user =
                this.userRepository
                        .findByEmailAndPassword(userLoginDto.getEmail(),
                                userLoginDto.getPassword()).orElse(null);

        if (user == null) {
            System.out.println("Incorrect username / password");

            return;
        }

        System.out.println("Successfully logged in " + user.getFullName());
        loggedInUser = user;
    }

    @Override
    public void logoutUser() {
        if (this.loggedInUser == null) {
            System.out.println("Cannot log out. No user was logged in.");

        } else {
            System.out.printf("User %s successfully logged out%n", this.loggedInUser.getFullName());
            loggedInUser = null;
        }
    }

    @Override
    public boolean isUserLoggedIn() {
        if (this.loggedInUser == null) {
            System.out.println("User not logged in");

            return false;
        }

        return true;
    }

    @Override
    public boolean isUserAdmin() {
        if (!this.loggedInUser.isAdmin()) {
            System.out.println("Logged in user is not an admin.");

            return false;
        }

        return true;
    }

    @Override
    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    @Override
    public void buyItems() {
        System.out.println("Successfully bought games:");
        this.loggedInUser.getOrder().getProducts()
                .forEach(game -> {
                    this.loggedInUser.getGames().add(game);
                    System.out.println(" -" + game.getTitle());
                });

        this.loggedInUser.setOrder(null);
        this.userRepository.save(this.loggedInUser);
    }

    private <T> boolean checkIsDataValid(T dto) {
        Set<ConstraintViolation<T>> violation = validationUtil.getViolations(dto);

        if (!violation.isEmpty()) {
            violation.stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            return false;
        }

        return true;
    }
}
