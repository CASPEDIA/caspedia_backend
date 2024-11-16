package com.cast.caspedia.boardgame.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardgameSearchDto {

    private Pagination pagination;
    private List<Data> data;

    @lombok.Data
    public static class Pagination {
        private int total;
        private int page;
        private int lastPage;
    }

    @lombok.Data
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
