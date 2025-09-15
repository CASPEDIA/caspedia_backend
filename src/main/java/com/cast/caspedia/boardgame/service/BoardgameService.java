package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.dto.*;
import com.cast.caspedia.boardgame.repository.BoardgameCategoryRepository;
import com.cast.caspedia.boardgame.repository.BoardgameMechanicRepository;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.boardgame.util.KoreanMatcher;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.dto.TagCountsResponseDto;
import com.cast.caspedia.rating.repository.*;
import com.cast.caspedia.rating.util.TagBitmaskUtil;
import com.cast.caspedia.user.domain.Like;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BoardgameService {

    private final BoardgameRepository boardgameRepository;

    private final LikeRepository likeRepository;

    private final UserRepository userRepository;

    private final RatingTagRepository ratingTagRepository;

    private final TagRepository tagRepository;

    private final RatingRepository ratingRepository;

    private final KoreanMatcher koreanMatcher;

    private final BoardgameMechanicRepository boardgameMechanicRepository;

    private final BoardgameCategoryRepository boardgameCategoryRepository;

    private final TagBitmaskUtil tagBitmaskUtil;
    private final ReplyRepository replyRepository;
    private final RatingImpressedRepository ratingImpressedRepository;

    public BoardgameService(BoardgameMechanicRepository boardgameMechanicRepository, BoardgameCategoryRepository boardgameCategoryRepository, BoardgameRepository boardgameRepository, KoreanMatcher koreanMatcher, LikeRepository likeRepository, UserRepository userRepository, RatingTagRepository ratingTagRepository, TagRepository tagRepository, RatingRepository ratingRepository, TagBitmaskUtil tagBitmaskUtil, ReplyRepository replyRepository, RatingImpressedRepository ratingImpressedRepository) {
        this.boardgameRepository = boardgameRepository;
        this.koreanMatcher = koreanMatcher;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.ratingTagRepository = ratingTagRepository;
        this.tagRepository = tagRepository;
        this.ratingRepository = ratingRepository;
        this.boardgameMechanicRepository = boardgameMechanicRepository;
        this.boardgameCategoryRepository = boardgameCategoryRepository;
        this.tagBitmaskUtil = tagBitmaskUtil;
        this.replyRepository = replyRepository;
        this.ratingImpressedRepository = ratingImpressedRepository;
    }

    public List<BoardgameAutoFillDto> autofill(String query) {
        Pageable pageable = PageRequest.of(0, 16);
        if(koreanMatcher.isKorean(query)) {
            return boardgameRepository.autofillKor(query, pageable).getContent();
        }else {
            return boardgameRepository.autofillEng(query, pageable).getContent();
        }
    }


    public BoardgameSearchDto search(String query, int page) {
        Pageable pageable = PageRequest.of(page-1, 10);

        Page<Boardgame> result = boardgameRepository.search(query, pageable);

        if(result != null) {
            BoardgameSearchDto dto = new BoardgameSearchDto();
            dto.setPagination(new BoardgameSearchDto.Pagination((int) result.getTotalElements(), page, result.getTotalPages()));
            dto.setData(result.map(boardgame -> {
                BoardgameSearchDto.Data data = new BoardgameSearchDto.Data();
                data.setBoardgameKey(boardgame.getBoardgameKey());
                data.setImageUrl(boardgame.getImageUrl());
                data.setNameKor(boardgame.getNameKor());
                data.setNameEng(boardgame.getNameEng());
                data.setLikes(likeRepository.countLikeByBoardgame(boardgame));
                data.setYearPublished(boardgame.getYearPublished());
                //소수점 첫째 자리까지만 반환
                data.setGeekScore((float) Math.round(boardgame.getGeekScore() * 10) / 10);
                data.setCastScore(boardgame.getCastScore());
                return data;
            }).getContent());
            return dto;
        }else {
            BoardgameSearchDto dto = new BoardgameSearchDto();
            dto.setPagination(new BoardgameSearchDto.Pagination(0, page, 0));
            dto.setData(new ArrayList<>());
            return dto;
        }

    }

    public boolean checkLike(int boardgameKey, String userId) throws AppException {

        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

        log.info("boardgame : {}", boardgame);
        log.info("user : {}", user);

        return likeRepository.existsByBoardgameAndUser(boardgame, user);
    }

    @Transactional
    public void addLike(int boardgameKey, String userId) throws AppException {
        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

        if(likeRepository.existsByBoardgameAndUser(boardgame, user)) {
            throw new AppException("이미 좋아요를 누르셨습니다.", HttpStatus.BAD_REQUEST);
        }else {
            boardgame.setLikes(boardgame.getLikes() + 1);
            boardgameRepository.save(boardgame);
            likeRepository.save(new Like(boardgame, user));
        }
    }

    @Transactional
    public void deleteLike(int boardgameKey, String userId) throws AppException {
        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

        Like like = likeRepository.findByBoardgameAndUser(boardgame, user);
        if(like == null) {
            throw new AppException("좋아요를 누르지 않으셨습니다.", HttpStatus.BAD_REQUEST);
        }else {
            boardgame.setLikes(boardgame.getLikes() - 1);
            boardgameRepository.save(boardgame);
            likeRepository.delete(like);
        }
    }

    public List<LikeResponseDto> getLikeList(int boardgameKey) throws AppException {
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        List<Like> likes = likeRepository.findAllByBoardgame(boardgame);

        List<LikeResponseDto> result = new ArrayList<>();
        for(Like like : likes) {
            result.add(new LikeResponseDto(like.getUser().getNanoid(), like.getUser().getNickname(), like.getUser().getUserImage().getUserImageKey()));
        }
        return result;
    }

    public BoardgameInfoResponseDto getBasicInfo(int boardgameKey) throws AppException{
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

        if(boardgame == null) {
            throw new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        BoardgameInfoResponseDto dto = new BoardgameInfoResponseDto();
        dto.setBoardgameKey(boardgame.getBoardgameKey());
        dto.setImageUrl(boardgame.getImageUrl());
        dto.setNameKor(boardgame.getNameKor());
        dto.setNameEng(boardgame.getNameEng());
        dto.setYearPublished(boardgame.getYearPublished());
        dto.setDescription(boardgame.getDescription());
        dto.setMinPlayers(boardgame.getMinPlayers());
        dto.setMaxPlayers(boardgame.getMaxPlayers());
        dto.setMinPlaytime(boardgame.getMinPlaytime());
        dto.setMaxPlaytime(boardgame.getMaxPlaytime());
        dto.setGeekScore((float) Math.round(boardgame.getGeekScore() * 10) / 10);
        dto.setGeekWeight((float) Math.round(boardgame.getGeekWeight() * 10) / 10);
        dto.setCastScore(boardgame.getCastScore());
        dto.setAge(boardgame.getAge());
        dto.setDesigner(boardgame.getDesigner());

        List<String> categoryNames = boardgameCategoryRepository.findKoreanCategoryNamesByBoardgame(boardgame);
        List<String> mechanicNames = boardgameMechanicRepository.findKoreanMechanicNamesByBoardgame(boardgame);

        dto.setCategory(categoryNames);
        dto.setMechanic(mechanicNames);

        return dto;
    }

    public List<TagCountsResponseDto> getTagCounts(int boardgameKey) {
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        if(boardgame == null) {
            throw new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 각 태그별 리뷰 수 조회 (tagKey, count)
        List<Object[]> tagCountsRaw = ratingTagRepository.countTagsByBoardgame(boardgameKey);

        // 전체 태그 수 조회 → 누락 태그에 대한 count = 0 처리
        long totalTagCount = tagRepository.count();

        // Map 으로 변환
        Map<Integer, Integer> tagCountMap = new HashMap<>();
        for (Object[] row : tagCountsRaw) {
            Integer tagKey = (Integer) row[0];
            Long count = (Long) row[1];
            tagCountMap.put(tagKey, count.intValue());
        }

        // 전체 태그에 대해 응답 생성 (없는 태그는 count = 0)
        List<TagCountsResponseDto> result = new ArrayList<>();
        for (int i = 1; i <= totalTagCount; i++) {
            int count = tagCountMap.getOrDefault(i, 0);
            result.add(new TagCountsResponseDto(i, count));
        }

        return result;
    }

    public List<RatingListResponseDto> getRatingList(int boardgameKey) {
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        List<Rating> ratings = ratingRepository.findAllRatingByBoardgame(boardgame);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findUserByUserId(userId);

        long totalTagCount = tagRepository.count();

        List<RatingListResponseDto> result = new ArrayList<>();

        for(Rating rating : ratings) {
            RatingListResponseDto dto = new RatingListResponseDto();
            dto.setRatingKey(rating.getRatingKey());
            dto.setNanoid(rating.getUser().getNanoid());
            dto.setNickname(rating.getUser().getNickname());
            dto.setUserImageKey(rating.getUser().getUserImage().getUserImageKey());
            dto.setComment(rating.getComment());
            dto.setScore(rating.getScore());
            dto.setCreatedAt(rating.getCreatedAt().toString());
            dto.setUpdatedAt(rating.getUpdatedAt().toString());
            dto.setReplyCount(replyRepository.countByRating(rating));
            dto.setImpressed(ratingImpressedRepository.existsByUserAndRating(user, rating));

            dto.setTagKeys(tagBitmaskUtil.getTagBitmask(rating));
            result.add(dto);
        }
        return result;
    }


    public Map<String, Object> exploreBoardgames(
            int page,
            int minPlayers, int maxPlayers,
            int minPlayTime, int maxPlayTime,
            int minGeekWeight, int maxGeekWeight,
            String sortParam) {

        Sort sort = switch (sortParam) {
            case "likedesc" -> Sort.by(Sort.Direction.DESC, "likes");
            case "likeasc" -> Sort.by(Sort.Direction.ASC, "likes");
            case "castdesc" -> Sort.by(Sort.Direction.DESC, "castScore");
            case "castasc" -> Sort.by(Sort.Direction.ASC, "castScore");
            default -> null;
        };

        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<ExploreDefaultDto> result = boardgameRepository.findExploreDefault(
                minPlayers,
                maxPlayers,
                minPlayTime,
                maxPlayTime,
                (float) minGeekWeight,
                (float) maxGeekWeight,
                pageable
        );

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", result.getTotalElements());   // 전체 아이템 수
        pagination.put("page", result.getNumber() + 1);      // 1부터 시작하는 현재 페이지
        pagination.put("last_page", result.getTotalPages());     // 전체 페이지 수

        Map<String, Object> response = new HashMap<>();
        response.put("pagination", pagination);
        response.put("data", result.getContent());

        return response;
    }

}
