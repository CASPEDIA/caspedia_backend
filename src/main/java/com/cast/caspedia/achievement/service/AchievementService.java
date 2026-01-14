package com.cast.caspedia.achievement.service;

import com.cast.caspedia.achievement.domain.UserAchievement;
import com.cast.caspedia.achievement.domain.UserLoginHistory;
import com.cast.caspedia.achievement.dto.AchievementResponseDto;
import com.cast.caspedia.achievement.dto.CommonBadgeDto;
import com.cast.caspedia.achievement.repository.UserAchievementRepository;
import com.cast.caspedia.achievement.repository.UserLoginHistoryRepository;
import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.rating.repository.ReplyRepository;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AchievementService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Transactional(readOnly = true)
    public AchievementResponseDto getAchievement(String nanoid) {
        User user = userRepository.findByNanoid(nanoid);
        if (user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // Common badge counts
        int loginCount = userLoginHistoryRepository.countByUser(user);
        int ratingCount = ratingRepository.countByUser(user);
        int commentCount = replyRepository.countByUser(user);
        int likeCount = likeRepository.countByUser(user);

        CommonBadgeDto commonBadge = new CommonBadgeDto(loginCount, ratingCount, commentCount, likeCount);

        // Event badges
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
        List<String> eventBadge = userAchievements.stream()
                .map(ua -> ua.getAchievement().getName())
                .collect(Collectors.toList());

        return new AchievementResponseDto(commonBadge, eventBadge);
    }

    @Transactional
    public void recordLogin(User user) {
        // 서울 시간 기준 오늘 날짜
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // 오늘 이미 로그인 기록이 있는지 확인
        boolean alreadyLoggedInToday = userLoginHistoryRepository.existsByUserAndLoginAtBetween(user, startOfDay, endOfDay);

        if (!alreadyLoggedInToday) {
            UserLoginHistory loginHistory = new UserLoginHistory(user);
            userLoginHistoryRepository.save(loginHistory);
            log.info("로그인 기록 저장: user_key={}, date={}", user.getUserKey(), today);
        } else {
            log.info("오늘 이미 로그인 기록 있음: user_key={}, date={}", user.getUserKey(), today);
        }
    }
}
