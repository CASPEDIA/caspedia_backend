package com.cast.caspedia.notification.repository;

import com.cast.caspedia.notification.domain.Notification;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    /**
     * 읽지 않은 알림 개수 조회
     * Requirements: 5.1, 5.2
     * @param recipient 알림 수신자
     * @param isRead 읽음 여부
     * @return 읽지 않은 알림 개수
     */
    int countByRecipientAndIsRead(User recipient, boolean isRead);
    
    /**
     * 사용자의 알림 목록 조회 (최신순, 최대 300개)
     * Requirements: 6.1, 6.2, 6.3
     * @param recipient 알림 수신자
     * @return 알림 목록 (최신순 정렬, 최대 300개)
     */
    List<Notification> findTop300ByRecipientOrderByCreatedAtDesc(User recipient);
    
    /**
     * 특정 알림 조회 (읽음 처리 시 권한 확인용)
     * Requirements: 7.1
     * @param notificationKey 알림 키
     * @param recipient 알림 수신자
     * @return 알림 엔티티
     */
    Optional<Notification> findByNotificationKeyAndRecipient(Integer notificationKey, User recipient);
}
