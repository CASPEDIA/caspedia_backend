package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BoardgameRepository extends JpaRepository<Boardgame, Integer> {

    //보드게임 키로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE Boardgame b SET b.nameEng = :nameEng, " +
            "b.nameKor = :nameKor, " +
            "b.yearPublished = :yearPublished, " +
            "b.minPlayers = :minPlayers, " +
            "b.maxPlayers = :maxPlayers, " +
            "b.minPlaytime = :minPlaytime, " +
            "b.maxPlaytime = :maxPlaytime, " +
            "b.age = :age, " +
            "b.description = :description, " +
            "b.imageUrl = :imageUrl, " +
            "b.updatedAt = :updatedAt, " +
            "b.geekWeight = :geekWeight, " +
            "b.geekScore = :geekScore " +
            "WHERE b.boardgameKey = :boardgameKey")
    int updateBoardgame(Boardgame boardgame);

    //보드게임 페이지와 함께 정렬
    @Query("SELECT b FROM Boardgame b ORDER BY b.boardgameKey ASC")
    Page<Boardgame> getBoardgameList(Pageable pageable);


    //보드게임 이름으로 검색 auto fill
    @Query("SELECT new com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto(b.boardgameKey, b.nameEng, b.yearPublished) " +
            "FROM Boardgame b WHERE b.nameEng LIKE %:query%")
    Page<BoardgameAutoFillDto> autofillEng(String query, Pageable pageable);

    @Query("SELECT new com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto(b.boardgameKey, b.nameKor, b.yearPublished) " +
            "FROM Boardgame b WHERE b.nameKor LIKE %:query%")
    Page<BoardgameAutoFillDto> autofillKor(String query, Pageable pageable);

//    @Query("SELECT new com.cast.caspedia.boardgame.dto.BoardgameSearchDto.Data(b.boardgameKey, b.imageUrl, b.nameKor, b.nameEng, b.likes, b.geekScore, b.castScore) " +
//            "FROM Boardgame b " +
//            "join rating r ON b.boardgameKey = r.boardgameKey " +
//            "join " +
//            " b.nameEng LIKE %:query% OR b.nameKor LIKE %:query%")
//    Page<BoardgameSearchDto.Data> search(String query, Pageable pageable);

}
