package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingTag;
import com.cast.caspedia.rating.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RatingTagRepository extends JpaRepository<RatingTag, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RatingTag rt WHERE rt.rating = :rating")
    void deleteByRating(@Param("rating") Rating rating);

    @Query("SELECT rt.tag FROM RatingTag rt WHERE rt.rating = :rating")
    List<Tag> findTagByRating(@Param("rating") Rating rating);

    @Query("""
        SELECT rt.tag.tagKey, COUNT(rt.ratingTagKey)
        FROM RatingTag rt
        JOIN rt.rating r
        WHERE r.boardgame.boardgameKey = :boardgameKey
        GROUP BY rt.tag.tagKey
        ORDER BY rt.tag.tagKey
    """)
    List<Object[]> countTagsByBoardgame(@Param("boardgameKey") int boardgameKey);

    void deleteAllByRating(Rating rating);

    @Query("""
        SELECT b, COUNT(rt)
        FROM RatingTag rt
            JOIN rt.rating r
            JOIN r.boardgame b
        WHERE rt.tag = :tag
        GROUP BY b.boardgameKey
        ORDER BY COUNT(rt) DESC
    """)
    List<Object[]> findBoardgameAndTagCountByTag(@Param("tag") Tag tag);
}
