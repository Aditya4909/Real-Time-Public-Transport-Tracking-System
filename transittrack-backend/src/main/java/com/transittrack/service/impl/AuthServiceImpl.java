package com.transittrack.service.impl;

import com.transittrack.dto.request.LoginRequest;
import com.transittrack.dto.request.RegisterRequest;
import com.transittrack.dto.response.JwtAuthResponse;
import com.transittrack.dto.response.UserResponse;
import com.transittrack.entity.ERole;
import com.transittrack.entity.Role;
import com.transittrack.entity.User;
import com.transittrack.exception.BadRequestException;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.RoleRepository;
import com.transittrack.repository.UserRepository;
import com.transittrack.security.JwtTokenProvider;
import com.transittrack.security.UserPrincipal;
import com.transittrack.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of AuthService for handling registration, login, and authentication tokens.
 *
 * Annotations explanation:
 * - @Service: Specialization of @Component marking this bean as a business logic service in the Spring IoC container.
 * - @Transactional: Configures database transactions with automatic rollback on runtime exceptions.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final long jwtExpirationMs;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           @Value("${app.jwt.expiration-ms}") long jwtExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Override
    public JwtAuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtAuthResponse(
                jwt,
                jwtExpirationMs,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles
        );
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getPhoneNumber()
        );

        Set<Role> roles = new HashSet<>();
        String strRole = registerRequest.getRole();

        if (strRole == null || strRole.isBlank() || strRole.equalsIgnoreCase("USER")) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", ERole.ROLE_USER));
            roles.add(userRole);
        } else if (strRole.equalsIgnoreCase("DRIVER")) {
            Role driverRole = roleRepository.findByName(ERole.ROLE_DRIVER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", ERole.ROLE_DRIVER));
            roles.add(driverRole);

            if (registerRequest.getLicenseNumber() != null && !registerRequest.getLicenseNumber().isBlank()) {
                user.setLicenseNumber(registerRequest.getLicenseNumber());
            } else {
                throw new BadRequestException("License number is required when registering as a DRIVER");
            }
        } else if (strRole.equalsIgnoreCase("ADMIN")) {
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", ERole.ROLE_ADMIN));
            roles.add(adminRole);
        } else {
            throw new BadRequestException("Invalid role specified: " + strRole);
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        return mapUserToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        return mapUserToResponse(user);
    }

    public static UserResponse mapUserToResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getLicenseNumber(),
                user.isEnabled(),
                roleNames,
                user.getCreatedAt()
        );
    }
}
