package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.user.domain.Like;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.boardgame = :boardgame AND l.user = :user")
    boolean existsByBoardgameAndUser(Boardgame boardgame, User user);

    @Query("SELECT l FROM Like l WHERE l.boardgame = :boardgame AND l.user = :user")
    Like findByBoardgameAndUser(Boardgame boardgame, User user);

    @Query("SELECT l FROM Like l WHERE l.boardgame = :boardgame")
    List<Like> findAllByBoardgame(Boardgame boardgame);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.boardgame = :boardgame")
    int countLikeByBoardgame(Boardgame boardgame);

    @Query("SELECT l FROM Like l WHERE l.user = :user")
    List<Like> findAllByUser(User user);
}
