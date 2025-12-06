package com.aftersports.aftersports.domain.service;

import com.aftersports.aftersports.domain.model.User;
import com.aftersports.aftersports.domain.model.UserRole;
import com.aftersports.aftersports.domain.repo.UserRepository;
import com.aftersports.aftersports.infra.security.JwtService;
import com.aftersports.aftersports.infra.security.JwtService.JwtPayload;
import com.aftersports.aftersports.web.dto.AuthResponse;
import com.aftersports.aftersports.web.dto.LoginRequest;
import com.aftersports.aftersports.web.dto.RegisterRequest;
import com.aftersports.aftersports.web.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordService.hash(request.password()));
        user.setRole(UserRole.USER);
        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordService.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserDTO currentUser(String authorizationHeader) {
        User user = resolveUserFromHeader(authorizationHeader);
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public User resolveUserFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing Authorization header");
        }
        String token = authorizationHeader.substring(7);
        JwtPayload payload = jwtService.parseAndValidate(token);
        return userRepository.findById(payload.sub())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, toDTO(user));
    }
}
