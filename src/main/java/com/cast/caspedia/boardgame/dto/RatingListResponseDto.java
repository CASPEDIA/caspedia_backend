package com.cast.caspedia.boardgame.dto;

import lombok.Data;

@Data
public class RatingListResponseDto {
    private String nanoid;
    private String nickname;
    private int userImageKey;
    private String comment;
    private int score;
    private String createdAt;
    private String updatedAt;
    private String tagKeys;
}
