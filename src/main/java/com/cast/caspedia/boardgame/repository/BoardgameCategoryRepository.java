package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.domain.BoardgameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardgameCategoryRepository extends JpaRepository<BoardgameCategory, Integer> {

    @Query("SELECT bck.name " +
            "FROM BoardgameCategory bc " +
            "JOIN BoardgameCategoryKor bck ON bc.categoryId = bck.categoryId " +
            "WHERE bc.boardgame = :boardgame")
    List<String> findKoreanCategoryNamesByBoardgame(@Param("boardgame") Boardgame boardgame);

}
