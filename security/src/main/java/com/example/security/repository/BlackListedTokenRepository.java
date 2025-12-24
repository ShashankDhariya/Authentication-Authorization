package com.example.security.repository;

import com.example.security.entity.BlackListedTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlackListedTokens, Long> {
    boolean existsByToken(String token);
}
