package com.cast.caspedia.boardgame.dto;

import lombok.Data;

@Data
public class BoardgameAutoFillDto {
    private int boardgameKey;
    private String name;
    private int yearPublished;

    BoardgameAutoFillDto(int boardgameKey, String name, int yearPublished) {
        this.boardgameKey = boardgameKey;
        this.name = name;
        this.yearPublished = yearPublished;
    }
}
