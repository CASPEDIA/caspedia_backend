package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.dto.RatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    /*
    private Integer ratingKey;
    private Integer score;
    private String comment;
    private Integer boardgameKey;
    private String nanoid;
    private String nameKor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
     */
    @Query("SELECT new com.cast.caspedia.rating.dto.RatingDto(r.ratingKey, r.score, r.comment, r.boardgame.boardgameKey, u.nanoid, b.nameKor, r.createdAt, r.updatedAt) " +
            "FROM Rating r JOIN r.user u JOIN r.boardgame b " +
            "WHERE u.nanoid = :nanoid")
    List<RatingDto> findByNanoId(@Param("nanoid") String nanoid);


    @Query("SELECT COUNT(r) > 0 FROM Rating r WHERE r.user.id = :userId AND r.boardgame.boardgameKey = :boardgameKey")
    boolean existsByUserIdAndBoardgameKey(@Param("userId") String userId, @Param("boardgameKey") Integer boardgameKey);

    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId AND r.boardgame.boardgameKey = :boardgameKey")
    Rating findByUserIdAndBoardgameKey(@Param("userId") String userId, @Param("boardgameKey") Integer boardgameKey);

    @Query("SELECT r.score FROM Rating r WHERE r.boardgame.boardgameKey = :boardgameKey")
    List<Integer> findRatingByBoardgameKey(Integer boardgameKey);

}
