package com.example.security.dto;

import com.example.security.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}
