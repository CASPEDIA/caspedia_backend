package com.cast.caspedia.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RatingDto {
    private Integer ratingKey;
    private Integer score;
    private String comment;
    private Integer boardgameKey;
    private String nanoid;
    private String nameKor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
