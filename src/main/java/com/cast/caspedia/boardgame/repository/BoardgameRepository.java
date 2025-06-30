package com.cast.caspedia.boardgame.repository;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.ExploreDefaultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
            "FROM Boardgame b " +
            "WHERE LOWER(REPLACE(b.nameEng, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :query, '%'), ' ', '')) " +
            "ORDER BY b.boardgameKey")
    Page<BoardgameAutoFillDto> autofillEng(@Param("query") String query, Pageable pageable);


    @Query("SELECT new com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto(b.boardgameKey, b.nameKor, b.yearPublished) " +
            "FROM Boardgame b " +
            "WHERE REPLACE(b.nameKor, ' ', '') LIKE REPLACE(CONCAT('%', :query, '%'), ' ', '') " +
            "order by b.boardgameKey")
    Page<BoardgameAutoFillDto> autofillKor(@Param("query") String query, Pageable pageable);


    //보드게임 이름으로 검색
    @Query("SELECT b " +
            "FROM Boardgame b " +
            "WHERE LOWER(REPLACE(b.nameEng, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :query, '%'), ' ', '')) " +
            "OR REPLACE(b.nameKor, ' ', '') LIKE REPLACE(CONCAT('%', :query, '%'), ' ', '') " +
            "ORDER BY b.boardgameKey")
    Page<Boardgame> search(@Param("query") String query, Pageable pageable);


    //보드게임 랭킹순 조회
    List<Boardgame> findAllByOrderByCastScoreDesc(Pageable pageable);

    //보드게임 리뷰 갯수순 조회 (period)
    @Query("""
        SELECT b
        FROM Boardgame b, Rating r
        WHERE r.boardgame = b
          AND r.createdAt >= :since
        GROUP BY b
        ORDER BY COUNT(r) DESC
    """)
    List<Boardgame> findTopByPeriodRatingCount(
            @Param("since") LocalDateTime since,
            Pageable pageable
    );

    //보드게임 리뷰 갯수순 조회
    @Query("""
        SELECT b
        FROM Boardgame b, Rating r
        WHERE r.boardgame = b
        GROUP BY b
        ORDER BY COUNT(r) DESC
    """)
    List<Boardgame> findTopByRatingCount(
            Pageable pageable
    );

    @Query(value = """
    SELECT new com.cast.caspedia.boardgame.dto.ExploreDefaultDto(
      b.boardgameKey,
      b.castScore,
      b.geekWeight,
      b.imageUrl,
      (SELECT COUNT(lg) FROM Like lg WHERE lg.boardgame = b),
      b.nameEng,
      b.nameKor
    )
    FROM Boardgame b
    WHERE (b.nameKor <> '' OR EXISTS(
            SELECT 1 FROM Rating r WHERE r.boardgame = b))
      AND b.minPlayers   >= :minPlayers
      AND b.maxPlayers   <= :maxPlayers
      AND b.minPlaytime  >= :minPlayTime
      AND b.maxPlaytime  <= :maxPlayTime
      AND b.geekWeight   BETWEEN :minGeekWeight AND :maxGeekWeight
  """,
            countQuery = """
    SELECT COUNT(b)
    FROM Boardgame b
    WHERE (b.nameKor <> '' OR EXISTS(
            SELECT 1 FROM Rating r WHERE r.boardgame = b))
      AND b.minPlayers   >= :minPlayers
      AND b.maxPlayers   <= :maxPlayers
      AND b.minPlaytime  >= :minPlayTime
      AND b.maxPlaytime  <= :maxPlayTime
      AND b.geekWeight   BETWEEN :minGeekWeight AND :maxGeekWeight
  """)
    Page<ExploreDefaultDto> findExploreDefault(
            int minPlayers, int maxPlayers,
            int minPlayTime, int maxPlayTime,
            int minGeekWeight, int maxGeekWeight,
            Pageable pageable
    );
}