package com.example.security.controller;

import com.example.security.dto.LoginResponse;
import com.example.security.entity.BlackListedTokens;
import com.example.security.entity.User;
import com.example.security.repository.BlackListedTokenRepository;
import com.example.security.repository.UserRepository;
import com.example.security.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlackListedTokenRepository blackListedTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User userRequest) {
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        else if(userRepository.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        newUser.setRole(userRequest.getRole());

        userRepository.save(newUser);

        return ResponseEntity.ok("User Successfully registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));


        if(!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user);

        return  ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public  ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {

        if(authHeader == null || authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String token = authHeader.substring(7);
        Date expiry = jwtUtil.extractExpiration(token);

        BlackListedTokens blackListedTokens = new BlackListedTokens(
                null,
                token,
                expiry.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        blackListedTokenRepository.save(blackListedTokens);
        return ResponseEntity.ok("Successfully logged out");
    }
}
