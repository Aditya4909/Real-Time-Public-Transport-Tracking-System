package com.transittrack.controller;

import com.transittrack.dto.request.LoginRequest;
import com.transittrack.dto.request.RegisterRequest;
import com.transittrack.dto.response.ApiResponse;
import com.transittrack.dto.response.JwtAuthResponse;
import com.transittrack.dto.response.UserResponse;
import com.transittrack.security.UserPrincipal;
import com.transittrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user authentication, account registration, and session identity.
 *
 * Annotations explanation:
 * - @RestController: Combines @Controller and @ResponseBody, ensuring method return values are serialized directly into HTTP response bodies.
 * - @RequestMapping: Sets the base URL path for all endpoints in this controller.
 * - @Tag: OpenAPI annotation grouping endpoints under the 'Authentication' section in Swagger UI.
 * - @Valid: Triggers Jakarta Bean Validation on the request payload prior to method execution.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and identity retrieval")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and returns a signed JWT Bearer token with roles")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user or driver", description = "Creates a new commuter or driver user account in the system")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.registerUser(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", userResponse), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated profile", description = "Returns profile details of the user associated with the provided JWT Bearer token")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse userResponse = authService.getCurrentUser(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userResponse));
    }
}
