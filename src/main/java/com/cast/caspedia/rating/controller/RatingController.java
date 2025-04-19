package com.cast.caspedia.rating.controller;

import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.dto.RatingRequestDto;
import com.cast.caspedia.rating.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rating")
@Slf4j
public class RatingController {

    ObjectMapper objectMapper;
    RatingService ratingService;

    public RatingController(RatingService ratingService, ObjectMapper objectMapper) {
        this.ratingService = ratingService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{boardgamekey}")
    public ResponseEntity<?> addRating(@RequestBody Map<String, Object> param, @PathVariable Integer boardgamekey){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        //태그와 점수는 필수 값
        if(param.containsKey("score") && param.containsKey("tag_key")) {
            RatingRequestDto ratingRequestDto = new RatingRequestDto();
            ratingRequestDto.setScore((Integer) param.get("score"));
            ratingRequestDto.setComment((String) param.getOrDefault("comment", ""));
            ratingRequestDto.setBoardgameKey(boardgamekey);
            ratingRequestDto.setUserId(userId);
            ratingRequestDto.setTags((String)param.get("tag_key"));

            //한줄평 길이 확인
            if(ratingRequestDto.getComment().length() > 300) {
                throw new AppException("한줄평은 300자 이하여야 합니다.", HttpStatus.BAD_REQUEST);
            }

            int tagCnt = 0;
            if(ratingRequestDto.getTags().length() != 24) {
                throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
            }
            for(int i = 0; i < ratingRequestDto.getTags().length(); i++) {
                if(ratingRequestDto.getTags().charAt(i) == '1') {
                    tagCnt++;
                    if(tagCnt > 5) {
                        throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 평점이 1~10 사이의 값인지 확인
            if(ratingRequestDto.getScore() < 1 || ratingRequestDto.getScore() > 10) {
                throw new AppException("평점은 1~10 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
            }
            ratingService.addRating(ratingRequestDto);
            return ResponseEntity.ok().build();
        }else {
            throw new AppException("필수 정보가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{boardgamekey}")
    public ResponseEntity<?> updateRating(@RequestBody Map<String, Object> param, @PathVariable Integer boardgamekey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if(param.containsKey("score") && param.containsKey("tag_key")) {
            RatingRequestDto ratingRequestDto = new RatingRequestDto();
            ratingRequestDto.setScore((Integer) param.get("score"));
            ratingRequestDto.setComment((String) param.getOrDefault("comment", ""));
            ratingRequestDto.setBoardgameKey(boardgamekey);
            ratingRequestDto.setUserId(userId);
            ratingRequestDto.setTags((String)param.get("tag_key"));

            int tagCnt = 0;
            if(ratingRequestDto.getTags().length() != 24) {
                throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
            }
            for(int i = 0; i < ratingRequestDto.getTags().length(); i++) {
                if(ratingRequestDto.getTags().charAt(i) == '1') {
                    tagCnt++;
                    if(tagCnt > 5) {
                        throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 평점이 1~10 사이의 값인지 확인
            if(ratingRequestDto.getScore() < 1 || ratingRequestDto.getScore() > 10) {
                throw new AppException("평점은 1~10 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
            }

            ratingService.updateRating(ratingRequestDto);

            return ResponseEntity.ok().build();

        }else {
            throw new AppException("필수 정보가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{boardgamekey}")
    public ResponseEntity<?> deleteRating(@PathVariable Integer boardgamekey) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ratingService.deleteRating(userId, boardgamekey);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardgamekey}")
    public ResponseEntity<?> getRating(@PathVariable Integer boardgamekey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            return ResponseEntity.ok(ratingService.getRating(userId, boardgamekey));
        } catch (Exception e) {
            throw new AppException("평점 정보를 가져오는데 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 보드게임 리뷰 요청 api
    @PostMapping("/req/{boardgamekey}")
    public ResponseEntity<?> addRatingReq(@PathVariable Integer boardgamekey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        ratingService.addRatingReq(userId, boardgamekey);
        return ResponseEntity.ok().build();
    }

    // 보드게임 리뷰 요청 리스트 api
    @GetMapping("/req")
    public ResponseEntity<?> getRatingReq() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(ratingService.getRatingReq());
    }
}
