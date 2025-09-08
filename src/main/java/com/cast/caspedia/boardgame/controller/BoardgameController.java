package com.cast.caspedia.boardgame.controller;

import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.BoardgameSearchDto;
import com.cast.caspedia.boardgame.dto.LikeResponseDto;
import com.cast.caspedia.boardgame.service.BggFetcherService;
import com.cast.caspedia.boardgame.service.BggIntegrationService;
import com.cast.caspedia.boardgame.service.BoardgameService;
import com.cast.caspedia.error.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private BggFetcherService bggFetcherService;

    @Autowired
    private BggIntegrationService bggIntegrationService;

    //BGG API 활용

    //보드게임 가져오기

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchBoardgames() {
        bggFetcherService.fetchAllGames();
        return ResponseEntity.ok("보드게임 데이터를 성공적으로 가져왔습니다.");
    }

    @GetMapping("/integrateData")
    public ResponseEntity<?> integrateData() {
        bggIntegrationService.integrateData();
        return ResponseEntity.ok("보드게임 데이터를 성공적으로 통합했습니다.");
    }


    //======================================================================

    //게임 검색 자동완성
    @GetMapping("/autofill")
    public ResponseEntity<?> autofill(@RequestParam(name="q", required = false)String query) {
        List<BoardgameAutoFillDto> result = new ArrayList<>();
        log.info("query : {}", query);
        if(query == null) {
            throw new AppException("query가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }else if (query.length() > 0) {
            result = boardgameService.autofill(query);
        }
        return ResponseEntity.ok(result);
    }

    //게임 검색 리스트 결과
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name="q", required = false)String query, @RequestParam(name="page", defaultValue = "1")int page) {
        BoardgameSearchDto result = new BoardgameSearchDto();
        log.info("query : {}", query);
        if(query == null) {
            throw new AppException("query가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }else if (query.length() > 0) {
            result = boardgameService.search(query, page);
        }
        return ResponseEntity.ok(result);
    }

    //보드게임 상세 페이지
    @GetMapping("/basicinfo")
    public ResponseEntity<?> getBasicInfo(@RequestParam(name="id", required = false)int boardgameKey) {
        if(boardgameKey == 0) {
            throw new AppException("id가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(boardgameService.getBasicInfo(boardgameKey));
    }

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
    @GetMapping("/tag")
    public ResponseEntity<?> getTagCounts(@RequestParam(name="id")int boardgameKey) {

        return ResponseEntity.ok(boardgameService.getTagCounts(boardgameKey));
    }

    //평가 목록
    @GetMapping("/rating")
    public ResponseEntity<?> getRatingList(@RequestParam(name="id")int boardgameKey) {
        return ResponseEntity.ok(boardgameService.getRatingList(boardgameKey));
    }

    //보드게임 탐방
    /**
     | **자료형** | **파라미터 명** | 의미 | **필수여부** | **제약사항** | 비고 |
     | --- | --- | --- | --- | --- | --- |
     | int | page | 페이지 | O | 1이상, 자연수 |  |
     | int | minp | 최소 인원 수 | O | 1이상, 자연수 | 1,2,3,4,5,6,7,8,9 |
     | int | maxp | 최대 인원 수 | O | 9이하, 자연수 | 1,2,3,4,5,6,7,8,9 |
     | int | mint | 최소 플레이 타임 | O | 10이상, 자연수 | 10,30,60,90,120,180 |
     | int | maxt | 최대 플레이 타임 | O | 180이하, 자연수 | 10,30,60,90,120,180 |
     | int | ming | 최소 긱 웨이트 | O | 1이상, 자연수 | 1,2,3,4,5 |
     | int | maxg | 최대 긱 웨이트 | O | 5이하, 자연수 | 1,2,3,4,5 |
     | str | sort | 정렬 | O | castdesc, castasc, likedesc, likeasc |  |
     */
    @GetMapping("/explore")
    public ResponseEntity<?> exploreBoardgames(@RequestParam(name="page", defaultValue = "1") int page,
                                               @RequestParam(name="minp", defaultValue = "1") int minPlayers,
                                               @RequestParam(name="maxp", defaultValue = "9") int maxPlayers,
                                               @RequestParam(name="mint", defaultValue = "10") int minPlayTime,
                                               @RequestParam(name="maxt", defaultValue = "180") int maxPlayTime,
                                               @RequestParam(name="ming", defaultValue = "1") int minGeekWeight,
                                               @RequestParam(name="maxg", defaultValue = "5") int maxGeekWeight,
                                               @RequestParam(name="sort", defaultValue = "castdesc") String sort) {

        // 유효성 검사

        //페이지 번호 검사
        if(page < 1) {
            throw new AppException("page는 1 이상의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }

        //정렬 방식 검사
        if(!sort.equals("castdesc") && !sort.equals("castasc") && !sort.equals("likedesc") && !sort.equals("likeasc")) {
            throw new AppException("sort는 castdesc, castasc, likedesc, likeasc 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);
        }

        //플레이어 수 검사
        switch (minPlayers) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                break;
            default:
                throw new AppException("minp는 1에서 9 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        switch (maxPlayers) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                break;
            default:
                throw new AppException("maxp는 1에서 9 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if(minPlayers > maxPlayers) {
            throw new AppException("minp는 maxp보다 작거나 같아야 합니다.", HttpStatus.BAD_REQUEST);
        }

        //플레이 타임 검사
        switch (minPlayTime) {
            case 10: case 30: case 60: case 90: case 120: case 180:
                break;
            default:
                throw new AppException("mint는 10, 30, 60, 90, 120, 180 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        switch (maxPlayTime) {
            case 10: case 30: case 60: case 90: case 120: case 180:
                break;
            default:
                throw new AppException("maxt는 10, 30, 60, 90, 120, 180 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if(minPlayTime > maxPlayTime) {
            throw new AppException("mint는 maxt보다 작거나 같아야 합니다.", HttpStatus.BAD_REQUEST);
        }

        //긱 웨이트 검사
        switch (minGeekWeight) {
            case 1: case 2: case 3: case 4: case 5:
                break;
            default:
                throw new AppException("ming는 1에서 5 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        switch (maxGeekWeight) {
            case 1: case 2: case 3: case 4: case 5:
                break;
            default:
                throw new AppException("maxg는 1에서 5 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if(minGeekWeight > maxGeekWeight) {
            throw new AppException("ming는 maxg보다 작거나 같아야 합니다.", HttpStatus.BAD_REQUEST);
        }


        return ResponseEntity.ok(boardgameService.exploreBoardgames(
                page, minPlayers, maxPlayers, minPlayTime, maxPlayTime, minGeekWeight, maxGeekWeight, sort));
    }


}
