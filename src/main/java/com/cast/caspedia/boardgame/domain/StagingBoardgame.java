package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="boardgames")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StagingBoardgame {
    @Id
    @Column(name = "boardgame_key", nullable = false)
    private Integer boardgameKey;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "yearpublished")
    private Integer yearpublished;

    @Column(name = "minplayers")
    private Integer minplayers;

    @Column(name = "maxplayers")
    private Integer maxplayers;

    @Column(name = "minplaytime")
    private Integer minplaytime;

    @Column(name = "maxplaytime")
    private Integer maxplaytime;

    @Column(name = "age")
    private Integer age;

    @Column(name = "geek_weight")
    private Float geekWeight;

    @Column(name = "geek_score")
    private Float geekScore;

    // TEXT 타입이지만 DB에서는 JSON(B) 타입으로 관리하는 것이 효율적입니다.
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // JSON 문자열을 그대로 저장하기 위한 컬럼들
    @Column(name = "names", columnDefinition = "jsonb")
    private String names;

    @Column(name = "links", columnDefinition = "jsonb")
    private String links;

    @Column(name = "designer", columnDefinition = "jsonb")
    private String designer;
}
