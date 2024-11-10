package com.cast.caspedia.boardgame.controller;

import com.cast.caspedia.boardgame.service.BoardgameCsvSaveService;
import com.cast.caspedia.boardgame.service.BoardgameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boardgame")
public class BoardgameController {

    @Autowired
    private BoardgameService boardgameService;

    @Autowired
    private BoardgameCsvSaveService boardgameCsvSaveService;

    //BGG API 활용

    //보드게임 가져오기

    //보드게임 저장
    @GetMapping("/save")
    public ResponseEntity<?> saveCsv() {
        boardgameCsvSaveService.importCsvData("./src/main/resources/data/boardgames_ranks_20241110.csv");

        return ResponseEntity.ok("success");
    }


    //======================================================================

    //게임 검색 자동완성
    public ResponseEntity<?> autofill(@RequestParam(name="q")String query) {
     return null;
    }

    //게임 검색 리스트 결과

    //보드게임 상세 페이지

    //보드게임 좋아요 표시 여부 체크


    //보드게임 좋아요 표시

    //보드게임 좋아요 취소

    //보드게임 좋아요 목록 조회

    //태그 통계

    //평가 목록



}
