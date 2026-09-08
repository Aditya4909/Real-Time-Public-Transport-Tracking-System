package com.transittrack.service;

import com.transittrack.dto.request.LoginRequest;
import com.transittrack.dto.request.RegisterRequest;
import com.transittrack.dto.response.JwtAuthResponse;
import com.transittrack.dto.response.UserResponse;
import com.transittrack.security.UserPrincipal;

/**
 * Service interface defining authentication and user registration contracts.
 */
public interface AuthService {

    JwtAuthResponse authenticateUser(LoginRequest loginRequest);

    UserResponse registerUser(RegisterRequest registerRequest);

    UserResponse getCurrentUser(UserPrincipal userPrincipal);
}
