package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.domain.BoardgameMechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardgameMechanicRepository extends JpaRepository<BoardgameMechanic, Integer> {

    @Query("SELECT bm.mechanicValue FROM BoardgameMechanic bm WHERE bm.boardgame = :boardgame")
    List<String> findMechanicByBoardgame(Boardgame boardgame);
}
