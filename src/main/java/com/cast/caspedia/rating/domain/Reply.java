package com.cast.caspedia.rating.domain;

import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Data
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_key")
    private Integer replyKey;

    @ManyToOne
    @JoinColumn(name = "rating_key", nullable = false)
    private Rating rating;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
