-- 모든 테이블 삭제 (외래 키 관계를 고려하여 CASCADE 사용)
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
                        nickname varchar NOT NULL,
                        name varchar NOT NULL,
                        nanoid varchar NOT NULL,
                        introduction varchar NOT NULL DEFAULT '',
                        student_id int NOT NULL,
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
                        comment varchar NOT NULL DEFAULT '',
                        boardgame_key int NOT NULL,  -- 외래 키 추가
                        user_key int NOT NULL,  -- 외래 키 추가
                        created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                        updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 태그 기록 테이블 (rating_tag)
CREATE TABLE rating_tag (
                            rating_tag_key serial PRIMARY KEY,
                            rating_key int NOT NULL,  -- 외래 키 추가
                            tag_key int NOT NULL,  -- 외래 키 추가
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
                           created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                           updated_at timestamp DEFAULT CURRENT_TIMESTAMP
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

-- rating_tag 테이블의 tag_key가 tag 테이블의 tag_key를 참조
ALTER TABLE rating_tag
    ADD CONSTRAINT FK_rating_tag_tag
        FOREIGN KEY (tag_key)
            REFERENCES tag (tag_key);

-- rating_tag 테이블의 rating_key가 rating 테이블의 rating_key를 참조
ALTER TABLE rating_tag
    ADD CONSTRAINT FK_rating_tag_rating
        FOREIGN KEY (rating_key)
            REFERENCES rating (rating_key);

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

CREATE TRIGGER trigger_update_rating_tag
    BEFORE UPDATE ON rating_tag
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

