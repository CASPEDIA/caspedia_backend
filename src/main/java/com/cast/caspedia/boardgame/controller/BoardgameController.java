package com.cast.caspedia.boardgame.controller;

import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.BoardgameSearchDto;
import com.cast.caspedia.boardgame.service.BGGService;
import com.cast.caspedia.boardgame.service.BoardgameCsvSaveService;
import com.cast.caspedia.boardgame.service.BoardgameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        List<BoardgameSearchDto> result = new ArrayList<>();
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


    //보드게임 좋아요 표시

    //보드게임 좋아요 취소

    //보드게임 좋아요 목록 조회

    //태그 통계

    //평가 목록



}
