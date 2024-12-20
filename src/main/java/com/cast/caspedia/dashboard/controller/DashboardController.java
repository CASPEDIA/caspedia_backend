package com.cast.caspedia.dashboard.controller;

import com.cast.caspedia.dashboard.dto.RecentRatedBoardgameResponseDto;
import com.cast.caspedia.dashboard.dto.RecentRatedUserResponseDto;
import com.cast.caspedia.dashboard.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    //최근에 평가된 게임
    @GetMapping("/boardgame/recent")
    public ResponseEntity<?> getRecentRatedBoardgame() {
        List<RecentRatedBoardgameResponseDto> list = dashboardService.getRecentRatedBoardgame();
        return ResponseEntity.ok(list);
    }

    //최근 평가한 유저
    @GetMapping("/user/recent")
    public ResponseEntity<?> getRatedUser() {
        List<RecentRatedUserResponseDto> list = dashboardService.getRecentRatedUser();
        return ResponseEntity.ok(list);
    }
}
