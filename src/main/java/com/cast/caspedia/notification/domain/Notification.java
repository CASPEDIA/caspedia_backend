package com.cast.caspedia.notification.domain;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.Reply;
import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification", indexes = {
    @Index(name = "idx_notification_recipient_read_created", 
           columnList = "recipient_user_key, is_read, created_at"),
    @Index(name = "idx_notification_recipient_created_desc", 
           columnList = "recipient_user_key, created_at")
})
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_key")
    private Integer notificationKey;
    
    @ManyToOne
    @JoinColumn(name = "notification_type_key", nullable = false)
    private NotificationType notificationType;
    
    @ManyToOne
    @JoinColumn(name = "recipient_user_key", nullable = false)
    private User recipient;
    
    @ManyToOne
    @JoinColumn(name = "actor_user_key", nullable = false)
    private User actor;
    
    @ManyToOne
    @JoinColumn(name = "rating_key")
    private Rating rating;
    
    @ManyToOne
    @JoinColumn(name = "reply_key")
    private Reply reply;
    
    @ManyToOne
    @JoinColumn(name = "boardgame_key")
    private Boardgame boardgame;
    
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "description", length = 255, nullable = false)
    private String description;
}
