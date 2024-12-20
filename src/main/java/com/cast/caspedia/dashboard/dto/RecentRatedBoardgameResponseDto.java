package com.cast.caspedia.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecentRatedBoardgameResponseDto {
    private Integer boardgameKey;
    private String nameEng;
    private String nameKor;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
