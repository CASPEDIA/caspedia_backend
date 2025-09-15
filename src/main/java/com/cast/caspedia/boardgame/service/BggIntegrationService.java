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

import java.util.*;
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
    private static final int BATCH_SIZE = 200;

    private static final Pattern KOREAN_PATTERN = Pattern.compile(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");

    @Transactional
    public void integrateData() {
        log.info("🚀 데이터 통합 배치 작업을 시작합니다... (배치 사이즈: {})", BATCH_SIZE);
        long totalProcessedCount = 0;

        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<StagingBoardgame> stagedGamesPage;

        do {
            stagedGamesPage = stagingRepo.findAll(pageable);
            List<StagingBoardgame> stagedGamesInBatch = stagedGamesPage.getContent();

            if (stagedGamesInBatch.isEmpty()) break;

            List<Integer> keys = stagedGamesInBatch.stream()
                    .map(StagingBoardgame::getBoardgameKey)
                    .toList();

            Map<Integer, Boardgame> existingBoardgamesMap = boardgameRepo.findAllById(keys).stream()
                    .collect(Collectors.toMap(Boardgame::getBoardgameKey, Function.identity()));

            List<Boardgame> boardgamesToSave = new ArrayList<>();

            for (StagingBoardgame stagedGame : stagedGamesInBatch) {
                try {
                    Boardgame boardgame = existingBoardgamesMap.getOrDefault(
                            stagedGame.getBoardgameKey(), new Boardgame());
                    boardgame.setBoardgameKey(stagedGame.getBoardgameKey());

                    mapBasicInfo(boardgame, stagedGame);
                    mapLinks(boardgame, stagedGame.getLinks());

                    boardgamesToSave.add(boardgame);
                } catch (Exception e) {
                    log.error("ID {} 통합 처리 중 오류 발생", stagedGame.getBoardgameKey(), e);
                }
            }

            if (!boardgamesToSave.isEmpty()) boardgameRepo.saveAll(boardgamesToSave);

            log.info("📄 페이지 {} / {} 처리 완료. ({}개 항목 처리)",
                    stagedGamesPage.getNumber() + 1, stagedGamesPage.getTotalPages(), stagedGamesInBatch.size());

            totalProcessedCount += stagedGamesInBatch.size();
            pageable = stagedGamesPage.nextPageable();

        } while (stagedGamesPage.hasNext());

        log.info("✅ 총 {}개 항목에 대한 데이터 통합 작업을 완료했습니다.", totalProcessedCount);
    }

    /** -------------------- 핵심 변경: 안전 매핑 -------------------- **/
    private void mapBasicInfo(Boardgame boardgame, StagingBoardgame stagedGame) {
        // 문자열은 "" 기본값
        boardgame.setNameEng(s(stagedGame.getNameEng()));
        boardgame.setImageUrl(s(stagedGame.getImageUrl()));
        boardgame.setDescription(s(stagedGame.getDescription()));

        // 숫자는 0 기본값
        boardgame.setYearPublished(i(stagedGame.getYearpublished()));
        boardgame.setMinPlayers(i(stagedGame.getMinplayers()));
        boardgame.setMaxPlayers(i(stagedGame.getMaxplayers()));
        boardgame.setMinPlaytime(i(stagedGame.getMinplaytime()));
        boardgame.setMaxPlaytime(i(stagedGame.getMaxplaytime()));
        boardgame.setAge(i(stagedGame.getAge()));
        boardgame.setGeekScore(f(stagedGame.getGeekScore()));
        boardgame.setGeekWeight(f(stagedGame.getGeekWeight()));

        // 'names' JSON에서 첫 한글 이름 추출 (없으면 기존 값, 그마저 없으면 "")
        List<Map<String, String>> names = parseListOrEmpty(stagedGame.getNames(), new TypeReference<>() {});
        String koreanName = names.stream()
                .map(m -> m.getOrDefault("value", ""))
                .filter(v -> !v.isBlank())
                .filter(v -> KOREAN_PATTERN.matcher(v).matches())
                .findFirst()
                .orElse(s(boardgame.getNameKor()));
        boardgame.setNameKor(s(koreanName));

        // 'designer' JSON → "a, b, c" 문자열 (없으면 "")
        List<DesignerDto> designers = parseListOrEmpty(stagedGame.getDesigner(), new TypeReference<>() {});
        String designerText = designers.stream()
                .map(DesignerDto::getValue)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.joining(", "));
        boardgame.setDesigner(s(designerText));
    }

    private void mapLinks(Boardgame boardgame, String linksJson) {
        // 부모-자식 관계 초기화 (null 세이프)
        if (boardgame.getCategories() != null) boardgame.getCategories().clear();
        if (boardgame.getMechanics() != null) boardgame.getMechanics().clear();

        // 링크 JSON이 없거나 비어있으면 종료
        if (s(linksJson).isEmpty()) return;

        List<LinkDto> links = parseListOrEmpty(linksJson, new TypeReference<>() {});

        for (LinkDto link : links) {
            if ("boardgamecategory".equals(s(link.getType()))) {
                BoardgameCategory newCategoryLink = new BoardgameCategory();
                newCategoryLink.setBoardgame(boardgame);
                newCategoryLink.setCategoryId(i(link.getId()));        // int 기본 0
                newCategoryLink.setCategoryValue(s(link.getValue()));  // "" 기본
                boardgame.getCategories().add(newCategoryLink);
            } else if ("boardgamemechanic".equals(s(link.getType()))) {
                BoardgameMechanic newMechanicLink = new BoardgameMechanic();
                newMechanicLink.setBoardgame(boardgame);
                newMechanicLink.setMechanicId(i(link.getId()));
                newMechanicLink.setMechanicValue(s(link.getValue()));
                boardgame.getMechanics().add(newMechanicLink);
            }
        }
    }

    /** -------------------- 유틸: 기본값 강제 -------------------- **/
    private static String s(String v) {
        return (v == null || v.isBlank()) ? "" : v;
    }
    private static Integer i(Integer v) {
        return (v == null) ? 0 : v;
    }
    private static Long l(Long v) {
        return (v == null) ? 0L : v;
    }

    private static Float f(Number v) {
        return (v == null) ? 0.0f : v.floatValue();
    }

    private <T> List<T> parseListOrEmpty(String json, TypeReference<List<T>> type) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("⚠️ JSON 파싱 실패 → 빈 리스트로 처리합니다. payload={}", json);
            return Collections.emptyList();
        }
    }

    // JSON 파싱용 DTO
    @Getter @Setter
    private static class LinkDto {
        private String type;
        private Integer id;     // ← 널 안전을 위해 Integer로
        private String value;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DesignerDto {
        private Integer id;     // ← 널 안전을 위해 Integer로
        private String value;
    }
}
