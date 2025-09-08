package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "boardgame_category")
@EqualsAndHashCode(exclude = "boardgame")
@ToString(exclude = "boardgame")
public class BoardgameCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "boardgame_key", nullable = false)
    private Boardgame boardgame;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "category_value")
    private String categoryValue;
}
