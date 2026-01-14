package com.cast.caspedia.achievement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponseDto {
    @JsonProperty("common_badge")
    private CommonBadgeDto commonBadge;

    @JsonProperty("event_badge")
    private List<String> eventBadge;
}
