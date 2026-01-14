package com.cast.caspedia.achievement.repository;

import com.cast.caspedia.achievement.domain.UserLoginHistory;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Integer> {
    int countByUser(User user);

    @Query("SELECT COUNT(h) > 0 FROM UserLoginHistory h WHERE h.user = :user AND h.loginAt >= :startOfDay AND h.loginAt < :endOfDay")
    boolean existsByUserAndLoginAtBetween(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
