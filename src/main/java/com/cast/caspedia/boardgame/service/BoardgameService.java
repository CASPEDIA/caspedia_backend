package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.dto.BoardgameAutoFillDto;
import com.cast.caspedia.boardgame.dto.BoardgameSearchDto;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.util.KoreanMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardgameService {

    BoardgameRepository boardgameRepository;

    KoreanMatcher koreanMatcher;

    public BoardgameService(BoardgameRepository boardgameRepository, KoreanMatcher koreanMatcher) {
        this.boardgameRepository = boardgameRepository;
        this.koreanMatcher = koreanMatcher;
    }

    public List<BoardgameAutoFillDto> autofill(String query) {
        Pageable pageable = PageRequest.of(1, 16);
        if(koreanMatcher.isKorean(query)) {
            return boardgameRepository.autofillKor(query, pageable).getContent();
        }else {
            return boardgameRepository.autofillEng(query, pageable).getContent();
        }
    }


    public List<BoardgameSearchDto> search(String query, int page) {
        Pageable pageable = PageRequest.of(page-1, 10);

//        Page<BoardgameSearchDto.Data> result = boardgameRepository.search(query, pageable);

        return null;
    }
}
