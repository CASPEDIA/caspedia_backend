package com.cast.caspedia.boardgame.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 이 클래스를 스프링 빈으로 등록합니다.
@RequiredArgsConstructor
public class BggUpdateScheduler {

    private final BggFetcherService fetcherService;
    private final BggIntegrationService integrationService;

    /**
     * 매일 새벽 4시에 BGG 데이터 업데이트 전체 프로세스를 실행합니다.
     * cron = "초 분 시 일 월 요일"
     * 0 0 4 * * * : 매일 새벽 4시 0분 0초
     */
    @Scheduled(cron = "0 0 7 * * MON") // 매주 월요일 오전 07시에 실행
    public void runFullUpdateProcess() {
        log.info("🚀 BGG 데이터 업데이트 스케줄을 시작합니다.");
        try {
            // 1단계: BGG API에서 데이터를 가져와 Staging DB에 저장
            fetcherService.fetchAllGames();

            // 2단계: Staging DB의 데이터를 운영 DB로 통합
            integrationService.integrateData();

            log.info("✅ BGG 데이터 업데이트 스케줄을 성공적으로 완료했습니다.");
        } catch (Exception e) {
            log.error("❌ BGG 데이터 업데이트 스케줄 실행 중 오류가 발생했습니다.", e);
        }
    }
}