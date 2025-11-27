package com.cast.caspedia.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationInfoDto {
    private Integer notificationKey;
    private String code;
    private Integer ratingKey;
    private Integer replyKey;
    private Integer boardgameKey;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
}
