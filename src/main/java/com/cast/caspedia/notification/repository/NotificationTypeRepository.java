package com.cast.caspedia.notification.repository;

import com.cast.caspedia.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
    /**
     * 알림 타입 코드로 알림 타입 조회
     * @param code 알림 타입 코드 (예: "REPLY_ON_RATING")
     * @return 알림 타입 엔티티
     */
    Optional<NotificationType> findByCode(String code);
}
