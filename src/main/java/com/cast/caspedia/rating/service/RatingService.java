package com.cast.caspedia.rating.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.*;
import com.cast.caspedia.rating.dto.*;
import com.cast.caspedia.rating.repository.*;
import com.cast.caspedia.rating.util.TagBitmaskUtil;
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

    private final RatingTagRepository ratingTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private final RatingRepository ratingRepository;

    private final BoardgameRepository boardgameRepository;

    private final RatingReqRepository ratingReqRepository;

    private final LikeRepository likeRepository;

    private final RatingImpressedRepository ratingImpressedRepository;

    private final ReplyImpressedRepository replyImpressedRepository;
    private final ReplyRepository replyRepository;

    private final TagBitmaskUtil tagBitmaskUtil;

    RatingService(UserRepository userRepository, RatingRepository ratingRepository, BoardgameRepository boardgameRepository, RatingReqRepository ratingReqRepository, LikeRepository likeRepository, RatingTagRepository ratingTagRepository, TagRepository tagRepository, RatingImpressedRepository ratingImpressedRepository, ReplyImpressedRepository replyImpressedRepository, ReplyRepository replyRepository, TagBitmaskUtil tagBitmaskUtil) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.boardgameRepository = boardgameRepository;
        this.ratingReqRepository = ratingReqRepository;
        this.likeRepository = likeRepository;
        this.ratingTagRepository = ratingTagRepository;
        this.tagRepository = tagRepository;
        this.ratingImpressedRepository = ratingImpressedRepository;
        this.replyImpressedRepository = replyImpressedRepository;
        this.replyRepository = replyRepository;
        this.tagBitmaskUtil = tagBitmaskUtil;
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

        //TODO: ratingTag로 리펙토링
        rating.setTagKey(ratingRequestDto.getTags());
        String tagKeyBitmasking = ratingRequestDto.getTags();

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
        // RatingTag 생성 및 저장
        for(int i = 0; i < tagKeyBitmasking.length(); i++) {
            if(tagKeyBitmasking.charAt(i) == '1') {
                ratingTagRepository.save(
                        RatingTag.builder()
                                .rating(savedRating)
                                .tag(tagRepository.findById(i + 1)
                                        .orElseThrow(() -> new AppException("태그를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST)))
                                .build()
                );
            }
        }

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
        //TODO: ratingTag로 리펙토링
        rating.setTagKey(ratingRequestDto.getTags());
        String tagKeyBitmasking = ratingRequestDto.getTags();

        // 기존 RatingTag 삭제
        ratingTagRepository.deleteAllByRating(rating);

        // rating 업데이트
        Rating savedRating = ratingRepository.save(rating);
        // RatingTag 생성 및 저장
        for(int i = 0; i < tagKeyBitmasking.length(); i++) {
            if(tagKeyBitmasking.charAt(i) == '1') {
                ratingTagRepository.save(
                        RatingTag.builder()
                                .rating(savedRating)
                                .tag(tagRepository.findById(i + 1)
                                        .orElseThrow(() -> new AppException("태그를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST)))
                                .build()
                );
            }
        }

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

        // RatingTag 삭제
        ratingTagRepository.deleteByRating(rating);

        //rating 삭제
        ratingRepository.delete(rating);

        // 댓글 목록 조회
        List<Reply> replies = replyRepository.findAllByRating(rating);

        // 댓글에 대한 공감 삭제
        for (Reply reply : replies) {
            // 댓글에 대한 공감 삭제
            replyImpressedRepository.deleteAllByReply(reply);
        }

        // 댓글 삭제
        replyRepository.deleteAll(replies);

        // 평가에 대한 공감 삭제
        ratingImpressedRepository.deleteAllByRating(rating);

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
    public Object getRating(String userId, Integer boardgameKey) throws AppException {
        boolean ratingExists = ratingRepository.existsByUserIdAndBoardgameKey(userId, boardgameKey);

        Boardgame boardgame = boardgameRepository.findById(boardgameKey)
                .orElseThrow(() -> new AppException("보드게임을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

        if (!ratingExists) {
            // 평가가 없는 경우 응답
            RatingNoExistResponseDto ratingNoExistResponseDto = new RatingNoExistResponseDto();
            ratingNoExistResponseDto.setRatingExist(false);
            ratingNoExistResponseDto.setNameEng(boardgame.getNameEng());
            ratingNoExistResponseDto.setNameKor(boardgame.getNameKor());
            ratingNoExistResponseDto.setImageUrl(boardgame.getImageUrl());
            return ratingNoExistResponseDto;
        }

        // 평가가 있는 경우
        Rating rating = ratingRepository.findByUserIdAndBoardgameKey(userId, boardgameKey);

        RatingExistResponseDto ratingExistResponseDto = new RatingExistResponseDto();
        ratingExistResponseDto.setRatingExist(true);
        ratingExistResponseDto.setScore(rating.getScore());
        ratingExistResponseDto.setComment(rating.getComment());
        ratingExistResponseDto.setNameEng(boardgame.getNameEng());
        ratingExistResponseDto.setNameKor(boardgame.getNameKor());
        ratingExistResponseDto.setImageUrl(boardgame.getImageUrl());
        ratingExistResponseDto.setTagKey(tagBitmaskUtil.getTagBitmask(rating)); // 비트마스킹 문자열로 설정
        ratingExistResponseDto.setReplyCount(replyRepository.countByRating(rating)); // 평가에 대한 댓글 수

        return ratingExistResponseDto;
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


    public Object getTaggedGames(Integer tagKey) {
        if(tagKey == null) {
            throw new AppException("태그 키가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        Tag tag = tagRepository.findById(tagKey)
                .orElseThrow(() -> new AppException("해당 태그를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<Object[]> boardgames = ratingTagRepository.findBoardgameAndTagCountByTag(tag);
        if(boardgames.isEmpty()) {
            throw new AppException("해당 태그가 적용된 보드게임이 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<TaggedGameResponseDto> responseDtos = new ArrayList<>();
        for(Object[] result : boardgames) {
            Boardgame boardgame = (Boardgame) result[0];
            Long count = (Long) result[1];
            int tagCount = count != null ? count.intValue() : 0;
            TaggedGameResponseDto responseDto = TaggedGameResponseDto.builder()
                    .boardgameKey(boardgame.getBoardgameKey())
                    .nameEng(boardgame.getNameEng())
                    .nameKor(boardgame.getNameKor())
                    .likes(likeRepository.countLikeByBoardgame(boardgame))
                    .castScore(boardgame.getCastScore())
                    .imageUrl(boardgame.getImageUrl())
                    .tagCount(tagCount)
                    .build();
            responseDtos.add(responseDto);
        }
        return responseDtos;
    }

    // 평가에 공감 추가
    public void addRatingImpressed(String userId, Integer ratingKey) {
        Rating rating = ratingRepository.findById(ratingKey)
                .orElseThrow(() -> new AppException("해당 평가를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 공감이 없을때만 추가
        if(!ratingImpressedRepository.existsByUserAndRating(user, rating)) {
            RatingImpressed ratingImpressed = new RatingImpressed();
            ratingImpressed.setUser(user);
            ratingImpressed.setRating(rating);
            ratingImpressedRepository.save(ratingImpressed);
        } else {
            throw new AppException("이미 공감한 평가입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 평가에 공감 삭제
    public void deleteRatingImpressed(String userId, Integer ratingKey) {
        Rating rating = ratingRepository.findById(ratingKey)
                .orElseThrow(() -> new AppException("해당 평가를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 공감이 있을때, 유저가 공감한 평가인지 확인 후 삭제
        RatingImpressed ratingImpressed = ratingImpressedRepository.findByUserAndRating(user, rating)
                .orElseThrow(() -> new AppException("공감한 평가가 없습니다.", HttpStatus.NOT_FOUND));

        if(!ratingImpressed.getUser().getId().equals(user.getId())) {
            throw new AppException("공감한 평가가 아닙니다.", HttpStatus.FORBIDDEN);
        } else {
            // 공감 삭제
            ratingImpressedRepository.delete(ratingImpressed);
        }
    }

    // 평가에 댓글 추가
    public void addRatingReply(String userId, Integer ratingKey, String content) {
        Rating rating = ratingRepository.findById(ratingKey)
                .orElseThrow(() -> new AppException("해당 평가를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Reply reply = new Reply();
        reply.setContent(content);
        reply.setRating(rating);
        reply.setUser(user);
        replyRepository.save(reply);
    }

    // 평가 댓글 삭제
    @Transactional
    public void deleteRatingReply(String userId, Integer replyKey) {
        Reply reply = replyRepository.findById(replyKey)
                .orElseThrow(() -> new AppException("해당 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 댓글 작성자와 요청한 사용자가 일치하는지 확인
        if(!reply.getUser().getId().equals(user.getId())) {
            throw new AppException("댓글 작성자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
        }

        // 댓글에 대한 공감 삭제
        replyImpressedRepository.deleteAllByReply(reply);

        // 댓글 삭제
        replyRepository.delete(reply);
    }

    // 댓글에 공감 추가
    public void addReplyImpressed(String userId, Integer replyKey) {
        Reply reply = replyRepository.findById(replyKey)
                .orElseThrow(() -> new AppException("해당 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 공감이 없을때만 추가
        if(!replyImpressedRepository.existsByUserAndReply(user, reply)) {
            ReplyImpressed replyImpressed = new ReplyImpressed();
            replyImpressed.setUser(user);
            replyImpressed.setReply(reply);
            replyImpressedRepository.save(replyImpressed);
        } else {
            throw new AppException("이미 공감한 댓글입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글에 공감 삭제
    public void deleteReplyImpressed(String userId, Integer replyKey) {
        Reply reply = replyRepository.findById(replyKey)
                .orElseThrow(() -> new AppException("해당 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 공감이 있을때, 유저가 공감한 댓글인지 확인 후 삭제
        ReplyImpressed replyImpressed = replyImpressedRepository.findByUserAndReply(user, reply)
                .orElseThrow(() -> new AppException("공감한 댓글이 없습니다.", HttpStatus.NOT_FOUND));

        if(!replyImpressed.getUser().getId().equals(user.getId())) {
            throw new AppException("공감한 댓글이 아닙니다.", HttpStatus.FORBIDDEN);
        } else {
            // 공감 삭제
            replyImpressedRepository.delete(replyImpressed);
        }
    }

    public RatingDetailResponseDto getRatingDetail(String userId, Integer ratingKey) {
        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Rating rating = ratingRepository.findById(ratingKey)
                .orElseThrow(() -> new AppException("해당 평가를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Boardgame boardgame = rating.getBoardgame();
        RatingDetailResponseDto.GameInfoDto gameInfo = RatingDetailResponseDto.GameInfoDto.builder()
                .boardgameKey(boardgame.getBoardgameKey())
                .imageUrl(boardgame.getImageUrl())
                .nameKor(boardgame.getNameKor())
                .nameEng(boardgame.getNameEng())
                .yearPublished(boardgame.getYearPublished())
                .castScore(boardgame.getCastScore())
                .build();

        RatingDetailResponseDto.RatingInfoDto ratingInfo = RatingDetailResponseDto.RatingInfoDto.builder()
                .nanoid(user.getNanoid())
                .nickname(user.getNickname())
                .userImageKey(user.getUserImage().getUserImageKey())
                .comment(rating.getComment())
                .score(rating.getScore())
                .createdAt(rating.getCreatedAt().toString())
                .updatedAt(rating.getUpdatedAt().toString())
                .tagKeys(tagBitmaskUtil.getTagBitmask(rating)) // 비트마스킹 문자열로 설정
                .impressedCount(ratingImpressedRepository.countByRating(rating))
                .replyCount(replyRepository.countByRating(rating))
                .build();

        List<Reply> replyInfoList = replyRepository.findAllByRating(rating);

        List<RatingDetailResponseDto.ReplyInfoDto> replyInfoDtos = new ArrayList<>();

        for (Reply reply : replyInfoList) {
            RatingDetailResponseDto.ReplyInfoDto replyInfo = RatingDetailResponseDto.ReplyInfoDto.builder()
                    .replyKey(reply.getReplyKey())
                    .nanoid(reply.getUser().getNanoid())
                    .nickname(reply.getUser().getNickname())
                    .userImageKey(reply.getUser().getUserImage().getUserImageKey())
                    .impressedCount(replyImpressedRepository.countByReply(reply))
                    .content(reply.getContent())
                    .build();
            replyInfoDtos.add(replyInfo);
        }

        return RatingDetailResponseDto.builder()
                .gameInfo(gameInfo)
                .ratingInfo(ratingInfo)
                .replyInfo(replyInfoDtos.toArray(new RatingDetailResponseDto.ReplyInfoDto[0]))
                .build();
    }
}
