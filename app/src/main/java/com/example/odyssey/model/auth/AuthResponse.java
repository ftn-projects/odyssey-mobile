package com.example.odyssey.model.auth;

public class AuthResponse {
    private String token;
    private Long expiresIn;

    public AuthResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
