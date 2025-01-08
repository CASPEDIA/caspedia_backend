package com.cast.caspedia.boardgame.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "boardgame_category_kor")
public class BoardgameCategoryKor {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "category_id")
        private int categoryId;

        @Column(name = "name")
        private String name;
}
