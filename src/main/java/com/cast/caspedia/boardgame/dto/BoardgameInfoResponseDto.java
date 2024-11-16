package com.cast.caspedia.boardgame.dto;

import lombok.Data;

@Data
public class BoardgameInfoResponseDto {
    private int boardgameKey;
    private String imageUrl;
    private String nameKor;
    private String nameEng;
    private int yearPublished;
    private String description;
    private int minPlayers;
    private int maxPlayers;
    private int minPlaytime;
    private int maxPlaytime;
    private float geekWeight;
    private float geekScore;
    private float castScore;
    private int age;
}
