package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Reply;
import com.cast.caspedia.rating.domain.ReplyImpressed;
import com.cast.caspedia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyImpressedRepository extends JpaRepository<ReplyImpressed, Integer> {

    boolean existsByUserAndReply(User user, Reply reply);

    Optional<ReplyImpressed> findByUserAndReply(User user, Reply reply);

    int countByReply(Reply reply);

    void deleteAllByReply(Reply reply);
}
