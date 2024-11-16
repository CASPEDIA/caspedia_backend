package com.cast.caspedia.user.domain;

import com.cast.caspedia.boardgame.domain.Boardgame;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"like\"")
@Data
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_key")
    private Integer likeKey;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "boardgame_key", nullable = false)
    private Boardgame boardgame;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Like() {
    }

    public Like(Boardgame boardgame, User user) {
        this.user = user;
        this.boardgame = boardgame;
    }
}
