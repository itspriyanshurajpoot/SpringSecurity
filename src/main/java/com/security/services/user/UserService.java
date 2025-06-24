package com.security.services.user;

import com.security.dtos.user.LoginRequest;
import com.security.dtos.user.RegistrationRequest;
import com.security.entities.JwtToken;
import com.security.entities.User;
import com.security.jwt.JwtUtils;
import com.security.repositories.JwtTokenRepository;
import com.security.repositories.UserRepository;
import com.security.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements IUserService{

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public ApiResponse registerUser(RegistrationRequest request) {
        if (request == null) {
            throw new RuntimeException("Registration request cannot be null");
        }

        // Check if user already exists
        User existingUser = userRepository.findUsersByEmailIgnoreCase(request.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }


        // Create new user
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setAge(request.getAge());
        newUser.setRoles(request.getRole().stream().toList());

        // Save new user
        User savedUser = userRepository.save(newUser);

        // generate tokens
        JwtToken jwtToken = new JwtToken();
        jwtToken.setUser(newUser);
        jwtToken.setToken(jwtUtils.generateToken(savedUser)); // Replace with actual token generation logic
        jwtToken.setLogout(false);
        jwtToken.setCreatedAt(LocalDateTime.now());

        // Save JWT token
        jwtTokenRepository.save(jwtToken);

        return ApiResponse.builder()
                .message("User registered successfully")
                .data(savedUser)
                .success(true)
                .build();
    }

    @Override
    public ApiResponse loginUser(LoginRequest request) {
        if (request == null) {
            throw new RuntimeException("Login request cannot be null");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            User user = userRepository.findUsersByEmailIgnoreCase(request.getEmail());
            if (user == null) {
                throw new RuntimeException("User not found with email: " + request.getEmail());
            }

            // logout pre-existed token
            List<JwtToken> existingTokens = jwtTokenRepository.findByUserAndLogoutFalse(user);
            if (!existingTokens.isEmpty()) {
                existingTokens.forEach(token -> {
                    token.setLogout(true);
                    jwtTokenRepository.save(token);
                });
            }

            // Generate JWT token
            String jwtToken = jwtUtils.generateToken(user); // Replace with actual token generation logic

            // Save JWT token
            JwtToken token = new JwtToken();
            token.setUser(user);
            token.setToken(jwtToken);
            token.setLogout(false);
            token.setCreatedAt(LocalDateTime.now());
            jwtTokenRepository.save(token);

            return ApiResponse.builder()
                    .message("Login successful")
                    .data(user)
                    .jwtToken(jwtToken)
                    .success(true)
                    .build();
        }
        return null;
    }

    @Override
    public ApiResponse logoutUser(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        // Find the JWT token for the user
        List<JwtToken> jwtToken = jwtTokenRepository.findByUserAndLogoutFalse(user);
        if(!jwtToken.isEmpty()){

            jwtToken.forEach(token -> {
                token.setLogout(true);
                jwtTokenRepository.save(token);
            });
        }
        return ApiResponse.builder()
                .message("User logged out successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse getUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }

        return userRepository.findById(userId)
                .map(user -> new ApiResponse("User found", true, user))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public ApiResponse deleteUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }

        userRepository.findById(userId)
                .ifPresentOrElse(userRepository::delete, () -> {
                    throw new RuntimeException("User not found with ID: " + userId);
                });

        return new ApiResponse("User deleted successfully", true, null);
    }

    @Override
    public ApiResponse updateUser(RegistrationRequest request, Long userId) {
        if (userId == null || request == null) {
            throw new RuntimeException("User ID and request cannot be null");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    // Update user fields based on request
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFullName(request.getFullName());
                    user.setAge(request.getAge());


                    // Save updated user
                    userRepository.save(user);


                    return new ApiResponse("User updated successfully", true, user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public ApiResponse getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return ApiResponse.builder()
                .message("Success")
                .data(allUsers)
                .success(true)
                .build();
    }
}
