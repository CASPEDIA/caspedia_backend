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

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;


    RatingTag() {}

    public RatingTag(Rating rating, Tag tag) {
        this.rating = rating;
        this.tag = tag;
    }
}
