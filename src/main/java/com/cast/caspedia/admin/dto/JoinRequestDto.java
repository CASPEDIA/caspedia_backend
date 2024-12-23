package com.cast.caspedia.admin.dto;

import lombok.Data;

@Data
public class JoinRequestDto {
    private String id;
    private String name;
    private int studentId;
    private int authorityKey;
    private boolean enabled;
}
