package com.cast.caspedia.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LikeResponseDto {
    private String nanoid;
    private String nickname;
    private int userImageKey;
}
