package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.domain.BoardgameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardgameCategoryRepository extends JpaRepository<BoardgameCategory, Integer> {

    @Query("SELECT bc.categoryValue FROM BoardgameCategory bc WHERE bc.boardgame = :boardgame")
    List<String> findCategoryByBoardgame(Boardgame boardgame);
}
