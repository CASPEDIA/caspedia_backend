package com.cast.caspedia.dashboard.service;

import com.cast.caspedia.dashboard.dto.RecentRatedBoardgameResponseDto;
import com.cast.caspedia.dashboard.dto.RecentRatedUserResponseDto;
import com.cast.caspedia.rating.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    RatingRepository ratingRepository;

    public List<RecentRatedBoardgameResponseDto> getRecentRatedBoardgame() {
        List<RecentRatedBoardgameResponseDto> list = ratingRepository.findRecentRatedBoardgame();
        return list;
    }

    public List<RecentRatedUserResponseDto> getRecentRatedUser() {
        List<RecentRatedUserResponseDto> list = ratingRepository.findRecentRatedUser();
        return list;
    }
}
