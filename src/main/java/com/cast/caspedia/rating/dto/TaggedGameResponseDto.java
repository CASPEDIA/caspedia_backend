package com.cast.caspedia.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaggedGameResponseDto {
    private Integer boardgameKey;
    private String imageUrl;
    private String nameKor;
    private String nameEng;
    private int likes;
    private double castScore;
    private int tagCount;
}
