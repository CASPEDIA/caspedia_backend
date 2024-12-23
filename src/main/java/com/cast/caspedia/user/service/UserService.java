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
import java.util.Map;
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

    // 닉네임 중복 체크
    public boolean isUniqueNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    // 닉네임 길이 및 문자열 체크
    public boolean isValidNickname(String nickname) {
        int maxByteLength = 20; // 최대 허용 길이
        int byteLength = 0;

        for (char ch : nickname.toCharArray()) {
            if (Character.toString(ch).matches("[가-힣]")) {
                byteLength += 2; // 한글은 2바이트
            } else if (Character.toString(ch).matches("[a-zA-Z0-9_.]")) {
                byteLength += 1; // 영어, 숫자, '_', '.'은 1바이트
            } else {
                // 허용되지 않는 문자 발견 시 false 반환
                return false;
            }

            // 초과 여부를 미리 확인
            if (byteLength > maxByteLength) {
                return false;
            }
        }
        return true;
    }


    // 닉네임 변경
    @Transactional
    public boolean changeNickname(String newNickname, String userId) throws AppException {
        int rowsUpdated = userRepository.updateNicknamebyId(newNickname, userId);
        return rowsUpdated > 0;
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


    public boolean checkPassword(String oldPassword, String userId){
        User user = userRepository.findUserByUserId(userId);
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public boolean changePassword(String newPassword, String userId){
        String encodedPw = passwordEncoder.encode(newPassword);
        int rowsUpdated = userRepository.updatePasswordById(encodedPw, userId);
        return rowsUpdated > 0;
    }

    public void updateUser(Map<String, String> params) {
        try {
            String nanoid = params.get("nanoid");
            String nickname = params.get("nickname");
            String introduction = params.get("introduction");
            boolean enabled = Boolean.parseBoolean(params.get("enabled"));
            int authorityKey = Integer.parseInt(params.get("authority_key"));

            User user = userRepository.findByNanoid(nanoid);

            user.setNickname(nickname);
            user.setIntroduction(introduction);
            user.setEnabled(enabled);
            user.setAuthority(authorityRepository.findById(authorityKey).get());

            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("유저 정보 수정에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteUser(String nanoid) {
        try {
            User user = userRepository.findByNanoid(nanoid);
            user.setEnabled(false);
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("유저 정보 삭제에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public void resetPassword(String nanoid) {
        try {
            User user = userRepository.findByNanoid(nanoid);
            String encodedPw = passwordEncoder.encode(user.getStudentId() + "");
            user.setPassword(encodedPw);
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("비밀번호 초기화에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
