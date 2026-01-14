package com.cast.caspedia.achievement.repository;

import com.cast.caspedia.achievement.domain.UserAchievement;
import com.cast.caspedia.achievement.domain.UserAchievementId;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UserAchievementId> {
    List<UserAchievement> findByUser(User user);
}
