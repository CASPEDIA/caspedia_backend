package com.cast.caspedia.boardgame.controller;

import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.BoardgameSearchDto;
import com.cast.caspedia.boardgame.dto.LikeResponseDto;
import com.cast.caspedia.boardgame.service.BGGService;
import com.cast.caspedia.boardgame.service.BoardgameCsvSaveService;
import com.cast.caspedia.boardgame.service.BoardgameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/boardgame")
@Slf4j
public class BoardgameController {

    @Autowired
    private BoardgameService boardgameService;

    @Autowired
    private BoardgameCsvSaveService boardgameCsvSaveService;

    @Autowired
    private BGGService bggService;

    //BGG API 활용

    //보드게임 가져오기

    //scv 저장
//    @GetMapping("/save")
    public ResponseEntity<?> saveCsv() {
        boardgameCsvSaveService.importCsvData("./src/main/resources/data/boardgames_ranks_20241110.csv");

        return ResponseEntity.ok("success");
    }

    //상세 정보 가져오기
    @GetMapping("/detail")
    public ResponseEntity<?> getDetail() throws Exception {
        bggService.detailEnhance();

        return ResponseEntity.ok("success");
    }

    //======================================================================

    //게임 검색 자동완성
    @GetMapping("/autofill")
    public ResponseEntity<?> autofill(@RequestParam(name="q")String query) {
        List<BoardgameAutoFillDto> result = new ArrayList<>();
        log.info("query : {}", query);
        if(query == null) {
            return ResponseEntity.badRequest().build();
        }else if (query.length() > 0) {
            result = boardgameService.autofill(query);
        }
        return ResponseEntity.ok(result);
    }

    //게임 검색 리스트 결과
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name="q")String query, @RequestParam(name="page", defaultValue = "1")int page) {
        BoardgameSearchDto result = new BoardgameSearchDto();
        log.info("query : {}", query);
        if(query == null) {
            return ResponseEntity.badRequest().build();
        }else if (query.length() > 0) {
            result = boardgameService.search(query, page);
        }
        return ResponseEntity.ok(result);
    }

    //보드게임 상세 페이지

    //보드게임 좋아요 표시 여부 체크
    @GetMapping("/like")
    public ResponseEntity<?> checkLike(@RequestParam(name="id")int boardgameKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        boolean result = boardgameService.checkLike(boardgameKey, userId);
        return ResponseEntity.ok(result);
    }

    //보드게임 좋아요 표시
    @PostMapping("/like")
    public ResponseEntity<?> addLike(@RequestParam(name="id")int boardgameKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        boardgameService.addLike(boardgameKey, userId);
        return ResponseEntity.ok().build();
    }
    //보드게임 좋아요 취소
    @DeleteMapping("/like")
    public ResponseEntity<?> deleteLike(@RequestParam(name="id")int boardgameKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        boardgameService.deleteLike(boardgameKey, userId);
        return ResponseEntity.ok().build();
    }

    //보드게임 좋아요 누른사람 목록
    @GetMapping("/likelist")
    public ResponseEntity<?> getLikeList(@RequestParam(name="id")int boardgameKey) {
        List<LikeResponseDto> result = boardgameService.getLikeList(boardgameKey);
        return ResponseEntity.ok(result);
    }

    //태그 통계

    //평가 목록



}
