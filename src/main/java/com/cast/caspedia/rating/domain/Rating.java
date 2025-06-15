package com.cast.caspedia.rating.domain;


import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rating")
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_key")
    private Integer ratingKey;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "comment", length = 300, nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "boardgame_key", nullable = false)
    private Boardgame boardgame;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @Column(name = "tag_key", nullable = false)
    private String tagKey;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "rating", fetch = FetchType.LAZY)
    private List<RatingTag> ratingTags;

}

