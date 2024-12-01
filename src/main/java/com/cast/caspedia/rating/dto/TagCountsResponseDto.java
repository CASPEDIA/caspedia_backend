package com.cast.caspedia.rating.dto;

import lombok.Data;

@Data
public class TagCountsResponseDto {
    private int tagKey;
    private int count;

    public TagCountsResponseDto() {
    }

    public TagCountsResponseDto(int tagKey, int count) {
        this.tagKey = tagKey;
        this.count = count;
    }
}
