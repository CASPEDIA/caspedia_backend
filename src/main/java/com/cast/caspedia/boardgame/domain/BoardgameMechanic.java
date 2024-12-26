package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "boardgame_mechanic")
public class BoardgameMechanic {

    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "boardgame_key", nullable = false)
    private Boardgame boardgame;

    @Column(name = "mechanic_id")
    private int mechanicId;

    @Column(name = "mechanic_value")
    private String mechanicValue;
}