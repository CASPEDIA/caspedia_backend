package com.cast.caspedia.admin.service;

import com.cast.caspedia.admin.dto.JoinRequestDto;
import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.AuthorityRepository;
import com.cast.caspedia.user.repository.UserImageRepository;
import com.cast.caspedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



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
        // 비밀번호 암호화
        String userPw = joinRequestDto.getPassword();
        String encodedPw = passwordEncoder.encode(userPw);

        User user = new User();
        user.setId(joinRequestDto.getId());
        user.setName(joinRequestDto.getName());
        user.setPassword(encodedPw);
        user.setStudentId(joinRequestDto.getStudentId());
        user.setNickname(joinRequestDto.getNickname());
        user.setAuthority(authorityRepository.findByAuthorityKey(joinRequestDto.getAuthorityKey()));
        user.setUserImage(userImageRepository.findByUserImageKey(joinRequestDto.getUserImageKey()));
        user.setIntroduction("");
        user.setNanoid(nanoidGeneratorService.generateNanoid());

        // 회원 등록
        User result = userRepository.save(user);

        if (result == null) {
            return null;
        }
        return result.getNanoid();
    }
}
