package com.cast.caspedia.boardgame.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingListResponseDto {
    private int ratingKey;
    private String nanoid;
    private String nickname;
    private int userImageKey;
    private String comment;
    private int score;
    private String createdAt;
    private String updatedAt;
    private String tagKeys;

    private int replyCount;

    @JsonProperty("is_Impressed")
    private boolean isImpressed;
}
