package com.cast.caspedia.admin.dto;

import lombok.Data;

@Data
public class JoinRequestDto {
    /*
        "id":"ssafy1",
    "name":"1234",
    "password":"1234",
    "studentId":12345,
    "nickname":"ssafy",
    "authorityKey":1,
    "userImageKey":1
     */
    private String id;
    private String name;
    private String password;
    private int studentId;
    private String nickname;
    private int authorityKey;
    private int userImageKey;
}
