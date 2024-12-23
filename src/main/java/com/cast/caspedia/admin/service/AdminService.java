package com.cast.caspedia.admin.service;

import com.cast.caspedia.admin.dto.JoinRequestDto;
import com.cast.caspedia.admin.dto.UsersResponseDto;
import com.cast.caspedia.error.AppException;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.AuthorityRepository;
import com.cast.caspedia.user.repository.UserImageRepository;
import com.cast.caspedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private  UserImageRepository userImageRepository;

    @Autowired NanoidGeneratorService nanoidGeneratorService;

    public String join(JoinRequestDto joinRequestDto) throws Exception {
        try {
            // 비밀번호 암호화
            String userPw = joinRequestDto.getStudentId() +"";
            String encodedPw = passwordEncoder.encode(userPw);

            User user = new User();
            user.setId(joinRequestDto.getId());
            user.setName(joinRequestDto.getName());
            user.setPassword(encodedPw);
            user.setStudentId(joinRequestDto.getStudentId());
            user.setAuthority(authorityRepository.findByAuthorityKey(joinRequestDto.getAuthorityKey()));
            user.setUserImage(userImageRepository.findByUserImageKey(joinRequestDto.getUserImageKey()));
            user.setIntroduction("안녕하세요! caspedia에 오신 것을 환영합니다.");

            // 랜덤 닉네임 생성, 중복 체크
            String nickname = "user_" + (int)(Math.random() * 100000);
            while(userRepository.existsByNickname(nickname)) {
                nickname = "user_" + (int)(Math.random() * 100000);
            }
            user.setNickname(nickname);

            // nanoid 생성, 중복 체크
            String nanoid = nanoidGeneratorService.generateNanoid();
            while(userRepository.existsByNanoid(nanoid)) {
                nanoid = nanoidGeneratorService.generateNanoid();
            }
            user.setNanoid(nanoidGeneratorService.generateNanoid());

            // 회원 등록
            User result = userRepository.save(user);

            return result.getNanoid();
        } catch (Exception e) {
            // jpa 중복 에러 메세지 파싱
            String targetStart = "Detail: ";
            String targetEnd = "]";
            int targetStartIndex = e.getMessage().indexOf(targetStart) + targetStart.length();
            int targetEndIndex = e.getMessage().indexOf(targetEnd, targetStartIndex);
            String target = e.getMessage().substring(targetStartIndex, targetEndIndex);
            throw new AppException(target, HttpStatus.BAD_REQUEST);
        }
    }

    public List<UsersResponseDto> getAllUsers() {

        List<UsersResponseDto> responseDtos = new ArrayList<>();

        for(User user : userRepository.findAll()) {
            UsersResponseDto responseDto = new UsersResponseDto();
            responseDto.setId(user.getId());
            responseDto.setNanoid(user.getNanoid());
            responseDto.setNickname(user.getNickname());
            responseDto.setName(user.getName());
            responseDto.setIntroduction(user.getIntroduction());
            responseDto.setStudentId(user.getStudentId());
            responseDto.setAuthorityKey(user.getAuthority().getAuthorityKey());
            responseDto.setUserImageKey(user.getUserImage().getUserImageKey());
            responseDto.setEnabled(user.isEnabled());
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }
}
