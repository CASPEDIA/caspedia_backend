package com.cast.caspedia.rating.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.dto.RatingExistResponseDto;
import com.cast.caspedia.rating.dto.RatingNoExistResponseDto;
import com.cast.caspedia.rating.dto.RatingRequestDto;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RatingService {

    UserRepository userRepository;

    RatingRepository ratingRepository;


    BoardgameRepository boardgameRepository;

    RatingService(UserRepository userRepository, RatingRepository ratingRepository, BoardgameRepository boardgameRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.boardgameRepository = boardgameRepository;
    }

    @Transactional
    public void addRating(RatingRequestDto ratingRequestDto) throws AppException {

        // 이미 평가한 보드게임인지 확인
        if(ratingRepository.existsByUserIdAndBoardgameKey(ratingRequestDto.getUserId(), ratingRequestDto.getBoardgameKey())) {
            throw new AppException("이미 평가한 보드게임입니다.", HttpStatus.BAD_REQUEST);
        }

        // Rating 엔티티 생성
        Rating rating = new Rating();
        rating.setScore(ratingRequestDto.getScore());
        rating.setComment(ratingRequestDto.getComment());
        rating.setTagKey(ratingRequestDto.getTags());

        // boardgameKey로 Boardgame 엔티티 조회
        Boardgame boardgame = boardgameRepository.findById(ratingRequestDto.getBoardgameKey())
                .orElseThrow(() -> new AppException("보드게임을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
        rating.setBoardgame(boardgame);

        // userId로 User 엔티티 조회
        try {
            User user = userRepository.findUserByUserId(ratingRequestDto.getUserId());
            rating.setUser(user);
            log.info("user: {}", user);
            if (user == null) {
                throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        //rating 저장
        Rating savedRating = ratingRepository.save(rating);

        // boardgame 엔티티에 평점 업데이트
        boardgame.setCastScore(calculateRating(ratingRequestDto.getBoardgameKey()));
        // boardgame 테이블에 저장
        boardgameRepository.save(boardgame);
    }

    @Transactional
    public void updateRating(RatingRequestDto ratingRequestDto) throws AppException{
        // 이미 평가한 보드게임인지 확인
        if(!ratingRepository.existsByUserIdAndBoardgameKey(ratingRequestDto.getUserId(), ratingRequestDto.getBoardgameKey())) {
            throw new AppException("평가 기록이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // Rating 엔티티 가져오기
        Rating rating = ratingRepository.findByUserIdAndBoardgameKey(ratingRequestDto.getUserId(), ratingRequestDto.getBoardgameKey());
        Boardgame boardgame = rating.getBoardgame();

        // Rating 엔티티 수정
        rating.setScore(ratingRequestDto.getScore());
        rating.setComment(ratingRequestDto.getComment());
        rating.setTagKey(ratingRequestDto.getTags());

        // rating 업데이트
        ratingRepository.save(rating);

        // boardgame 엔티티에 평점 업데이트
        boardgame.setCastScore(calculateRating(ratingRequestDto.getBoardgameKey()));
        // boardgame 테이블에 저장
        boardgameRepository.save(boardgame);
    }

    @Transactional
    public void deleteRating(String userId, Integer boardgameKey) {
        // 이미 평가한 보드게임인지 확인
        if(!ratingRepository.existsByUserIdAndBoardgameKey(userId, boardgameKey)) {
            throw new AppException("평가 기록이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // Rating 엔티티 가져오기
        Rating rating = ratingRepository.findByUserIdAndBoardgameKey(userId, boardgameKey);
        Boardgame boardgame = rating.getBoardgame();

        //rating 삭제
        ratingRepository.delete(rating);

        // boardgame 엔티티에 평점 업데이트
        boardgame.setCastScore(calculateRating(boardgameKey));
        // boardgame 테이블에 저장
        boardgameRepository.save(boardgame);
    }

    @Transactional
    public float calculateRating(Integer boardgameKey) {
        List<Integer> ratings = ratingRepository.findRatingByBoardgameKey(boardgameKey);
        int sum = ratings.stream().mapToInt(Integer::intValue).sum();
        double castRating =  (double) sum / ratings.size();
        castRating = Math.round(castRating * 10) / 10.0;
        return (float)castRating;
    }

    //평가를 조회하는 메서드
    public Object getRating(String userId, Integer boardgamekey) throws AppException{
        if(!ratingRepository.existsByUserIdAndBoardgameKey(userId, boardgamekey)) {
            Boardgame boardgame = boardgameRepository.findById(boardgamekey)
                    .orElseThrow(() -> new AppException("보드게임을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
            RatingNoExistResponseDto ratingNoExistResponseDto = new RatingNoExistResponseDto();
            ratingNoExistResponseDto.setRatingExist(false);
            ratingNoExistResponseDto.setNameEng(boardgame.getNameEng());
            ratingNoExistResponseDto.setNameKor(boardgame.getNameKor());
            ratingNoExistResponseDto.setImageUrl(boardgame.getImageUrl());
            return ratingNoExistResponseDto;
        }else {
            Rating rating = ratingRepository.findByUserIdAndBoardgameKey(userId, boardgamekey);
            RatingExistResponseDto ratingExistResponseDto = new RatingExistResponseDto();
            ratingExistResponseDto.setRatingExist(true);
            ratingExistResponseDto.setScore(rating.getScore());
            ratingExistResponseDto.setComment(rating.getComment());
            ratingExistResponseDto.setNameEng(rating.getBoardgame().getNameEng());
            ratingExistResponseDto.setNameKor(rating.getBoardgame().getNameKor());
            ratingExistResponseDto.setTagKey(rating.getTagKey());
            ratingExistResponseDto.setImageUrl(rating.getBoardgame().getImageUrl());
            return ratingExistResponseDto;
        }
    }
}
