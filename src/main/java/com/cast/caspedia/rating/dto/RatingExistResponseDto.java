package com.cast.caspedia.rating.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingExistResponseDto {
    private boolean ratingExist;
    private int ratingKey;
    private int score;
    private String comment;
    private String nameEng;
    private String nameKor;
    private String tagKey;
    private String imageUrl;
    private int replyCount;
    @JsonProperty("is_Impressed")
    private boolean isImpressed;
}
