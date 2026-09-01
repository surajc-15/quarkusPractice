package com.example.dtos;

import com.example.entity.Role;

public record AutoLoginResponse(String email, Role role, String username) {
}
