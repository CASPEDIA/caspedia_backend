package com.cast.caspedia.boardgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class BoardgameSearchDto {

    private Pagination pagination;
    private List<Data> data;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Pagination {
        private int total;
        private int page;
        private int lastPage;
    }

    @Getter
    @Setter
    public static class Data {
        private long boardgameKey;
        private String imageUrl;
        private String nameKor;
        private String nameEng;
        private int likes;
        private float geekScore;
        private float castScore;
    }
}
