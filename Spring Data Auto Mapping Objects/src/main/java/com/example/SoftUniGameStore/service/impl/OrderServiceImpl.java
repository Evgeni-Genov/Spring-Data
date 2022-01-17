package com.example.SoftUniGameStore.service.impl;

import com.example.SoftUniGameStore.model.dto.GameViewDto;
import com.example.SoftUniGameStore.model.entity.Game;
import com.example.SoftUniGameStore.model.entity.Order;
import com.example.SoftUniGameStore.model.entity.User;
import com.example.SoftUniGameStore.repository.GameRepository;
import com.example.SoftUniGameStore.repository.OrderRepository;
import com.example.SoftUniGameStore.service.OrderService;
import com.example.SoftUniGameStore.service.UserService;
import com.example.SoftUniGameStore.util.ValidationUtil;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
    private final ValidationUtil validationUtil;
    private final UserService userService;
    private final GameRepository gameRepository;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(ValidationUtil validationUtil, UserService userService, GameRepository gameRepository, OrderRepository orderRepository) {
        this.validationUtil = validationUtil;
        this.userService = userService;
        this.gameRepository = gameRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void addItem(GameViewDto gameViewDto) {
        if (!checkIsDataValid(gameViewDto)) {
            return;
        }

        if (!this.userService.isUserLoggedIn()) {
            return;
        }

        User loggedInUser = this.userService.getLoggedInUser();

        boolean isGameAlreadyBought =
                loggedInUser
                        .getGames()
                        .stream()
                        .anyMatch(game -> game.getTitle().equals(gameViewDto.getTitle()));

        if (isGameAlreadyBought) {
            System.out.println("Game is already bought.");

            return;
        }

        Game game = this.gameRepository.findByTitle(gameViewDto.getTitle());

        Order order;

        if (loggedInUser.getOrder() == null) {
            order = new Order();
            order.setBuyer(loggedInUser);

        } else {
            order = loggedInUser.getOrder();
        }

        order.getProducts().add(game);
        this.orderRepository.save(order);
        loggedInUser.setOrder(order);

        System.out.println(gameViewDto.getTitle() + " added to cart.");
    }

    @Override
    public void removeItem(GameViewDto gameViewDto) {
        if (!checkIsDataValid(gameViewDto)) {
            return;
        }

        if (!this.userService.isUserLoggedIn()) {
            return;
        }

        User loggedInUser = this.userService.getLoggedInUser();
        Order order = loggedInUser.getOrder();
        List<Game> products = order.getProducts();
        Game game =
                products
                        .stream()
                        .filter(p -> p.getTitle().equals(gameViewDto.getTitle()))
                        .findAny().orElse(null);

        products.remove(game);

        this.orderRepository.save(order);

        if (order.getProducts().isEmpty()) {
            loggedInUser.setOrder(null);
            this.orderRepository.delete(order);
        }

        System.out.println(game.getTitle() + " removed from cart.");
    }

    private <T> boolean checkIsDataValid(T dto) {
        Set<ConstraintViolation<T>> violation = validationUtil.getViolations(dto);
        if (!violation.isEmpty()) {
            violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            return false;
        }

        return true;
    }
}