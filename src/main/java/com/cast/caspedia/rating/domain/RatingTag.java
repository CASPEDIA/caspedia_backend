package com.cast.caspedia.rating.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating_tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    //자동으로 들어가게
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
