package com.cast.caspedia.notification.controller;

import com.cast.caspedia.error.AppException;
import com.cast.caspedia.notification.dto.NotificationCountDto;
import com.cast.caspedia.notification.dto.NotificationInfoDto;
import com.cast.caspedia.notification.dto.NotificationReadRequestDto;
import com.cast.caspedia.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 REST API 컨트롤러
 * Requirements: 5.1, 5.2, 5.3, 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4, 7.5, 10.1, 10.2, 10.3
 */
@RestController
@RequestMapping(value = "/api/noti", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * 읽지 않은 알림 개수 조회
     * Requirements: 5.1, 5.2, 5.3, 10.1, 10.2
     * 
     * @return 읽지 않은 알림 개수
     */
    @GetMapping("/count")
    public ResponseEntity<NotificationCountDto> getUnreadCount() {
        // Requirement 10.1, 10.2: SecurityContext에서 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        if (userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        NotificationCountDto count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 알림 목록 조회
     * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 10.1, 10.2
     * 
     * @return 알림 목록
     */
    @GetMapping("/info")
    public ResponseEntity<List<NotificationInfoDto>> getNotifications() {
        // Requirement 10.1, 10.2: SecurityContext에서 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        if (userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        List<NotificationInfoDto> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * 알림 읽음 처리
     * Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 10.1, 10.2, 10.3
     * 
     * @param request 알림 읽음 처리 요청
     * @return 성공 응답
     */
    @PostMapping("/read")
    public ResponseEntity<?> markAsRead(@RequestBody NotificationReadRequestDto request) {
        // Requirement 10.1, 10.2: SecurityContext에서 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        if (userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        // 요청 파라미터 검증
        if (request.getNotificationKey() == null) {
            throw new AppException("알림 키가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
        
        // Requirement 10.3: 요청한 사용자가 해당 알림의 recipient인지 확인 (서비스 레이어에서 처리)
        notificationService.markAsRead(request.getNotificationKey(), userId);
        
        return ResponseEntity.ok().build();
    }
}
