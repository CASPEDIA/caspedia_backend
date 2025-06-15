package com.cast.caspedia.rating.domain;

import com.cast.caspedia.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reply_impressed")
@Data
public class ReplyImpressed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_impressed_key")
    private Integer replyImpressedKey;

    @ManyToOne
    @JoinColumn(name = "user_key", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reply_key", nullable = false)
    private Reply reply;
}
