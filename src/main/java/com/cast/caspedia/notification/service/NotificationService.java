package com.cast.caspedia.notification.service;

import com.cast.caspedia.boardgame.domain.Boardgame;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.notification.domain.Notification;
import com.cast.caspedia.notification.domain.NotificationType;
import com.cast.caspedia.notification.domain.NotificationTypeCode;
import com.cast.caspedia.notification.dto.NotificationCountDto;
import com.cast.caspedia.notification.dto.NotificationInfoDto;
import com.cast.caspedia.notification.repository.NotificationRepository;
import com.cast.caspedia.notification.repository.NotificationTypeRepository;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.RatingImpressed;
import com.cast.caspedia.rating.domain.Reply;
import com.cast.caspedia.rating.domain.ReplyImpressed;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.user.domain.Like;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 생성 및 관리 서비스
 * Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 9.4, 9.5
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    
    /**
     * 댓글 알림 생성
     * Requirements: 1.1, 1.2, 1.3, 1.4
     * 
     * @param reply 생성된 댓글
     */
    public void createReplyNotification(Reply reply) {
        Rating rating = reply.getRating();
        User actor = reply.getUser();
        User recipient = rating.getUser();
        
        // 자기 자신에게 알림 생성 방지 (Requirement 1.4)
        if (actor.getId().equals(recipient.getId())) {
            return;
        }
        
        // 알림 타입 조회 (Requirement 1.2)
        NotificationType notificationType = notificationTypeRepository
                .findByCode(NotificationTypeCode.REPLY_ON_RATING.getCode())
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + NotificationTypeCode.REPLY_ON_RATING.getCode()));
        
        // 알림 생성 (Requirement 1.1, 1.3)
        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setRating(rating);
        notification.setReply(reply);
        notification.setBoardgame(rating.getBoardgame());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setDescription(createReplyNotificationDescription(actor));
        
        notificationRepository.save(notification);
    }
    
    /**
     * 한줄평 좋아요 알림 생성
     * Requirements: 2.1, 2.2, 2.5
     * 
     * @param ratingImpressed 생성된 한줄평 좋아요
     */
    public void createRatingImpressedNotification(RatingImpressed ratingImpressed) {
        Rating rating = ratingImpressed.getRating();
        User actor = ratingImpressed.getUser();
        User recipient = rating.getUser();
        
        // 자기 자신에게 알림 생성 방지 (Requirement 2.5)
        if (actor.getId().equals(recipient.getId())) {
            return;
        }
        
        // 알림 타입 조회 (Requirement 2.2)
        NotificationType notificationType = notificationTypeRepository
                .findByCode(NotificationTypeCode.IMPRESSED_ON_RATING.getCode())
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + NotificationTypeCode.IMPRESSED_ON_RATING.getCode()));
        
        // 알림 생성 (Requirement 2.1)
        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setRating(rating);
        notification.setBoardgame(rating.getBoardgame());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setDescription(createRatingImpressedNotificationDescription(actor));
        
        notificationRepository.save(notification);
    }
    
    /**
     * 댓글 좋아요 알림 생성
     * Requirements: 2.3, 2.4, 2.5
     * 
     * @param replyImpressed 생성된 댓글 좋아요
     */
    public void createReplyImpressedNotification(ReplyImpressed replyImpressed) {
        Reply reply = replyImpressed.getReply();
        User actor = replyImpressed.getUser();
        User recipient = reply.getUser();
        
        // 자기 자신에게 알림 생성 방지 (Requirement 2.5)
        if (actor.getId().equals(recipient.getId())) {
            return;
        }
        
        // 알림 타입 조회 (Requirement 2.4)
        NotificationType notificationType = notificationTypeRepository
                .findByCode(NotificationTypeCode.IMPRESSED_ON_REPLY.getCode())
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + NotificationTypeCode.IMPRESSED_ON_REPLY.getCode()));
        
        // 알림 생성 (Requirement 2.3)
        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setRating(reply.getRating());
        notification.setReply(reply);
        notification.setBoardgame(reply.getRating().getBoardgame());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setDescription(createReplyImpressedNotificationDescription(actor));
        
        notificationRepository.save(notification);
    }
    
    /**
     * 한줄평 작성 시 다중 알림 생성
     * - 해당 게임에 한줄평을 남긴 다른 사용자들에게 알림
     * - 해당 게임에 좋아요를 누른 다른 사용자들에게 알림
     * Requirements: 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4
     * 
     * @param rating 생성된 한줄평
     */
    public void createRatingNotifications(Rating rating) {
        User actor = rating.getUser();
        Boardgame boardgame = rating.getBoardgame();
        LocalDateTime now = LocalDateTime.now();
        
        // 배치 insert를 위한 알림 리스트
        List<Notification> notificationsToSave = new java.util.ArrayList<>();
        
        // 1. 해당 게임에 한줄평을 남긴 다른 사용자들에게 알림 생성 (Requirements 3.1, 3.2, 3.3, 3.4)
        List<Rating> existingRatings = ratingRepository.findAllRatingByBoardgame(boardgame);
        NotificationType ratingOnRatedType = notificationTypeRepository
                .findByCode(NotificationTypeCode.RATING_ON_RATED_BOARDGAME.getCode())
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + NotificationTypeCode.RATING_ON_RATED_BOARDGAME.getCode()));
        
        for (Rating existingRating : existingRatings) {
            User recipient = existingRating.getUser();
            
            // 자기 자신에게 알림 생성 방지 (Requirement 3.4)
            if (actor.getId().equals(recipient.getId())) {
                continue;
            }
            
            Notification notification = new Notification();
            notification.setNotificationType(ratingOnRatedType);
            notification.setRecipient(recipient);
            notification.setActor(actor);
            notification.setRating(rating);
            notification.setBoardgame(boardgame);
            notification.setRead(false);
            notification.setCreatedAt(now);
            notification.setUpdatedAt(now);
            notification.setDescription(createRatingOnRatedBoardgameNotificationDescription(actor, boardgame));
            
            notificationsToSave.add(notification);
        }
        
        // 2. 해당 게임에 좋아요를 누른 다른 사용자들에게 알림 생성 (Requirements 4.1, 4.2, 4.3, 4.4)
        List<Like> likes = likeRepository.findAllByBoardgame(boardgame);
        NotificationType ratingOnLikedType = notificationTypeRepository
                .findByCode(NotificationTypeCode.RATING_ON_LIKED_BOARDGAME.getCode())
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + NotificationTypeCode.RATING_ON_LIKED_BOARDGAME.getCode()));
        
        for (Like like : likes) {
            User recipient = like.getUser();
            
            // 자기 자신에게 알림 생성 방지 (Requirement 4.4)
            if (actor.getId().equals(recipient.getId())) {
                continue;
            }
            
            Notification notification = new Notification();
            notification.setNotificationType(ratingOnLikedType);
            notification.setRecipient(recipient);
            notification.setActor(actor);
            notification.setRating(rating);
            notification.setBoardgame(boardgame);
            notification.setRead(false);
            notification.setCreatedAt(now);
            notification.setUpdatedAt(now);
            notification.setDescription(createRatingOnLikedBoardgameNotificationDescription(actor, boardgame));
            
            notificationsToSave.add(notification);
        }
        
        // 배치 insert 수행 (성능 최적화)
        if (!notificationsToSave.isEmpty()) {
            notificationRepository.saveAll(notificationsToSave);
        }
    }
    
    /**
     * 댓글 알림 설명 생성
     * Requirements: 9.4
     * 
     * @param actor 알림 발생자
     * @return 알림 설명
     */
    private String createReplyNotificationDescription(User actor) {
        return String.format("%s님이 회원님의 한줄평에 댓글을 남겼습니다.", actor.getNickname());
    }
    
    /**
     * 한줄평 좋아요 알림 설명 생성
     * Requirements: 9.4
     * 
     * @param actor 알림 발생자
     * @return 알림 설명
     */
    private String createRatingImpressedNotificationDescription(User actor) {
        return String.format("%s님이 회원님의 한줄평에 좋아요를 눌렀습니다.", actor.getNickname());
    }
    
    /**
     * 댓글 좋아요 알림 설명 생성
     * Requirements: 9.4
     * 
     * @param actor 알림 발생자
     * @return 알림 설명
     */
    private String createReplyImpressedNotificationDescription(User actor) {
        return String.format("%s님이 회원님의 댓글에 좋아요를 눌렀습니다.", actor.getNickname());
    }
    
    /**
     * 한줄평 작성 알림 설명 생성 (한줄평 남긴 게임)
     * Requirements: 9.4
     * 
     * @param actor 알림 발생자
     * @param boardgame 보드게임
     * @return 알림 설명
     */
    private String createRatingOnRatedBoardgameNotificationDescription(User actor, Boardgame boardgame) {
        return String.format("%s님이 %s에 한줄평을 남겼습니다.", actor.getNickname(), boardgame.getNameKor());
    }
    
    /**
     * 한줄평 작성 알림 설명 생성 (좋아요 누른 게임)
     * Requirements: 9.4
     * 
     * @param actor 알림 발생자
     * @param boardgame 보드게임
     * @return 알림 설명
     */
    private String createRatingOnLikedBoardgameNotificationDescription(User actor, Boardgame boardgame) {
        return String.format("%s님이 회원님이 좋아요한 %s에 한줄평을 남겼습니다.", actor.getNickname(), boardgame.getNameKor());
    }
    
    /**
     * 읽지 않은 알림 개수 조회
     * Requirements: 5.1, 5.2, 5.3
     * 
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 개수
     */
    @Transactional(readOnly = true)
    public NotificationCountDto getUnreadCount(String userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        // Requirement 5.1, 5.2: is_read가 false인 알림의 개수를 반환
        int count = notificationRepository.countByRecipientAndIsRead(user, false);
        
        // Requirement 5.3: count 필드를 포함한 JSON 형식으로 응답
        NotificationCountDto dto = new NotificationCountDto();
        dto.setCount(count);
        return dto;
    }
    
    /**
     * 알림 목록 조회
     * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5
     * 
     * @param userId 사용자 ID
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationInfoDto> getNotifications(String userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        // Requirement 6.1, 6.2, 6.3: 해당 사용자를 recipient로 하는 모든 알림을 최신순으로 최대 300개 반환
        List<Notification> notifications = notificationRepository.findTop300ByRecipientOrderByCreatedAtDesc(user);
        
        // Entity를 DTO로 변환
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 알림 읽음 처리
     * Requirements: 7.1, 7.2, 7.3, 7.4, 7.5
     * 
     * @param notificationKey 알림 키
     * @param userId 사용자 ID
     */
    public void markAsRead(Integer notificationKey, String userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new AppException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        // Requirement 7.4, 7.5: 존재하지 않는 알림 또는 다른 사용자의 알림 접근 예외 처리
        Notification notification = notificationRepository.findByNotificationKeyAndRecipient(notificationKey, user)
                .orElseThrow(() -> new AppException("해당 알림이 존재하지 않거나 접근 권한이 없습니다.", HttpStatus.FORBIDDEN));
        
        // Requirement 7.1, 7.2, 7.3: is_read를 true로 변경, read_at과 updated_at을 현재 시간으로 설정
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * Notification 엔티티를 NotificationInfoDto로 변환
     * Requirements: 6.4, 6.5
     * 
     * @param notification 알림 엔티티
     * @return 알림 DTO
     */
    private NotificationInfoDto convertToDto(Notification notification) {
        NotificationInfoDto dto = new NotificationInfoDto();
        dto.setNotificationKey(notification.getNotificationKey());
        dto.setCode(notification.getNotificationType().getCode());
        
        // Requirement 6.5: 해당하지 않는 키 값은 null로 반환
        dto.setRatingKey(notification.getRating() != null ? notification.getRating().getRatingKey() : null);
        dto.setReplyKey(notification.getReply() != null ? notification.getReply().getReplyKey() : null);
        dto.setBoardgameKey(notification.getBoardgame() != null ? notification.getBoardgame().getBoardgameKey() : null);
        
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        dto.setDescription(notification.getDescription());
        
        return dto;
    }
}
