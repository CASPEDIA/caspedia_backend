package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.domain.BoardgameCategory;
import com.cast.caspedia.boardgame.domain.BoardgameMechanic;
import com.cast.caspedia.boardgame.domain.StagingBoardgame;
import com.cast.caspedia.boardgame.repository.BoardgameCategoryRepository;
import com.cast.caspedia.boardgame.repository.BoardgameMechanicRepository;
import com.cast.caspedia.boardgame.repository.BoardgameRepository;
import com.cast.caspedia.boardgame.repository.StagingBoardgameRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BggIntegrationService {

    private final StagingBoardgameRepository stagingRepo;
    private final BoardgameRepository boardgameRepo;
    private final BoardgameCategoryRepository categoryRepo;
    private final BoardgameMechanicRepository mechanicRepo;
    private final ObjectMapper objectMapper;
    private static final int BATCH_SIZE = 100; // 한 번에 처리할 배치 크기

    private static final Pattern KOREAN_PATTERN = Pattern.compile(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");

    @Transactional
    public void integrateData() {
        log.info("🚀 데이터 통합 배치 작업을 시작합니다... (배치 사이즈: {})", BATCH_SIZE);
        long totalProcessedCount = 0;

        // 1. 첫 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<StagingBoardgame> stagedGamesPage;

        do {
            // 2. Staging DB에서 페이지 단위로 데이터 조회
            stagedGamesPage = stagingRepo.findAll(pageable);
            List<StagingBoardgame> stagedGamesInBatch = stagedGamesPage.getContent();

            if (stagedGamesInBatch.isEmpty()) {
                break;
            }

            // 3. [최적화] 현재 배치의 boardgameKey 목록 추출
            List<Integer> keys = stagedGamesInBatch.stream()
                    .map(StagingBoardgame::getBoardgameKey)
                    .toList();

            // 4. [최적화] 키 목록을 사용해 기존 Boardgame 엔티티들을 DB에서 '한 번에' 조회
            Map<Integer, Boardgame> existingBoardgamesMap = boardgameRepo.findAllById(keys).stream()
                    .collect(Collectors.toMap(Boardgame::getBoardgameKey, Function.identity()));

            List<Boardgame> boardgamesToSave = new ArrayList<>();

            for (StagingBoardgame stagedGame : stagedGamesInBatch) {
                try {
                    // 5. DB를 다시 조회하는 대신, Map에서 엔티티를 가져옴 (없으면 새로 생성)
                    Boardgame boardgame = existingBoardgamesMap.getOrDefault(stagedGame.getBoardgameKey(), new Boardgame());
                    boardgame.setBoardgameKey(stagedGame.getBoardgameKey());

                    // 데이터 매핑
                    mapBasicInfo(boardgame, stagedGame);
                    mapLinks(boardgame, stagedGame.getLinks());

                    boardgamesToSave.add(boardgame);
                } catch (Exception e) {
                    log.error("ID {} 통합 처리 중 오류 발생", stagedGame.getBoardgameKey(), e);
                }
            }

            // 6. 현재 배치에서 처리된 엔티티들을 '한 번에' 저장
            if (!boardgamesToSave.isEmpty()) {
                boardgameRepo.saveAll(boardgamesToSave);
            }

            log.info("📄 페이지 {} / {} 처리 완료. ({}개 항목 처리)",
                    stagedGamesPage.getNumber() + 1, stagedGamesPage.getTotalPages(), stagedGamesInBatch.size());

            totalProcessedCount += stagedGamesInBatch.size();

            // 7. 다음 페이지 요청
            pageable = stagedGamesPage.nextPageable();

        } while (stagedGamesPage.hasNext());

        log.info("✅ 총 {}개 항목에 대한 데이터 통합 작업을 완료했습니다.", totalProcessedCount);
    }

    private void mapBasicInfo(Boardgame boardgame, StagingBoardgame stagedGame) throws JsonProcessingException {
        boardgame.setNameEng(stagedGame.getNameEng());
        boardgame.setImageUrl(stagedGame.getImageUrl());
        boardgame.setYearPublished(stagedGame.getYearpublished());
        boardgame.setMinPlayers(stagedGame.getMinplayers());
        boardgame.setMaxPlayers(stagedGame.getMaxplayers());
        boardgame.setMinPlaytime(stagedGame.getMinplaytime());
        boardgame.setMaxPlaytime(stagedGame.getMaxplaytime());
        boardgame.setAge(stagedGame.getAge());
        boardgame.setDescription(stagedGame.getDescription());
        boardgame.setGeekScore(stagedGame.getGeekScore());
        boardgame.setGeekWeight(stagedGame.getGeekWeight());

        // 'names' JSON에서 한글 이름 추출
        List<Map<String, String>> names = objectMapper.readValue(stagedGame.getNames(), new TypeReference<>() {});
        String koreanName = names.stream()
                .filter(name -> KOREAN_PATTERN.matcher(name.getOrDefault("value", "")).matches())
                .map(name -> name.get("value"))
                .findFirst()
                .orElse(boardgame.getNameKor()); // 기존 한글 이름 유지
        boardgame.setNameKor(koreanName);

        // 'designer' JSON에서 디자이너 목록을 쉼표로 구분된 문자열로 변환
        List<DesignerDto> designers = objectMapper.readValue(stagedGame.getDesigner(), new TypeReference<>() {});
        String designerText = designers.stream()
                .map(DesignerDto::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        boardgame.setDesigner(designerText);
    }

    private void mapLinks(Boardgame boardgame, String linksJson) throws JsonProcessingException {
        if (linksJson == null || linksJson.isEmpty()) return;

        // 1. 기존 연관관계 레코드를 모두 제거할 준비를 합니다.
        // (orphanRemoval=true 덕분에 컬렉션에서 지우면 DB에서도 DELETE 쿼리가 나갑니다)
        boardgame.getCategories().clear();
        boardgame.getMechanics().clear();

        List<LinkDto> links = objectMapper.readValue(linksJson, new TypeReference<>() {});

        for (LinkDto link : links) {
            if ("boardgamecategory".equals(link.getType())) {
                // 2. 새로운 '관계 엔티티(BoardgameCategory)' 인스턴스를 생성합니다.
                BoardgameCategory newCategoryLink = new BoardgameCategory();

                // 3. 관계의 주인(Boardgame)과 BGG의 카테고리 정보를 설정합니다.
                newCategoryLink.setBoardgame(boardgame); // ★★★ 부모 엔티티(Boardgame)를 설정해주는 것이 핵심입니다.
                newCategoryLink.setCategoryId(link.getId());
                newCategoryLink.setCategoryValue(link.getValue());

                // 4. 부모 엔티티의 컬렉션에 자식(관계 엔티티)을 추가합니다.
                // (cascade=ALL 덕분에 boardgame이 저장될 때 newCategoryLink도 함께 저장됩니다)
                boardgame.getCategories().add(newCategoryLink);

            } else if ("boardgamemechanic".equals(link.getType())) {
                // 메카닉도 동일한 로직으로 처리합니다.
                BoardgameMechanic newMechanicLink = new BoardgameMechanic();
                newMechanicLink.setBoardgame(boardgame);
                newMechanicLink.setMechanicId(link.getId());
                newMechanicLink.setMechanicValue(link.getValue());
                boardgame.getMechanics().add(newMechanicLink);
            }
        }
    }

    // JSON 파싱용 DTO
    @Getter @Setter
    private static class LinkDto {
        private String type;
        private int id;
        private String value;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DesignerDto {
        private int id;
        private String value;
    }
}