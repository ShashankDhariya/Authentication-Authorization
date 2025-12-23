package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
//      Passwords are stored using BCrypt which performs one-way hashing with
//      salting to prevent brute-force and rainbow table attacks
        return new BCryptPasswordEncoder();
    }
}
