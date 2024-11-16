package com.cast.caspedia.rating.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingTag;
import com.cast.caspedia.rating.dto.RatingRequestDto;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.rating.repository.RatingTagRepository;
import com.cast.caspedia.rating.repository.TagRepository;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RatingService {

    UserRepository userRepository;

    RatingRepository ratingRepository;

    RatingTagRepository ratingTagRepository;

    TagRepository tagRepository;

    BoardgameRepository boardgameRepository;

    RatingService(UserRepository userRepository, RatingRepository ratingRepository, RatingTagRepository ratingTagRepository, TagRepository tagRepository, BoardgameRepository boardgameRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.ratingTagRepository = ratingTagRepository;
        this.tagRepository = tagRepository;
        this.boardgameRepository = boardgameRepository;
    }

    public void addRating(RatingRequestDto ratingRequestDto) throws AppException {

        // 이미 평가한 보드게임인지 확인
        if(ratingRepository.existsByUserIdAndBoardgameKey(ratingRequestDto.getUserId(), ratingRequestDto.getBoardgameKey())) {
            throw new AppException("이미 평가한 보드게임입니다.", HttpStatus.BAD_REQUEST);
        }

        // Rating 엔티티 생성
        Rating rating = new Rating();
        rating.setScore(ratingRequestDto.getScore());
        rating.setComment(ratingRequestDto.getComment());

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
        List<Integer> ratings = ratingRepository.findRatingByBoardgameKey(ratingRequestDto.getBoardgameKey());
        int sum = ratings.stream().mapToInt(Integer::intValue).sum();
        double castRating =  (double) sum / ratings.size();
        castRating = Math.round(castRating * 100) / 100.0;
        boardgame.setCastScore((float)castRating);
        boardgameRepository.save(boardgame);

        // tagIds에 있는 모든 ID로 Tag 엔티티를 조회
        List<RatingTag> ratingTags = ratingRequestDto.getTags().stream()
                .map(tagId -> tagRepository.findById(tagId)
                        .orElseThrow(() -> new AppException("태그를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST)))
                .map(tag -> new RatingTag(savedRating, tag))
                .toList();

        //tag 저장
        ratingTagRepository.saveAll(ratingTags);
    }

}
