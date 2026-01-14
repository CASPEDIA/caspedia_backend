-- 모든 테이블 삭제 (외래 키 관계를 고려하여 CASCADE 사용)
DROP TABLE IF EXISTS user_achievement CASCADE;
DROP TABLE IF EXISTS achievement CASCADE;
DROP TABLE IF EXISTS user_login_history CASCADE;
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS notification_type CASCADE;
DROP TABLE IF EXISTS announcement CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS rating CASCADE;
DROP TABLE IF EXISTS rating_tag CASCADE;
DROP TABLE IF EXISTS tag CASCADE;
DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS user_image CASCADE;
DROP TABLE IF EXISTS "like" CASCADE;
DROP TABLE IF EXISTS boardgame CASCADE;

-- 공지 테이블 (announcement)
CREATE TABLE announcement (
                              announcement_key serial PRIMARY KEY,
                              title varchar NOT NULL,
                              content varchar NOT NULL DEFAULT '',
                              created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                              updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 회원 테이블 (user)
CREATE TABLE "user" (
                        user_key serial PRIMARY KEY,
                        id varchar NOT NULL UNIQUE,
                        password varchar NOT NULL,
                        nickname varchar NOT NULL UNIQUE,
                        name varchar NOT NULL,
                        nanoid varchar NOT NULL UNIQUE,
                        introduction TEXT NOT NULL DEFAULT '',
                        student_id int NOT NULL UNIQUE,
                        enabled boolean NOT NULL DEFAULT true,
                        authority_key int NOT NULL,  -- 외래 키 추가
                        user_image_key int NOT NULL,  -- 외래 키 추가
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 평가 테이블 (rating)
CREATE TABLE rating (
                        rating_key serial PRIMARY KEY,
                        score int NOT NULL DEFAULT 1,
                        comment TEXT NOT NULL DEFAULT '',
                        tag_key varchar(50) NOT NULL DEFAULT '000000000000000000000000',
                        cast_score FLOAT NOT NULL DEFAULT 0,
                        boardgame_key int NOT NULL,  -- 외래 키 추가
                        user_key int NOT NULL,  -- 외래 키 추가
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);


-- 태그 테이블 (tag)
CREATE TABLE tag (
                     tag_key serial PRIMARY KEY,
                     name varchar NOT NULL,
                     created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                     updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 권한 테이블 (authority)
CREATE TABLE authority (
                           authority_key serial PRIMARY KEY,
                           role varchar NOT NULL DEFAULT 'ROLE_USER',
                           created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                           updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 회원 이미지 테이블 (user_image)
CREATE TABLE user_image (
                            user_image_key serial PRIMARY KEY,
                            path varchar NOT NULL,
                            name varchar NOT NULL,
                            created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                            updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 좋아요 테이블 (like)
CREATE TABLE "like" (
                        like_key serial PRIMARY KEY,
                        user_key int NOT NULL,  -- 외래 키 추가
                        boardgame_key int NOT NULL,  -- 외래 키 추가
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 보드게임 테이블 (boardgame)
CREATE TABLE boardgame (
                           boardgame_key int PRIMARY KEY,
                           name_kor varchar NOT NULL DEFAULT '',
                           name_eng varchar NOT NULL DEFAULT '',
                           image_url varchar NOT NULL DEFAULT '',
                           year_published int NOT NULL DEFAULT 0,
                           description varchar NOT NULL DEFAULT '',
                           min_players int NOT NULL DEFAULT 0,
                           max_players int NOT NULL DEFAULT 0,
                           min_playtime int NOT NULL DEFAULT 0,
                           max_playtime int NOT NULL DEFAULT 0,
                           age int NOT NULL DEFAULT 0,
                           cast_owned boolean NOT NULL DEFAULT false,
                           geek_weight float NOT NULL DEFAULT 0,
                           geek_score float NOT NULL DEFAULT 0,
                           cast_score FLOAT NOT NULL DEFAULT 0,
                           designer TEXT NOT NULL DEFAULT '',
                           created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                           updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

drop table if exists boardgame_category;
drop table if exists boardgame_mechanic;

CREATE TABLE boardgame_category (
                                    id SERIAL PRIMARY KEY,
                                    boardgame_key INTEGER REFERENCES boardgame(boardgame_key),
                                    category_id INTEGER,
                                    category_value TEXT
);

CREATE TABLE boardgame_mechanic (
                                    id SERIAL PRIMARY KEY,
                                    boardgame_key INTEGER REFERENCES boardgame(boardgame_key),
                                    mechanic_id INTEGER,
                                    mechanic_value TEXT
);

create table boardgame_mechanic_kor
(
    id SERIAL PRIMARY KEY,
    mechanic_id integer,
    name text
);

create table boardgame_category_kor
(
    id SERIAL PRIMARY KEY,
    category_id integer,
    name text
);

-- 알림 타입 테이블 (notification_type)
CREATE TABLE notification_type (
    notification_type_key SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 알림 테이블 (notification)
CREATE TABLE notification (
    notification_key SERIAL PRIMARY KEY,
    notification_type_key INTEGER NOT NULL,
    recipient_user_key INTEGER NOT NULL,
    actor_user_key INTEGER NOT NULL,
    rating_key INTEGER,
    reply_key INTEGER,
    boardgame_key INTEGER,
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255) NOT NULL
);

-- 외래 키 설정
-- rating 테이블의 user_key가 user 테이블의 user_key를 참조
ALTER TABLE rating
    ADD CONSTRAINT FK_rating_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

-- rating 테이블의 boardgame_key가 boardgame 테이블의 boardgame_key를 참조
ALTER TABLE rating
    ADD CONSTRAINT FK_rating_boardgame
        FOREIGN KEY (boardgame_key)
            REFERENCES boardgame (boardgame_key);

-- like 테이블의 user_key가 user 테이블의 user_key를 참조
ALTER TABLE "like"
    ADD CONSTRAINT FK_like_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

-- like 테이블의 boardgame_key가 boardgame 테이블의 boardgame_key를 참조
ALTER TABLE "like"
    ADD CONSTRAINT FK_like_boardgame
        FOREIGN KEY (boardgame_key)
            REFERENCES boardgame (boardgame_key);

-- user 테이블의 authority_key가 authority 테이블의 authority_key를 참조
ALTER TABLE "user"
    ADD CONSTRAINT FK_user_authority
        FOREIGN KEY (authority_key)
            REFERENCES authority (authority_key);

-- user 테이블의 user_image_key가 user_image 테이블의 user_image_key를 참조
ALTER TABLE "user"
    ADD CONSTRAINT FK_user_user_image
        FOREIGN KEY (user_image_key)
            REFERENCES user_image (user_image_key);

-- notification 테이블의 notification_type_key가 notification_type 테이블을 참조
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_notification_type
        FOREIGN KEY (notification_type_key)
            REFERENCES notification_type (notification_type_key);

-- notification 테이블의 recipient_user_key가 user 테이블을 참조
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_recipient_user
        FOREIGN KEY (recipient_user_key)
            REFERENCES "user" (user_key);

-- notification 테이블의 actor_user_key가 user 테이블을 참조
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_actor_user
        FOREIGN KEY (actor_user_key)
            REFERENCES "user" (user_key);

-- notification 테이블의 rating_key가 rating 테이블을 참조 (nullable)
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_rating
        FOREIGN KEY (rating_key)
            REFERENCES rating (rating_key);

-- notification 테이블의 reply_key가 reply 테이블을 참조 (nullable)
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_reply
        FOREIGN KEY (reply_key)
            REFERENCES reply (reply_key);

-- notification 테이블의 boardgame_key가 boardgame 테이블을 참조 (nullable)
ALTER TABLE notification
    ADD CONSTRAINT FK_notification_boardgame
        FOREIGN KEY (boardgame_key)
            REFERENCES boardgame (boardgame_key);


-- 트리거 함수 생성
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 각 테이블에 트리거 설정
CREATE TRIGGER trigger_update_announcement
    BEFORE UPDATE ON announcement
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_user
    BEFORE UPDATE ON "user"
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_rating
    BEFORE UPDATE ON rating
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_tag
    BEFORE UPDATE ON tag
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_authority
    BEFORE UPDATE ON authority
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_user_image
    BEFORE UPDATE ON user_image
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_like
    BEFORE UPDATE ON "like"
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_boardgame
    BEFORE UPDATE ON boardgame
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_notification_type
    BEFORE UPDATE ON notification_type
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_update_notification
    BEFORE UPDATE ON notification
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- 성능 최적화를 위한 인덱스 추가
-- 읽지 않은 알림 개수 조회 최적화
CREATE INDEX idx_notification_recipient_read_created 
    ON notification(recipient_user_key, is_read, created_at);

-- 알림 목록 조회 최적화 (최신순)
CREATE INDEX idx_notification_recipient_created_desc 
    ON notification(recipient_user_key, created_at DESC);

insert into caspedia.public.authority (role) values('ROLE_ADMIN'), ('ROLE_USER');

insert into caspedia.public.user_image (user_image_key, path, name)
VALUES (1, 'image_url', 'default.png'), (2, 'image_url', 'default.png');

INSERT INTO tag (name) VALUES
                           ('2인 베스트✌️'),
                           ('3인 베스트🤟'),
                           ('4인 베스트🖖️'),
                           ('사람 많을수록 좋은👨‍👩‍👧‍👦'),
                           ('누구나 쉽게 할 수 있는👶'),
                           ('전략 게임 입문으로🧒🏻'),
                           ('숙련자들이 즐기는👨🏻‍🎓'),
                           ('게이머즈 게임👹🔥'),
                           ('친구들과 함께👭'),
                           ('가족들과 함께👨‍👩‍👦‍👩‍👧‍👦'),
                           ('연인과 함께👩‍❤️‍👨'),
                           ('룰이 간단한🔰'),
                           ('또 해보고 싶은💘'),
                           ('쉬는 동안 가볍게☕'),
                           ('스토리가 매력적인📽'),
                           ('구성물이 예쁜💎'),
                           ('승패가 중요하지 않은🙌'),
                           ('아이스브레이킹😄'),
                           ('추리력이 필요한🕵️'),
                           ('수싸움이 치열한🧠'),
                           ('심리전이 필요한👀'),
                           ('순발력이 필요한😎'),
                           ('상호작용이 많은⚔'),
                           ('상호작용이 적은😌');

-- 알림 타입 초기 데이터 삽입 (5가지 알림 타입)
INSERT INTO notification_type (code, description) VALUES
    ('REPLY_ON_RATING', '{actor}님이 회원님의 한줄평에 댓글을 남겼습니다.'),
    ('IMPRESSED_ON_RATING', '{actor}님이 회원님의 한줄평에 좋아요를 눌렀습니다.'),
    ('IMPRESSED_ON_REPLY', '{actor}님이 회원님의 댓글에 좋아요를 눌렀습니다.'),
    ('RATING_ON_RATED_BOARDGAME', '{actor}님이 {boardgame}에 한줄평을 남겼습니다.'),
    ('RATING_ON_LIKED_BOARDGAME', '{actor}님이 회원님이 좋아요한 {boardgame}에 한줄평을 남겼습니다.');

-- 로그인 기록 테이블 (user_login_history)
CREATE TABLE user_login_history (
    login_key SERIAL PRIMARY KEY,
    user_key INTEGER NOT NULL,
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 업적 정의 테이블 (achievement) - 이벤트/히든 전용
CREATE TABLE achievement (
    achievement_key SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL
);

-- 유저별 이벤트 업적 달성 테이블 (user_achievement)
CREATE TABLE user_achievement (
    user_key INTEGER NOT NULL,
    achievement_key INTEGER NOT NULL,
    achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_key, achievement_key)
);

-- user_login_history 외래 키 설정
ALTER TABLE user_login_history
    ADD CONSTRAINT FK_user_login_history_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

-- user_achievement 외래 키 설정
ALTER TABLE user_achievement
    ADD CONSTRAINT FK_user_achievement_user
        FOREIGN KEY (user_key)
            REFERENCES "user" (user_key);

ALTER TABLE user_achievement
    ADD CONSTRAINT FK_user_achievement_achievement
        FOREIGN KEY (achievement_key)
            REFERENCES achievement (achievement_key);

-- 업적 초기 데이터 삽입
INSERT INTO achievement (name, type) VALUES ('byunmin_crew', 'event');
