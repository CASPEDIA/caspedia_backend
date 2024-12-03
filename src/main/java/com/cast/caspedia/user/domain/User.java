package com.cast.caspedia.user.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Integer userKey;

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false , unique = true)
    private String nickname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduction", length = 300, nullable = false)
    private String introduction;

    @Column(name = "student_id", nullable = false)
    private int studentId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "authority_key", nullable = false)
    private Authority authority;

    @OneToOne
    @JoinColumn(name = "user_image_key", nullable = false)
    private UserImage userImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "nanoid")
    private String nanoid;

    // Getters and Setters
}
