package com.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "jwt_token")
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer token_id;
    private LocalDateTime createdAt;
    private String token;
    private boolean logout;


    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

}
