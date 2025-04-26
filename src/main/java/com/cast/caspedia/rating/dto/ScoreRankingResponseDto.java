package com.cast.caspedia.rating.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ScoreRankingResponseDto {
    private int ranking;
    private int boardgameKey;
    private String imageUrl;
    private String nameKor;
    private String nameEng;
    private int likes;
    private float geekScore;
    private float castScore;
}
