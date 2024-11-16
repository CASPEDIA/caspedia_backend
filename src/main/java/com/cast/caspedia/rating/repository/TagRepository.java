package com.cast.caspedia.rating.repository;

import com.cast.caspedia.rating.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}
