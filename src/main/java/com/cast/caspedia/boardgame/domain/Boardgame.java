package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "boardgame")
@EqualsAndHashCode(callSuper = false, exclude = {"categories", "mechanics"})
@ToString(exclude = {"categories", "mechanics"})
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

    @Column(name = "designer", nullable = false, columnDefinition = "text default ''")
    private String designer;

    @Column(name = "likes", nullable = false, columnDefinition = "int default 0")
    private int likes = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "boardgame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BoardgameCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "boardgame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BoardgameMechanic> mechanics = new HashSet<>();

}
