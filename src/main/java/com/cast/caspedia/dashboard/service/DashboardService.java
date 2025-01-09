package com.cast.caspedia.dashboard.service;

import com.cast.caspedia.dashboard.dto.RecentRatedBoardgameResponseDto;
import com.cast.caspedia.dashboard.dto.RecentRatedUserResponseDto;
import com.cast.caspedia.rating.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    RatingRepository ratingRepository;

    public List<RecentRatedBoardgameResponseDto> getRecentRatedBoardgame() {
        //최근 평가된 게임 10개 가져오기
        Pageable pageable = PageRequest.of(0, 10);
        Page<RecentRatedBoardgameResponseDto> data = ratingRepository.findRecentRatedBoardgame(pageable);
        return data.stream().toList();
    }

    public List<RecentRatedUserResponseDto> getRecentRatedUser() {
        //최근 평가한 유저 10명 가져오기
        Pageable pageable = PageRequest.of(0, 10);
        Page<RecentRatedUserResponseDto> data = ratingRepository.findRecentRatedUser(pageable);
        return data.stream().toList();
    }
}
