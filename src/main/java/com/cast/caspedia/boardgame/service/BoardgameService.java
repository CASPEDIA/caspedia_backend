package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.BoardgameSearchDto;
import com.cast.caspedia.boardgame.dto.LikeResponseDto;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.boardgame.util.KoreanMatcher;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.user.domain.Like;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BoardgameService {

    BoardgameRepository boardgameRepository;

    LikeRepository likeRepository;

    UserRepository userRepository;

    KoreanMatcher koreanMatcher;



    public BoardgameService(BoardgameRepository boardgameRepository, KoreanMatcher koreanMatcher, LikeRepository likeRepository, UserRepository userRepository) {
        this.boardgameRepository = boardgameRepository;
        this.koreanMatcher = koreanMatcher;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
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
//                data.setLikes(boardgame.getLikes());

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

    public void addLike(int boardgameKey, String userId) throws AppException {
        User user = userRepository.findUserByUserId(userId);
        if(user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        Boardgame boardgame = boardgameRepository.findById(boardgameKey).orElseThrow(() -> new AppException("해당 보드게임이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

        if(likeRepository.existsByBoardgameAndUser(boardgame, user)) {
            throw new AppException("이미 좋아요를 누르셨습니다.", HttpStatus.BAD_REQUEST);
        }else {
            likeRepository.save(new Like(boardgame, user));
        }
    }

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
}
