package com.cast.caspedia.user.dto;

import lombok.Data;

@Data
public class LikeDto {
    private int boardgameKey;
    private String ImageUrl;
    private String nameKor;

    public LikeDto(int boardgameKey, String ImageUrl, String nameKor) {
        this.boardgameKey = boardgameKey;
        this.ImageUrl = ImageUrl;
        this.nameKor = nameKor;
    }
}
