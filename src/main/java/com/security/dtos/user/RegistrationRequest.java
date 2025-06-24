package com.security.dtos.user;

import com.security.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegistrationRequest {

    private String fullName;
    private String email;
    private String password;
    private int age;
    private List<Role> role; // e.g., "USER", "ADMIN"

}
