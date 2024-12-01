package com.cast.caspedia.admin.controller;

import com.cast.caspedia.admin.dto.JoinRequestDto;
import com.cast.caspedia.admin.service.AdminService;
import com.cast.caspedia.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody JoinRequestDto joinRequestDto) throws Exception {
        
        log.info("들어옴");
        log.info("joinRequestDto: {}", joinRequestDto);

        if(adminService.join(joinRequestDto) != null) {
            log.info("join");
//            User user = userService;

            return ResponseEntity.ok("join");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
