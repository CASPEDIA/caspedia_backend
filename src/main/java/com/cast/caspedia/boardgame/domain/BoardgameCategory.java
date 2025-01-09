package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "boardgame_category")
public class BoardgameCategory {

    @Id
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
