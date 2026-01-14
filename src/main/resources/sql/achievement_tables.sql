-- 업적 시스템 테이블 생성 스크립트
-- 실행 순서: 1. 테이블 생성 -> 2. 외래키 설정 -> 3. 초기 데이터

-- 1. 로그인 기록 테이블
CREATE TABLE IF NOT EXISTS user_login_history (
    login_key SERIAL PRIMARY KEY,
    user_key INTEGER NOT NULL,
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 업적 정의 테이블 (이벤트/히든 전용)
CREATE TABLE IF NOT EXISTS achievement (
    achievement_key SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL
);

-- 3. 유저별 이벤트 업적 달성 테이블
CREATE TABLE IF NOT EXISTS user_achievement (
    user_key INTEGER NOT NULL,
    achievement_key INTEGER NOT NULL,
    achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_key, achievement_key)
);

-- 외래 키 설정
ALTER TABLE user_login_history
    ADD CONSTRAINT FK_user_login_history_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

ALTER TABLE user_achievement
    ADD CONSTRAINT FK_user_achievement_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

ALTER TABLE user_achievement
    ADD CONSTRAINT FK_user_achievement_achievement
        FOREIGN KEY (achievement_key)
            REFERENCES achievement (achievement_key);

-- 초기 데이터: 병민스크루 이벤트 뱃지
INSERT INTO achievement (name, type) VALUES ('byunmin_crew', 'event');
