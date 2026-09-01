package com.example.dtos;

import com.example.entity.enums.Role;

public record AutoLoginResponse(String email, Role role, String username) {
}
