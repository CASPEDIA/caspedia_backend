package com.cast.caspedia.boardgame.dto;

import lombok.Data;

@Data
public class BoardgameCsvDto {
    private String boardgameKey;
    private String nameEng;
    private String yearPublished;
    private float geekScore;
}
