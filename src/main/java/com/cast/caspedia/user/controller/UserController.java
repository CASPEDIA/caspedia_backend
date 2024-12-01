package com.cast.caspedia.user.controller;

import com.cast.caspedia.error.AppException;
import com.cast.caspedia.rating.dto.RatingDto;
import com.cast.caspedia.security.custom.CustomUserDetails;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.dto.*;
import com.cast.caspedia.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    //인증된 사용자 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        if(user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("logout");
    }


    //유저 검색 자동완성
    @GetMapping("/autofill")
    public ResponseEntity<?> autofill(@RequestParam(name="q", required=false) String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new AppException("query가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
        List<UserSearchDto> user = userService.autofill(query);

        return ResponseEntity.ok(user);
    }

    //유저 기본 정보 조회
    @GetMapping("/{nanoid}")
    public ResponseEntity<?> getUserInfo(@PathVariable String nanoid) {
        UserInfoDto user = userService.getUserInfo(nanoid);

        if(user == null) {
            throw new AppException("해당 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }else {
            return ResponseEntity.ok(user);
        }
    }

//    //닉네임 변경
    @PutMapping("/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody Map<String, String> params) {

        String newNickname = params.get("new_nickname");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if(userService.changeNickname(newNickname, userId)) {
            return ResponseEntity.ok().build();
        } else {
            throw new AppException("닉네임 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }

    }

    //자기소개 변경
    @PutMapping("/introduction")
    public ResponseEntity<?> changeIntroduction(@RequestBody Map<String, String> param) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        String newIntroduction = (String) param.get("new_introduction");
        if (newIntroduction == null || newIntroduction.length() > 300) {
            throw new AppException("자기소개 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }

        if(userService.changeIntroduction(param.get("new_introduction"), userId)) {
            return ResponseEntity.ok().build();
        } else {
            throw new AppException("자기소개 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //좋아요 게임 목록 조회
    @GetMapping("/likes")
    public ResponseEntity<?> getLikeGameList(@RequestParam(name="nanoid", required = false) String nanoid) {
        if (nanoid == null || nanoid.trim().isEmpty()) {
            throw new AppException("nanoid가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
        List<LikeDto> likeDtoList = userService.getLikeList(nanoid);
        return ResponseEntity.ok(likeDtoList);
    }

    //평가 내역 목록 조회
    @GetMapping("/ratings")
    public ResponseEntity<?> getRatingList(@RequestParam(name="nanoid") String nanoid) {
        if (nanoid == null || nanoid.trim().isEmpty()) {
            throw new AppException("nanoid가 비어 있거나 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }
        List<RatingDto> ratingDtoList = userService.getRatingList(nanoid);
        return ResponseEntity.ok(ratingDtoList);
    }

    //유저 프로필 사진 변경\
    @PutMapping("/image")
    public ResponseEntity<?> changeProfile(@RequestBody Map<String, String> param) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        if(id == null) {
            throw new AppException("인증된 사용자 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        int imageKey = Integer.parseInt(param.get("new_image_key"));

        if(userService.changeProfile(imageKey, id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
