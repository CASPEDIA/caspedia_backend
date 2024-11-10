package com.cast.caspedia.rating.domain;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating_tag")
@Data
public class RatingTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_tag_key")
    private Integer ratingTagKey;

    @ManyToOne
    @JoinColumn(name = "rating_key", nullable = false)
    private Rating rating;

    @ManyToOne
    @JoinColumn(name = "tag_key", nullable = false)
    private Tag tag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and Setters
}
