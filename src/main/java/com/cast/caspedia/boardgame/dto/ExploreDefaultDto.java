package com.cast.caspedia.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExploreDefaultDto {
    private int boardgameKey;
    private String imageUrl;
    private String nameKor;
    private String nameEng;
    private int likes;
    private float geekScore;
    private float castScore;

    public ExploreDefaultDto(int boardgameKey,
                             float castScore,
                             float geekScore,
                             String imageUrl,
                             long likes,
                             String nameEng,
                             String nameKor) {
        this.boardgameKey = boardgameKey;
        this.castScore = castScore;
        this.geekScore = geekScore;
        this.imageUrl = imageUrl;
        this.likes = (int)likes;
        this.nameEng = nameEng;
        this.nameKor = nameKor;
    }
}
