package com.cast.caspedia.rating.controller;

import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.dto.RatingRequestDto;
import com.cast.caspedia.rating.service.RatingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        if(param.containsKey("score") && param.containsKey("comment") && param.containsKey("tag_key")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            RatingRequestDto ratingRequestDto = new RatingRequestDto();
            ratingRequestDto.setScore((Integer) param.get("score"));
            ratingRequestDto.setComment((String) param.get("comment"));
            ratingRequestDto.setBoardgameKey(boardgamekey);
            ratingRequestDto.setUserId(userId);


            // Object를 List<Integer>로 변환
            List<Integer> tagList = objectMapper.convertValue(param.get("tag_key"), new TypeReference<List<Integer>>() {});
            //변환하여 DTO에 설정
            ratingRequestDto.setTags(new ArrayList<>(tagList));

            log.info("ratingRequestDto: {}", ratingRequestDto);

            for(int tag : ratingRequestDto.getTags()) {
                if(tag < 1 || tag > 24) {
                    throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
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
        if(param.containsKey("score") && param.containsKey("comment") && param.containsKey("tag_key")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            RatingRequestDto ratingRequestDto = new RatingRequestDto();
            ratingRequestDto.setScore((Integer) param.get("score"));
            ratingRequestDto.setComment((String) param.get("comment"));
            ratingRequestDto.setBoardgameKey(boardgamekey);
            ratingRequestDto.setUserId(userId);


            // Object를 List<Integer>로 변환
            List<Integer> tagList = objectMapper.convertValue(param.get("tag_key"), new TypeReference<List<Integer>>() {});
            //변환하여 DTO에 설정
            ratingRequestDto.setTags(new ArrayList<>(tagList));

            log.info("ratingRequestDto: {}", ratingRequestDto);

            for(int tag : ratingRequestDto.getTags()) {
                if(tag < 1 || tag > 24) {
                    throw new AppException("태그 정보가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
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
}
