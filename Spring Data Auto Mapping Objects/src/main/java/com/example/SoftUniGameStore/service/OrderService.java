package com.example.SoftUniGameStore.service;

import com.example.SoftUniGameStore.model.dto.GameViewDto;

public interface OrderService {
    void addItem(GameViewDto gameViewDto);

    void removeItem(GameViewDto gameViewDto);
}

