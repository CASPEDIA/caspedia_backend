package com.cast.caspedia.rating.dto;

import lombok.Data;

import java.util.List;

@Data
public class RatingRequestDto {
    private Integer score;
    private String comment;
    private Integer boardgameKey;
    private String userId;
    private List<Integer> tags;
}
