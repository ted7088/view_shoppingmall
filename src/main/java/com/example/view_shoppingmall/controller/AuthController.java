package com.example.view_shoppingmall.controller;

import com.example.view_shoppingmall.dto.*;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.repository.UserRepository;
import com.example.view_shoppingmall.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider tokenProvider;

        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String accessToken = tokenProvider.generateToken(authentication);
                        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsername());

                        User user = userRepository.findByUsername(loginRequest.getUsername())
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        AuthResponse response = new AuthResponse(
                                        accessToken,
                                        refreshToken,
                                        user.getUsername(),
                                        user.getEmail(),
                                        user.getRole());

                        return ResponseEntity.ok(response);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new MessageResponse("Invalid username or password"));
                }
        }

        @PostMapping("/signup")
        public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
                // 사용자명 중복 확인
                if (userRepository.existsByUsername(signupRequest.getUsername())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Username is already taken"));
                }

                // 이메일 중복 확인
                if (userRepository.existsByEmail(signupRequest.getEmail())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Email is already in use"));
                }

                // 새 사용자 생성
                User user = new User();
                user.setUsername(signupRequest.getUsername());
                user.setEmail(signupRequest.getEmail());
                user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                user.setRole("USER");

                userRepository.save(user);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new MessageResponse("User registered successfully"));
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
                String refreshToken = request.getRefreshToken();

                if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new MessageResponse("Invalid or expired refresh token"));
                }

                String username = tokenProvider.getUsernameFromToken(refreshToken);
                String newAccessToken = tokenProvider.generateTokenFromUsername(username);
                String newRefreshToken = tokenProvider.generateRefreshToken(username);

                return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken));
        }
//매핑
        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()
                                || "anonymousUser".equals(authentication.getPrincipal())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new MessageResponse("Not authenticated"));
                }

                String username = authentication.getName();
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                AuthResponse response = new AuthResponse(
                                null, // access token은 반환하지 않음
                                null, // refresh token은 반환하지 않음
                                user.getUsername(),
                                user.getEmail(),
                                user.getRole());

                return ResponseEntity.ok(response);
        }
}
