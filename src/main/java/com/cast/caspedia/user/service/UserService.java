package com.cast.caspedia.user.service;

import com.cast.caspedia.boardgame.repository.LikeRepository;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.dto.RatingDto;
import com.cast.caspedia.rating.repository.RatingRepository;
import com.cast.caspedia.rating.repository.RatingTagRepository;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.domain.UserImage;
import com.cast.caspedia.user.dto.LikeDto;
import com.cast.caspedia.user.dto.UserInfoDto;
import com.cast.caspedia.user.dto.UserSearchDto;
import com.cast.caspedia.user.repository.AuthorityRepository;
import com.cast.caspedia.user.repository.UserImageRepository;
import com.cast.caspedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingTagRepository ratingTagRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserImageRepository UserImageRepository;

    @Autowired
    private LikeRepository likeRepository;



    // 유저 검색 자동완성 기능
    public List<UserSearchDto> autofill(String query) {
        return userRepository.findByNicknameLike(query);
    }

    // 유저 정보 조회
    public UserInfoDto getUserInfo(String nanoid) {
        User user = userRepository.findByNanoid(nanoid);
        if (user != null) {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setId(user.getId());
            userInfoDto.setNanoid(user.getNanoid());
            userInfoDto.setNickname(user.getNickname());
            userInfoDto.setName(user.getName());
            userInfoDto.setIntroduction(user.getIntroduction());
            userInfoDto.setUserImageKey(user.getUserImage().getUserImageKey());
            return userInfoDto;
        }
        return null;
    }

    // 닉네임 변경
    @Transactional
    public boolean changeNickname(String newNickname, String userId) throws AppException {
        try {
            int rowsUpdated = userRepository.updateNicknamebyId(newNickname, userId);
            return rowsUpdated > 0;
        } catch (Exception e) {
            throw new AppException("닉네임 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }


    // 자기소개 변경
    @Transactional
    public boolean changeIntroduction(String newIntroduction, String id) {
        int rowsUpdated = userRepository.updateIntroductionById(newIntroduction, id);
        return rowsUpdated > 0;
    }

    // 좋아요 게임 목록 조회
    public List<LikeDto> getLikeList(String nanoid) {

        User user = userRepository.findByNanoid(nanoid);
        List<LikeDto> likeDtoList = new ArrayList<>();

        if (user != null) {
            likeRepository.findAllByUser(user).forEach(like -> {
                LikeDto likeDto = new LikeDto();
                likeDto.setBoardgameKey(like.getBoardgame().getBoardgameKey());
                likeDto.setNameKor(like.getBoardgame().getNameKor());
                likeDto.setNameEng(like.getBoardgame().getNameEng());
                likeDto.setImageUrl(like.getBoardgame().getImageUrl());
                likeDto.setCreatedAt(like.getCreatedAt());
                likeDtoList.add(likeDto);
            });
        }
        return likeDtoList;
    }

    // 특정 유저의 평가 내역 목록 조회
    public List<RatingDto> getRatingList(String nanoid) throws AppException {
        User user = userRepository.findByNanoid(nanoid);

        if (user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        List<Rating> ratings = new ArrayList<>();
        ratings = ratingRepository.findRatingByUser(user);

        List<RatingDto> ratingDtos = new ArrayList<>();

        if(ratings != null) {
            for (Rating rating : ratings) {
                RatingDto ratingDto = new RatingDto();
                ratingDto.setRatingKey(rating.getRatingKey());
                ratingDto.setScore(rating.getScore());
                ratingDto.setComment(rating.getComment());
                ratingDto.setBoardgameKey(rating.getBoardgame().getBoardgameKey());
                ratingDto.setNanoid(rating.getUser().getNanoid());
                ratingDto.setNameKor(rating.getBoardgame().getNameKor());
                ratingDto.setNameEng(rating.getBoardgame().getNameEng());
                ratingDto.setCreatedAt(rating.getCreatedAt());
                ratingDto.setUpdatedAt(rating.getUpdatedAt());
                ratingDto.setTagKey(rating.getTagKey());
                ratingDto.setImageUrl(rating.getBoardgame().getImageUrl());
                ratingDtos.add(ratingDto);
            }
        }



        return ratingDtos;
    }

    // 유저 프로필 사진 변경
    @Transactional
    public boolean changeProfile(int imageKey, String id) {
        // userImageKey를 통해 UserImage 객체 조회
        Optional<UserImage> userImageOptional = UserImageRepository.findById(imageKey);

        // UserImage 객체가 존재하지 않는 경우 false 반환
        if(userImageOptional.isEmpty()) {
            return false;
        }

        if (userImageOptional.isPresent()) {
            // 조회된 UserImage 객체로 프로필 이미지 업데이트
            int rowsUpdated = userRepository.updateUserImageById(userImageOptional.get(), id);
            return rowsUpdated > 0;
        } else {
            // UserImage가 존재하지 않는 경우 false 반환
            return false;
        }
    }



}
