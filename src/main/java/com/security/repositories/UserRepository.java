package com.security.repositories;

import com.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUsersByEmailIgnoreCase(String email);

}
