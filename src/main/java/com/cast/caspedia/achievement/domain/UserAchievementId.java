package com.cast.caspedia.achievement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementId implements Serializable {
    private Integer user;
    private Integer achievement;
}
