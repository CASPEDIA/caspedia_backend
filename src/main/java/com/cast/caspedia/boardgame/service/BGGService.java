package com.cast.caspedia.boardgame.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BGGService {
    private final WebClient BGGWebClient;
    private final XmlMapper xmlMapper;

    public BGGService(WebClient BGGWebClient) {
        this.BGGWebClient = BGGWebClient;
        this.xmlMapper = new XmlMapper();
    }

    public String getBoardgameInfo(String boardgameId) {
        return BGGWebClient.get()
                .uri("/thing?type=boardgame&stats=1&id=" + boardgameId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}