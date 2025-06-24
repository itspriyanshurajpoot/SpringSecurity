package com.security.services.user;

import com.security.dtos.user.LoginRequest;
import com.security.dtos.user.RegistrationRequest;
import com.security.entities.User;
import com.security.response.ApiResponse;

public interface IUserService {

    // register user
    ApiResponse registerUser(RegistrationRequest request);
    // login user
    ApiResponse loginUser(LoginRequest request);

    // logout user
    ApiResponse logoutUser(User user);


    // get user by id
    ApiResponse getUserById(Long userId);
    // delete user by user id
    ApiResponse deleteUserById(Long userId);
    // update user
    ApiResponse updateUser(RegistrationRequest request, Long userId);
    // get all users
    ApiResponse getAllUsers();
}
