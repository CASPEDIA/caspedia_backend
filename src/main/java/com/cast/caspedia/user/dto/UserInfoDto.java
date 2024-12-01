package com.cast.caspedia.user.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private String id;
    private String nickname;
    private String name;
    private String introduction;
    private String nanoid;
    private int userImageKey;
}

