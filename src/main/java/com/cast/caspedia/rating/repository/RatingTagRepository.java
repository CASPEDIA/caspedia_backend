package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.RatingTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingTagRepository extends JpaRepository<RatingTag, Integer> {
}
