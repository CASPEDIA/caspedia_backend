package com.cast.caspedia.rating.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.dashboard.dto.RecentRatedBoardgameResponseDto;
import com.cast.caspedia.dashboard.dto.RecentRatedUserResponseDto;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT r FROM Rating r WHERE r.user = :user")
    List<Rating> findRatingByUser(User user);


    @Query("SELECT COUNT(r) > 0 FROM Rating r WHERE r.user.id = :userId AND r.boardgame.boardgameKey = :boardgameKey")
    boolean existsByUserIdAndBoardgameKey(@Param("userId") String userId, @Param("boardgameKey") Integer boardgameKey);

    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId AND r.boardgame.boardgameKey = :boardgameKey")
    Rating findByUserIdAndBoardgameKey(@Param("userId") String userId, @Param("boardgameKey") Integer boardgameKey);

    @Query("SELECT r.score FROM Rating r WHERE r.boardgame.boardgameKey = :boardgameKey")
    List<Integer> findRatingByBoardgameKey(@Param("boardgameKey") Integer boardgameKey);

    @Query("SELECT r FROM Rating r WHERE r.boardgame = :boardgame")
    List<Rating> findAllRatingByBoardgame(@Param("boardgame") Boardgame boardgame);

    @Query("select new com.cast.caspedia.dashboard.dto.RecentRatedBoardgameResponseDto(r.boardgame.boardgameKey, r.boardgame.nameEng, r.boardgame.nameKor, r.boardgame.imageUrl, max(r.createdAt), max(r.updatedAt)) " +
            "from Rating r " +
            "group by r.boardgame.boardgameKey, r.boardgame.nameEng, r.boardgame.nameKor, r.boardgame.imageUrl " +
            "order by max(r.updatedAt) desc")
    Page<RecentRatedBoardgameResponseDto> findRecentRatedBoardgame(Pageable pageable);


    @Query("select new com.cast.caspedia.dashboard.dto.RecentRatedUserResponseDto(r.user.nanoid, r.user.nickname, r.user.userImage.userImageKey, max(r.createdAt), max(r.updatedAt)) " +
            "from Rating r " +
            "group by r.user.nanoid, r.user.nickname, r.user.userImage.userImageKey " +
            "order by max(r.updatedAt) desc")
    Page<RecentRatedUserResponseDto> findRecentRatedUser(Pageable pageable);

    int countByBoardgame(Boardgame boardgame);
}
