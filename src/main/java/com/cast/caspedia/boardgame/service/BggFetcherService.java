package com.cast.caspedia.boardgame.service;

import com.cast.caspedia.boardgame.config.BggConfig;
import com.cast.caspedia.boardgame.domain.StagingBoardgame;
import com.cast.caspedia.boardgame.dto.BggApiResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class BggFetcherService {

        private final BggConfig.BggProperties props;
        private final RestTemplate restTemplate;
        private final ObjectMapper objectMapper; // JSON 변환용
        private final JdbcTemplate jdbcTemplate; // 대량 데이터 저장을 위한 JdbcTemplate

        /**
         * XML 데이터를 Java 객체로 변환하기 위한 XmlMapper 빈을 생성합니다.
         * 알 수 없는 속성이 있어도 오류를 내지 않도록 설정합니다.
         */
        private XmlMapper xmlMapper() {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return xmlMapper;
        }
        private final XmlMapper xmlMapper = xmlMapper();

        /**
         * BGG API에서 모든 게임 데이터를 가져와 Staging DB에 저장하는 메인 메서드
         */
        public void fetchAllGames() {
            log.info("🚀 BGG Fetcher를 시작합니다... Batch Size: {}, Delay: {}초", props.getBatchSize(), props.getDelaySeconds());
            int emptyResponseCounter = 0;

            for (int i = props.getStartId(); i <= props.getEndId(); i += props.getBatchSize()) {
                List<Integer> batchIds = IntStream.range(i, Math.min(i + props.getBatchSize(), props.getEndId() + 1))
                        .boxed().toList();

                if (batchIds.isEmpty()) break;

                String ids = batchIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                String url = String.format("%s?id=%s&stats=1&type=boardgame,boardgameexpansion", props.getUrl(), ids);

                boolean batchSuccess = false;
                int maxRetries = 3;

                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                    try {
                        log.info("ID {}-{} 데이터 가져오는 중... (시도 {}/{})", batchIds.get(0), batchIds.get(batchIds.size() - 1), attempt, maxRetries);

                        String xmlResponse = restTemplate.getForObject(url, String.class);
                        BggApiResponseDto response = xmlMapper.readValue(xmlResponse, BggApiResponseDto.class);

                        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                            log.warn("ID {}-{} 에서 유효한 데이터를 찾지 못했습니다.", batchIds.get(0), batchIds.get(batchIds.size() - 1));
                            emptyResponseCounter++;
                            if (emptyResponseCounter >= props.getMaxEmptyResponses()) {
                                log.warn("연속 {}회 빈 응답이 감지되어 크롤링을 중단합니다.", emptyResponseCounter);
                                i = props.getEndId() + 1;
                            }
                        } else {
                            emptyResponseCounter = 0;
                            List<StagingBoardgame> gamesToSave = response.getItems().stream()
                                    .map(this::convertToStagingEntity)
                                    .filter(Objects::nonNull)
                                    .toList();
                            batchUpsert(gamesToSave);
                            log.info("💾 스테이징 DB에 {}개 항목 저장/업데이트 성공.", gamesToSave.size());
                        }

                        batchSuccess = true;
                        break;

                    } catch (HttpClientErrorException e) {
                        log.warn("ID {} 배치 처리 중 4xx 오류 발생 (상태 코드: {}).", ids, e.getStatusCode());
                        if (attempt == maxRetries) {
                            log.error("최대 재시도 횟수({})에 도달하여 ID {} 배치를 건너뜁니다.", maxRetries, ids, e);
                        } else {
                            log.info("30초 후 재시도합니다... ({}/{})", attempt, maxRetries);
                            try {
                                Thread.sleep(30000L);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                log.warn("재시도 대기 중 스레드가 중단되어 Fetcher를 종료합니다.");
                                i = props.getEndId() + 1;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("ID {} 배치 처리 중 재시도 불가능한 오류 발생. 이 배치를 건너뜁니다.", ids, e);
                        break;
                    }
                }

                if (batchSuccess) {
                    try {
                        Thread.sleep(props.getDelaySeconds() * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("API 딜레이 대기 중 스레드가 중단되어 Fetcher를 종료합니다.");
                        break;
                    }
                }
            }
            log.info("✅ BGG Fetcher 작업을 종료합니다.");
        }

        /**
         * BGG API DTO를 StagingBoardgame 엔티티로 변환합니다.
         */
        private StagingBoardgame convertToStagingEntity(BggApiResponseDto.Item item) {
            try {
                // 디자이너 정보만 별도로 추출
                List<BggApiResponseDto.Link> designers = item.getLinks().stream()
                        .filter(link -> "boardgamedesigner".equals(link.getType()))
                        .toList();

                BggApiResponseDto.Ratings ratings = item.getStatistics() != null ? item.getStatistics().getRatings() : null;

                float geekScore = (ratings != null && ratings.getAverage() != null)
                        ? ratings.getAverage().getValue() : 0.0f;
                float geekWeight = (ratings != null && ratings.getAverageweight() != null)
                        ? ratings.getAverageweight().getValue() : 0.0f;


                return StagingBoardgame.builder()
                        .boardgameKey(item.getId())
                        .nameEng(item.getNames().stream().filter(n -> "primary".equals(n.getType())).findFirst().map(BggApiResponseDto.Name::getValue).orElse(null))
                        .imageUrl(item.getImageUrl())
                        .yearpublished(item.getYearpublished() != null ? item.getYearpublished().getValue() : 0)
                        .minplayers(item.getMinplayers() != null ? item.getMinplayers().getValue() : 0)
                        .maxplayers(item.getMaxplayers() != null ? item.getMaxplayers().getValue() : 0)
                        .minplaytime(item.getMinplaytime() != null ? item.getMinplaytime().getValue() : 0)
                        .maxplaytime(item.getMaxplaytime() != null ? item.getMaxplaytime().getValue() : 0)
                        .age(item.getMinage() != null ? item.getMinage().getValue() : 0)
                        .description(item.getDescription())
                        .geekScore(geekScore)
                        .geekWeight(geekWeight)
                        .names(objectMapper.writeValueAsString(item.getNames()))
                        .links(objectMapper.writeValueAsString(item.getLinks()))
                        .designer(objectMapper.writeValueAsString(designers))
                        .build();
            } catch (JsonProcessingException e) {
                log.error("ID {} 의 JSON 변환 중 오류가 발생했습니다.", item.getId(), e);
                return null;
            }
        }

        /**
         * JdbcTemplate을 사용하여 Staging DB에 데이터를 일괄적으로 Upsert(Insert or Update)합니다.
         * JpaRepository의 saveAll 보다 훨씬 빠릅니다.
         */
        private void batchUpsert(List<StagingBoardgame> games) {
            if (games.isEmpty()) return;

            String sql = """
            INSERT INTO boardgames (boardgame_key, name_eng, image_url, yearpublished, minplayers, maxplayers, minplaytime, maxplaytime, age, geek_weight, geek_score, description, names, links, designer)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), CAST(? AS jsonb), CAST(? AS jsonb))
            ON CONFLICT (boardgame_key) DO UPDATE SET
                name_eng = EXCLUDED.name_eng,
                image_url = EXCLUDED.image_url,
                yearpublished = EXCLUDED.yearpublished,
                minplayers = EXCLUDED.minplayers,
                maxplayers = EXCLUDED.maxplayers,
                minplaytime = EXCLUDED.minplaytime,
                maxplaytime = EXCLUDED.maxplaytime,
                age = EXCLUDED.age,
                geek_weight = EXCLUDED.geek_weight,
                geek_score = EXCLUDED.geek_score,
                description = EXCLUDED.description,
                names = EXCLUDED.names,
                links = EXCLUDED.links,
                designer = EXCLUDED.designer
            """;

            jdbcTemplate.batchUpdate(sql, games, props.getBatchSize(), (ps, game) -> {
                ps.setInt(1, game.getBoardgameKey());
                ps.setString(2, game.getNameEng());
                ps.setString(3, game.getImageUrl());
                ps.setInt(4, game.getYearpublished());
                ps.setInt(5, game.getMinplayers());
                ps.setInt(6, game.getMaxplayers());
                ps.setInt(7, game.getMinplaytime());
                ps.setInt(8, game.getMaxplaytime());
                ps.setInt(9, game.getAge());
                ps.setDouble(10, game.getGeekWeight());
                ps.setDouble(11, game.getGeekScore());
                ps.setString(12, game.getDescription());
                ps.setString(13, game.getNames());
                ps.setString(14, game.getLinks());
                ps.setString(15, game.getDesigner());
            });
        }
    }