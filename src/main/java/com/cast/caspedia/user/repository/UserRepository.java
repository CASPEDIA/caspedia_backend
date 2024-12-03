package com.cast.caspedia.user.repository;

import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.domain.UserImage;
import com.cast.caspedia.user.dto.UserSearchDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findById(String id);
    public List<User> findByName(String name);

    //로그인
    public User findUserByIdAndPassword(String id, String password);

    public User findUserById(String id);


    //유저검색 자동완성
    @Query("SELECT new com.cast.caspedia.user.dto.UserSearchDto(u.nickname, u.name, u.id, u.nanoid) " +
            "FROM User u WHERE u.nickname LIKE %:keyword%")
    List<UserSearchDto> findByNicknameLike(String keyword);

    //유저정보조회
    public User findByNickname(String nickname);

    public User findByNanoid(String nanoid);

    //비밀번호 변경
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.nanoid = :nanoid")
    int updatePasswordByNanoid(String password, String nanoid);

    //닉네임 변경
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.nickname = :newNickname WHERE u.id = :userId")
    int updateNicknamebyId(String newNickname, String userId);

    //자기소개 변경
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.introduction = :introduction WHERE u.id = :id")
    int updateIntroductionById(String introduction, String id);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userImage = :userImage WHERE u.id = :id")
    int updateUserImageById(UserImage userImage, String id);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findUserByUserId(String userId);

    @Query("SELECT count(u)>0 FROM User u WHERE u.nickname = :newNickname")
    boolean existsByNickname(String newNickname);
}
