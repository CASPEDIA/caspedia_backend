package com.cast.caspedia.rating.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Integer ratingKey;
    private Integer score;
    private String comment;
    private Integer boardgameKey;
    private String nanoid;
    private String nameEng;
    private String nameKor;
    private String tagKey;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("impressed_count")
    private int impressedCount;

    @JsonProperty("reply_count")
    private int replyCount;

}
