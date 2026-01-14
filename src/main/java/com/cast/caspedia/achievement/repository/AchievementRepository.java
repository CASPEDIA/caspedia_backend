package com.cast.caspedia.achievement.repository;

import com.cast.caspedia.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Optional<Achievement> findByName(String name);
}
