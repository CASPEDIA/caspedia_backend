package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "boardgame")
@Data
public class Boardgame {

    @Id
    @Column(name = "boardgame_key")
    private Integer boardgameKey;

    @Column(name = "name_kor", nullable = false, columnDefinition = "varchar default ''")
    private String nameKor = "";

    @Column(name = "name_eng", nullable = false, columnDefinition = "varchar default ''")
    private String nameEng = "";

    @Column(name = "image_url", nullable = false, columnDefinition = "varchar default ''")
    private String imageUrl = "";

    @Column(name = "year_published", nullable = false, columnDefinition = "int default 0")
    private int yearPublished = 0;

    @Column(name = "description", nullable = false, columnDefinition = "varchar default ''")
    private String description = "";

    @Column(name = "min_players", nullable = false, columnDefinition = "int default 0")
    private int minPlayers = 0;

    @Column(name = "max_players", nullable = false, columnDefinition = "int default 0")
    private int maxPlayers = 0;

    @Column(name = "min_playtime", nullable = false, columnDefinition = "int default 0")
    private int minPlaytime = 0;

    @Column(name = "max_playtime", nullable = false, columnDefinition = "int default 0")
    private int maxPlaytime = 0;

    @Column(name = "age", nullable = false, columnDefinition = "int default 0")
    private int age = 0;

    @Column(name = "cast_owned", nullable = false, columnDefinition = "boolean default false")
    private boolean castOwned = false;

    @Column(name = "geek_weight", nullable = false, columnDefinition = "float default 0")
    private float geekWeight = 0.0f;

    @Column(name = "geek_score", nullable = false, columnDefinition = "float default 0")
    private float geekScore = 0.0f;

    @Column(name = "cast_score", nullable = false, columnDefinition = "float default 0")
    private float castScore = 0.0f;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;



}
