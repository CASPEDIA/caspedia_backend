package com.cast.caspedia.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDetailResponseDto {
    private GameInfoDto gameInfo;
    private RatingInfoDto ratingInfo;
    private ReplyInfoDto[] replyInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameInfoDto {
        private int boardgameKey;
        private String imageUrl;
        private String nameKor;
        private String nameEng;
        private int yearPublished;
        private float castScore;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RatingInfoDto {
        private String nanoid;
        private String nickname;
        private int userImageKey;
        private String comment;
        private int score;
        private String createdAt;
        private String updatedAt;
        private String tagKeys;
        private int impressedCount;
        private int replyCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyInfoDto {
        private int replyKey;
        private String nanoid;
        private String nickname;
        private int userImageKey;
        private int impressedCount;
        private String content;
    }
}

/*
{
	"game_info" : {
		"boardgame_key": 1,
    "image_url": "https://cf.geekdo-images.com/rpwCZAjYLD940NWwP3SRoA__original/img/yR0aoBVKNrAmmCuBeSzQnMflLYg=/0x0/filters:format(jpeg)/pic4718279.jpg",
    "name_kor": "디 마허",
    "name_eng": "Die Macher",
    "year_published": 1986,
    "cast_score": 1.0,
	},
	"rating_info" : {
    "nanoid": "kjizJgsAsK_OILUK2SQHv",
    "nickname": "ssafy2",
    "user_image_key": 1,
    "comment": "좋았어요",
    "score": 5,
    "created_at": "2024-11-18T21:47:24.158691",
    "updated_at": "2024-11-18T21:47:24.158691",
    "tag_keys": "111110000000000000000000",
    "impressed_count" : 50,
    "reply_count" : 4
	},
	"reply_info" : [
		{
			"nanoid": "kjizJgsAsK_OILUK2SQHv",
	    "nickname": "ssafy2",
	    "user_image_key": 1,
	    "impressed_count": 30,
	    "content" : "하이"
		},
		{
			"nanoid": "kjizJgsAsK_OILUK2SQHv",
	    "nickname": "ssafy2",
	    "user_image_key": 1,
	    "impressed_count": 30,
	    "content" : "하이"
		},
		{
			"nanoid": "kjizJgsAsK_OILUK2SQHv",
	    "nickname": "ssafy2",
	    "user_image_key": 1,
	    "impressed_count": 30,
	    "content" : "하이"
		}
	]
}

 */