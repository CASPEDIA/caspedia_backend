package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingImpressed;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingImpressedRepository extends JpaRepository<RatingImpressed, Integer> {

    boolean existsByUserAndRating(User user, Rating rating);

    void deleteByUserAndRating(User user, Rating rating);

    Optional<RatingImpressed> findByUserAndRating(User user, Rating rating);

    int countByRating(Rating rating);

    void deleteAllByRating(Rating rating);
}
