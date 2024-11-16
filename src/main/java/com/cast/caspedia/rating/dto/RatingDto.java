package com.cast.caspedia.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Integer ratingKey;
    private Integer score;
    private String comment;
    private Integer boardgameKey;
    private String nanoid;
    private String nameEng;
    private String nameKor;
    private List<Integer> tagKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
