package com.cast.caspedia.rating.domain;

import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rating_impressed")
@Data
public class RatingImpressed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_impressed_key")
    private Integer ratingImpressedKey;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "rating_key", nullable = false)
    private Rating rating;
}
