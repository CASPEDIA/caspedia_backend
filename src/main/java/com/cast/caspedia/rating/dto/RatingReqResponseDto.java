package com.cast.caspedia.rating.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class RatingReqResponseDto {
    @JsonProperty("req_key")
    private int reqKey;

    @JsonProperty("boardgame_key")
    private int boardgameKey;

    @JsonProperty("name_kor")
    private String nameKor;

    @JsonProperty("name_eng")
    private String nameEng;

    private String nanoid;

    private String nickname;

    @JsonProperty("user_image_key")
    private int userImageKey;

    private LocalDateTime createdAt;

}
