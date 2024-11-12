package com.cast.caspedia.boardgame.controller;

import com.cast.caspedia.boardgame.service.BGGService;
import com.cast.caspedia.boardgame.service.BoardgameCsvSaveService;
import com.cast.caspedia.boardgame.service.BoardgameService;
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
    @GetMapping("/save")
    public ResponseEntity<?> saveCsv() {
        boardgameCsvSaveService.importCsvData("./src/main/resources/data/boardgames_ranks_20241110.csv");

        return ResponseEntity.ok("success");
    }

    //상세 정보 가져오기
    @GetMapping("/detail")
    public ResponseEntity<?> getDetail() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        bggService.getBoardgameListInfo(list)
                .subscribe(item -> {
                    System.out.println("이미지: " + item.getImage());
                    System.out.println("한글 제목: " + item.getNameKor());
                    System.out.println("설명: " + item.getDescription());
                    System.out.println("최소 플레이어 수: " + item.getMinPlayers());
                    System.out.println("최대 플레이어 수: " + item.getMaxPlayers());
                    System.out.println("최소 플레이 시간: " + item.getMinPlaytime());
                    System.out.println("최대 플레이 시간: " + item.getMaxPlaytime());
                    System.out.println("최소 연령: " + item.getMinAge());
                    System.out.println("평점: " + item.getAverage());
                    System.out.println("난이도: " + item.getAverageWeight());
                });

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
