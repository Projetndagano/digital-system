package com.group.payment.auth;

import com.group.payment.config.ApiResponse;
import com.group.payment.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ApiResponse.ok("User registered successfully", Map.of(
                "token", token,
                "user", safeUser(user)
        ));
    }

    public ApiResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ApiResponse.ok("Login successful", Map.of(
                "token", token,
                "user", safeUser(user)
        ));
    }

   public Map<String, Object> getProfile(User user) {
    // Wrap the safeUser map inside another map with the key "user"
    return Map.of("user", safeUser(user));
}

    public ApiResponse updateProfile(User user, UpdateProfileRequest request) {
        if (request.getUsername() != null) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return ApiResponse.ok("Profile updated", Map.of("user", safeUser(user)));
    }

    public ApiResponse deactivateAccount(User user) {
        user.setActive(false);
        userRepository.save(user);
        return ApiResponse.ok("Account deactivated successfully", null);
    }

    // Never return the password in responses
    private Map<String, Object> safeUser(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "createdAt", user.getCreatedAt()
        );
    }
}