package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "boardgame_mechanic")
@EqualsAndHashCode(exclude = "boardgame")
@ToString(exclude = "boardgame")
public class BoardgameMechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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