package com.cast.caspedia.rating.dto;

import lombok.Data;

@Data
public class RatingNoExistResponseDto {
    private boolean ratingExist;
    private String nameEng;
    private String nameKor;
    private String imageUrl;
}
