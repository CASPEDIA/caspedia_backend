package com.cast.caspedia.boardgame.dto;

import lombok.Data;

import java.util.List;

@Data
public class RatingListResponseDto {
    private String nanoid;
    private String nickname;
    private int userImageKey;
    private String comment;
    private int score;
    private String createdAt;
    private String updatedAt;
    private List<Integer> tagKeys;
}
