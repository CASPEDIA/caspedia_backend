package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RatingTagRepository extends JpaRepository<RatingTag, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RatingTag rt WHERE rt.rating = :rating")
    void deleteByRating(Rating rating);
}
