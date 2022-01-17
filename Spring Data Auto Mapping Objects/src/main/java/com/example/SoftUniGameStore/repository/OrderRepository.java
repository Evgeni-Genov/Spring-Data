package com.example.SoftUniGameStore.repository;

import com.example.SoftUniGameStore.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
