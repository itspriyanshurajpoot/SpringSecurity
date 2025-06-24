package com.security.repositories;

import com.security.entities.JwtToken;
import com.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    List<JwtToken> findByUserAndLogoutFalse(User user);
}
