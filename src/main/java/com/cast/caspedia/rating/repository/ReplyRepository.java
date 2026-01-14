package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Rating;
import com.cast.caspedia.rating.domain.Reply;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    int countByRating(Rating rating);

    List<Reply> findAllByRating(Rating rating);

    int countByUser(User user);
}
