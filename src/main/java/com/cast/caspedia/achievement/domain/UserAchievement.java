package com.cast.caspedia.achievement.domain;

import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievement")
@Data
@NoArgsConstructor
@IdClass(UserAchievementId.class)
public class UserAchievement {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "achievement_key", nullable = false)
    private Achievement achievement;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt = LocalDateTime.now();

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.achievedAt = LocalDateTime.now();
    }
}
