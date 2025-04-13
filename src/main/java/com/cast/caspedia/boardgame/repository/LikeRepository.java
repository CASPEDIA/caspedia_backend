package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.user.domain.Like;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.boardgame = :boardgame AND l.user = :user")
    boolean existsByBoardgameAndUser(@Param("boardgame") Boardgame boardgame, @Param("user") User user);

    @Query("SELECT l FROM Like l WHERE l.boardgame = :boardgame AND l.user = :user")
    Like findByBoardgameAndUser(@Param("boardgame") Boardgame boardgame, @Param("user") User user);

    @Query("SELECT l FROM Like l WHERE l.boardgame = :boardgame")
    List<Like> findAllByBoardgame(@Param("boardgame") Boardgame boardgame);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.boardgame = :boardgame")
    int countLikeByBoardgame(@Param("boardgame") Boardgame boardgame);

    @Query("SELECT l FROM Like l WHERE l.user = :user")
    List<Like> findAllByUser(@Param("user") User user);
}
