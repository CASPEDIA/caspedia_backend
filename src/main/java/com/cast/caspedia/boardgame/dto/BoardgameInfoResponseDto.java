package com.cast.caspedia.boardgame.dto;

import lombok.Data;

import java.util.List;

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

    private String designer;
    private List<String> category;
    private List<String> mechanic;
}
