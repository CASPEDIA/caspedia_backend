package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.StagingBoardgame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingBoardgameRepository  extends JpaRepository<StagingBoardgame, Integer> {
}
