package com.cast.caspedia.rating.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ReviewRankingResponseDto {
    private int ranking;

    @JsonProperty("boardgame_key")
    private int boardgameKey;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("name_kor")
    private String nameKor;

    @JsonProperty("name_eng")
    private String nameEng;

    private int likes;

    @JsonProperty("geek_score")
    private float geekScore;

    @JsonProperty("cast_score")
    private float castScore;

    @JsonProperty("review_count")
    private int reviewCount;
}
