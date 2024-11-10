package com.cast.caspedia.user.repository;

import com.cast.caspedia.user.domain.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Integer> {
    UserImage findByUserImageKey(Integer userImageKey);
}
