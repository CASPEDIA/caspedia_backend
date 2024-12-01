package com.cast.caspedia.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    private int boardgameKey;
    private String ImageUrl;
    private String nameKor;
    private String nameEng;
    private LocalDateTime createdAt;


    public LikeDto() {
    }
}
