package com.cast.caspedia.achievement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonBadgeDto {
    private int login;
    private int rating;
    private int comment;
    private int like;
}
