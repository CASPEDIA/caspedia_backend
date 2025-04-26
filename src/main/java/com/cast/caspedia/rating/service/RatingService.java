package com.cast.caspedia.rating.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingReq;
import com.cast.caspedia.rating.dto.*;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.rating.repository.RatingReqRepository;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RatingService {

    UserRepository userRepository;

    RatingRepository ratingRepository;

    BoardgameRepository boardgameRepository;

    RatingReqRepository ratingReqRepository;

    LikeRepository likeRepository;

    RatingService(UserRepository userRepository, RatingRepository ratingRepository, BoardgameRepository boardgameRepository, RatingReqRepository ratingReqRepository, LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.boardgameRepository = boardgameRepository;
        this.ratingReqRepository = ratingReqRepository;
        this.likeRepository = likeRepository;
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

    public void addRatingReq(String userId, Integer boardgamekey) {

        // 보드게임이 존재하는지 확인
        Boardgame boardgame = boardgameRepository.findById(boardgamekey)
                .orElseThrow(() -> new AppException("보드게임을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

        // 같은 게임 평가 요청이 7일 이내에 있는지 확인
        if(ratingReqRepository.existsByBoardgame_BoardgameKeyAndCreatedAtAfter(boardgamekey, LocalDateTime.now().minusDays(7))) {
            throw new AppException("7일 이내 생성된 리뷰 요청이 존재합니다.", HttpStatus.CONFLICT);
        }

        // userId로 User 엔티티 조회
        User user = userRepository.findUserByUserId(userId);

        RatingReq ratingReq = new RatingReq();
        ratingReq.setBoardgame(boardgame);
        ratingReq.setUser(user);
        ratingReqRepository.save(ratingReq);
        log.info("RatingReq saved: {}", ratingReq);
    }

    public List<RatingReqResponseDto> getRatingReq() {
        List<RatingReq> list = ratingReqRepository.findAllByCreatedAtAfter(LocalDateTime.now().minusDays(7));

        List<RatingReqResponseDto> responseDtos = new ArrayList<>();

        for(RatingReq ratingReq : list) {
            responseDtos.add(RatingReqResponseDto.builder()
                            .reqKey(ratingReq.getReqKey())
                            .boardgameKey(ratingReq.getBoardgame().getBoardgameKey())
                            .nanoid(ratingReq.getUser().getNanoid())
                            .nickname(ratingReq.getUser().getNickname())
                            .nameEng(ratingReq.getBoardgame().getNameEng())
                            .nameKor(ratingReq.getBoardgame().getNameKor())
                            .imageUrl(ratingReq.getBoardgame().getImageUrl())
                            .userImageKey(ratingReq.getUser().getUserImage().getUserImageKey())
                            .createdAt(ratingReq.getCreatedAt())
                            .build()
            );
        }

        return responseDtos;
    }

    public List<ScoreRankingResponseDto> getTopScore(int count) {
        Pageable top = PageRequest.of(0, count);
        List<Boardgame> best = boardgameRepository.findAllByOrderByCastScoreDesc(top);
        return boardgameToScoreRankingResponseDtos(best);
    }

    private List<ScoreRankingResponseDto> boardgameToScoreRankingResponseDtos(List<Boardgame> best) {
        List<ScoreRankingResponseDto> rankingResponseDtos = new ArrayList<>();

        int ranking = 1;
        for (Boardgame boardgame : best) {
            ScoreRankingResponseDto rankingResponseDto = ScoreRankingResponseDto.builder()
                    .ranking(ranking++)
                    .boardgameKey(boardgame.getBoardgameKey())
                    .imageUrl(boardgame.getImageUrl())
                    .nameKor(boardgame.getNameKor())
                    .nameEng(boardgame.getNameEng())
                    .likes(likeRepository.countLikeByBoardgame(boardgame))
                    .geekScore(boardgame.getGeekScore())
                    .castScore(boardgame.getCastScore())
                    .build();
            rankingResponseDtos.add(rankingResponseDto);
        }

        return rankingResponseDtos;
    }

    public List<ReviewRankingResponseDto> getTopCount(int count, int period) {
        //1개월
        if(period == 30) {
            LocalDateTime since = LocalDateTime.now().minusMonths(1);
            PageRequest pageRequest = PageRequest.of(0, count);
            return boardgameToReviewRankingResponseDtos(boardgameRepository.findTopByPeriodRatingCount(since, pageRequest));

        //3개월
        }else if(period == 90) {
            LocalDateTime since = LocalDateTime.now().minusMonths(3);
            PageRequest pageRequest = PageRequest.of(0, count);
            return boardgameToReviewRankingResponseDtos(boardgameRepository.findTopByPeriodRatingCount(since, pageRequest));

        //전체기간 조회
        }else {
            PageRequest pageRequest = PageRequest.of(0, count);
            return boardgameToReviewRankingResponseDtos(boardgameRepository.findTopByRatingCount(pageRequest));
        }
    }

    public List<ReviewRankingResponseDto> boardgameToReviewRankingResponseDtos(List<Boardgame> boardgames) {
        List<ReviewRankingResponseDto> rankingResponseDtos = new ArrayList<>();

        int ranking = 1;
        for (Boardgame boardgame : boardgames) {
            ReviewRankingResponseDto rankingResponseDto = ReviewRankingResponseDto.builder()
                    .ranking(ranking++)
                    .boardgameKey(boardgame.getBoardgameKey())
                    .imageUrl(boardgame.getImageUrl())
                    .nameKor(boardgame.getNameKor())
                    .nameEng(boardgame.getNameEng())
                    .likes(likeRepository.countLikeByBoardgame(boardgame))
                    .geekScore(boardgame.getGeekScore())
                    .castScore(boardgame.getCastScore())
                    .reviewCount(ratingRepository.countByBoardgame(boardgame))
                    .build();
            rankingResponseDtos.add(rankingResponseDto);
        }

        return rankingResponseDtos;
    }


}
