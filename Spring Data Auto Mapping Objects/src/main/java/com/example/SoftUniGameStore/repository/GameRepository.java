package com.example.SoftUniGameStore.repository;

import com.example.SoftUniGameStore.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByTitle(String title);
}
