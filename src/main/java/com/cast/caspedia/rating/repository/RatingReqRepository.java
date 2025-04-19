package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.RatingReq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RatingReqRepository extends JpaRepository<RatingReq, Integer> {

    boolean existsByBoardgame_BoardgameKeyAndCreatedAtAfter(
            int boardgameKey,
            LocalDateTime limitDate
    );

    List<RatingReq> findAllByCreatedAtAfter(
            LocalDateTime limitDate
    );
}
