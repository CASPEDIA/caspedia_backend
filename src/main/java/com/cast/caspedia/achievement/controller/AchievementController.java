package com.cast.caspedia.achievement.controller;

import com.cast.caspedia.achievement.dto.AchievementResponseDto;
import com.cast.caspedia.achievement.service.AchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user/achievement", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping("/{nanoid}")
    public ResponseEntity<AchievementResponseDto> getAchievement(@PathVariable String nanoid) {
        log.info("업적 조회 요청: nanoid={}", nanoid);
        AchievementResponseDto response = achievementService.getAchievement(nanoid);
        return ResponseEntity.ok(response);
    }
}
