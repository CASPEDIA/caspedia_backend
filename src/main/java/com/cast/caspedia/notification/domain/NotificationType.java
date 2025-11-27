package com.cast.caspedia.notification.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notification_type")
@Data
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_key")
    private Integer notificationTypeKey;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "description", length = 255, nullable = false)
    private String description;
}
