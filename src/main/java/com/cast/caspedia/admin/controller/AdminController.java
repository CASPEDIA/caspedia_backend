package com.cast.caspedia.admin.controller;

import com.cast.caspedia.admin.dto.JoinRequestDto;
import com.cast.caspedia.admin.service.AdminService;
import com.cast.caspedia.boardgame.service.BggFetcherService;
import com.cast.caspedia.boardgame.service.BggIntegrationService;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private BggFetcherService bggFetcherService;

    @Autowired
    private BggIntegrationService bggIntegrationService;

    // 회원 등록
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody JoinRequestDto joinRequestDto) throws Exception {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        if(adminService.join(joinRequestDto) != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 모든 유저 정보 조회
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        if(checkAdmin()) {
            return ResponseEntity.ok(adminService.getAllUsers());
        } else {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    //회원 정보 수정
    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> params) {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        userService.updateUser(params);
        return ResponseEntity.ok().build();
    }

    //회원 정보 삭제
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestParam(required = false) String nanoid) {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        if(nanoid == null) {
            throw new AppException("nanoid값이 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        userService.deleteUser(nanoid);
        return ResponseEntity.ok().build();
    }

    //회원 비밀번호 초기화
    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam(required = false) String nanoid) {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        if(nanoid == null) {
            throw new AppException("nanoid값이 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        userService.resetPassword(nanoid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchBoardgames() {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        bggFetcherService.fetchAllGames();
        return ResponseEntity.ok("보드게임 데이터를 성공적으로 가져왔습니다.");
    }

    @GetMapping("/integrate")
    public ResponseEntity<?> integrateData() {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        bggIntegrationService.integrateData();
        return ResponseEntity.ok("보드게임 데이터를 성공적으로 통합했습니다.");
    }

    @GetMapping("/fetch-and-integrate")
    public ResponseEntity<?> fetchAndIntegrate() {
        if(!checkAdmin()) {
            throw new AppException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        bggFetcherService.fetchAllGames();
        bggIntegrationService.integrateData();
        return ResponseEntity.ok("보드게임 데이터를 성공적으로 가져오고 통합했습니다.");
    }

    // admin 권한 확인
    public boolean checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }
}
