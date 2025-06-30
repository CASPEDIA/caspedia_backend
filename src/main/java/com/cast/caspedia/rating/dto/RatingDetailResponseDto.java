package com.cast.caspedia.rating.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDetailResponseDto {
    private GameInfoDto gameInfo;
    private RatingInfoDto ratingInfo;
    private ReplyInfoDto[] replyInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameInfoDto {
        private int boardgameKey;
        private String imageUrl;
        private String nameKor;
        private String nameEng;
        private int yearPublished;
        private float castScore;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RatingInfoDto {
        private String nanoid;
        private String nickname;
        private int userImageKey;
        private String comment;
        private int score;
        private String createdAt;
        private String updatedAt;
        private String tagKeys;
        private int impressedCount;
        private int replyCount;
        @JsonProperty("is_Impressed")
        private boolean isImpressed;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyInfoDto {
        private int replyKey;
        private String nanoid;
        private String nickname;
        private int userImageKey;
        private int impressedCount;
        private String content;
        @JsonProperty("is_Impressed")
        private boolean isImpressed;
    }
}