package com.cast.caspedia.achievement.domain;

import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "user_login_history")
@Data
@NoArgsConstructor
public class UserLoginHistory {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_key")
    private Integer loginKey;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    public UserLoginHistory(User user) {
        this.user = user;
        this.loginAt = LocalDateTime.now(SEOUL_ZONE);
    }
}
