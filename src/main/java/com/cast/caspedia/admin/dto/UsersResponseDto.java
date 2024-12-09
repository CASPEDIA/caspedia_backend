package com.cast.caspedia.admin.dto;

import lombok.Data;

@Data
public class UsersResponseDto {
    private String nanoid;
    private String id;
    private String nickname;
    private String name;
    private String introduction;
    private int studentId;
    private int userImageKey;
    private boolean enabled;
    private int authorityKey;
}
