package com.cast.caspedia.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecentRatedUserResponseDto {
    private String nanoid;
    private String nickname;
    private int userImageKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
