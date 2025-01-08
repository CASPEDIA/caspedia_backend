package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class BoardgameMechanicKor {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private int id;

            @Column(name = "mechanic_id")
            private int mechanicId;

            @Column(name = "name")
            private String name;
}
