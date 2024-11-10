package com.cast.caspedia.user.repository;

import com.cast.caspedia.user.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Authority findByRole(String role);

    Authority findByAuthorityKey(Integer authorityKey);
}

