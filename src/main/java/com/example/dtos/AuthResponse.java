package com.example.dtos;

import com.example.entity.enums.Role;

public class AuthResponse {
    private String username;
    private Role role;
    private String email;
    private String token;

    public String getToken() {
        return token;
    }

    public AuthResponse() {
    }

    public AuthResponse(String username, Role role, String email, String token) {
        this.username = username;
        this.role = role;
        this.email = email;
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
