package com.security.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.security.entities.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Setter
@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String message;
    private boolean success;
    private Object data;
    private String jwtToken;


    public ApiResponse(String userFound, boolean b, User user) {
        this.message = userFound;
        this.success = b;
        this.data = user;
    }
}
