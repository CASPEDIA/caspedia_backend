package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.domain.BoardgameMechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardgameMechanicRepository extends JpaRepository<BoardgameMechanic, Integer> {

    @Query("SELECT bmk.name " +
            "FROM BoardgameMechanic bm " +
            "JOIN BoardgameMechanicKor bmk ON bm.mechanicId = bmk.mechanicId " +
            "WHERE bm.boardgame = :boardgame")
    List<String> findKoreanMechanicNamesByBoardgame(@Param("boardgame") Boardgame boardgame);

}
