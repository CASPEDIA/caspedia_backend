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

ALTER TABLE boardgame
    ADD COLUMN cast_score FLOAT NOT NULL DEFAULT 0;

--  introduction varchar NOT NULL DEFAULT '',

ALTER TABLE caspedia.public."user"
    ALTER COLUMN introduction TYPE text;

ALTER TABLE rating
    ADD COLUMN tag_key varchar(50) NOT NULL DEFAULT '000000000000000000000000';

ALTER TABLE caspedia.public."user"
    ALTER COLUMN nickname TYPE varchar(255);
ALTER TABLE caspedia.public."user"
    ALTER COLUMN nickname SET NOT NULL;
ALTER TABLE caspedia.public."user"
    ADD CONSTRAINT user_nickname_unique UNIQUE (nickname);


ALTER TABLE caspedia.public."boardgame"
    ADD COLUMN designer TEXT NOT NULL DEFAULT '';

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

ALTER TABLE caspedia.public."rating"
    ALTER COLUMN comment TYPE TEXT;