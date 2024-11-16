package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.dto.bggxmldto.Item;
import com.cast.caspedia.boardgame.dto.bggxmldto.Items;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.util.KoreanMatcher;
import com.cast.caspedia.error.AppException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BGGService {
    private final WebClient BGGWebClient;
    private final XmlMapper xmlMapper;

    private final KoreanMatcher koreanMatcher;

    private final BoardgameRepository boardgameRepository;

    public BGGService(WebClient BGGWebClient, KoreanMatcher koreanMatcher, BoardgameRepository boardgameRepository) {
        this.BGGWebClient = BGGWebClient;
        this.xmlMapper = new XmlMapper();
        this.koreanMatcher = koreanMatcher;
        this.boardgameRepository = boardgameRepository;
    }

//    public Mono<String> getDetailbyId(String boardgameId) {
//        return BGGWebClient.get()
//                .uri("/thing?type=boardgame&stats=1&id=" + boardgameId)
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    public Mono<String> getDetailbyIdList(List<Integer> boardgameIdList) {

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < boardgameIdList.size(); i++) {
            sb.append(boardgameIdList.get(i));
            if(i != boardgameIdList.size() - 1) {
                sb.append(",");
            }
        }

        return BGGWebClient.get()
                .uri("/thing?stats=1&id=" + sb.toString())
                .retrieve()
                .bodyToMono(String.class);
    }

    @Transactional
    public void detailSave(List<Integer> boardgameIdList) {
        XmlMapper xmlMapper = new XmlMapper();
        List<Boardgame> boardgameList = new ArrayList<>();

        getDetailbyIdList(boardgameIdList)
                .subscribe(response -> {
                    try {
                        Items items = xmlMapper.readValue(response, Items.class);

                        for(Item item : items.getItems()) {
                            log.info(item.getId() + "번 게임 저장 중");
                            Boardgame boardgame = new Boardgame();
                            boardgame.setBoardgameKey(item.getId());
                            boardgame.setYearPublished(item.getYearpublished().getValue());
                            boardgame.setImageUrl(item.getImage());
                            boardgame.setDescription(item.getDescription());
                            boardgame.setMinPlayers(item.getMinPlayers().getValue());
                            boardgame.setMaxPlayers(item.getMaxplayers().getValue());
                            boardgame.setAge(item.getMinage().getValue());
                            boardgame.setMinPlaytime(item.getMinplaytime().getValue());
                            boardgame.setMaxPlaytime(item.getMaxplaytime().getValue());
                            boardgame.setGeekWeight(item.getStatistics().getRatings().getAverageweight().getValue());
                            boardgame.setGeekScore(item.getStatistics().getRatings().getAverage().getValue());

                            //이름 넣기
                            for(int i = 0; i < item.getNames().size(); i++) {
                                if(koreanMatcher.isKorean(item.getNames().get(i).getValue())) {
                                    boardgame.setNameKor(item.getNames().get(i).getValue());
                                } else if(item.getNames().get(i).getType().equals("primary")) {
                                    boardgame.setNameEng(item.getNames().get(i).getValue());
                                }
                            }

                            boardgameList.add(boardgame);
                        }

                        for(Boardgame boardgame : boardgameList) {
                            saveOne(boardgame);
                        }

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Transactional public void saveOne(Boardgame boardgame) {
        try{
            boardgameRepository.save(boardgame);

        } catch (Exception e) {
            log.error(boardgame.getBoardgameKey() + "번 게임 저장 실패");
        }
    }


    @Transactional
    public void detailEnhance() {

        for(int i = 0; i < 8000; i++) {
            Pageable pageable = PageRequest.of(i, 20);
            List<Boardgame> list = boardgameRepository.getBoardgameList(pageable).getContent();

            if(list.isEmpty()) {
                throw new AppException("데이터 없음", HttpStatus.BAD_REQUEST);
            }
            if(list.get(0).getBoardgameKey() == 310) {
                continue;
            }

            if(!list.get(0).getDescription().isEmpty() || !list.get(0).getImageUrl().isEmpty()) {
                log.info(list.get(0).getBoardgameKey() + "~" + list.get(list.size() - 1).getBoardgameKey() + "번 게임은 이미 상세정보가 있습니다.");
                continue;
            }

            List<Integer> idList = new ArrayList<>();
            for(Boardgame boardgame : list) {
                idList.add(boardgame.getBoardgameKey());
            }

            try {
                detailSave(idList);

                System.out.println("3초 대기 시작");
                Thread.sleep(3000); // 10초 대기
                System.out.println("3초 대기 종료");

            }
            catch (Exception e) {
                log.error("상세정보 업데이트 실패");
                log.error(i-1 + "번째 페이지까지 업데이트 완료");
                break;
            }

        }


    }






}