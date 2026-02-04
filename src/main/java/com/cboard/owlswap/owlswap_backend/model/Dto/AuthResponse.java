package com.cboard.owlswap.owlswap_backend.model.Dto;

public class AuthResponse {
    private String token;

    // default constructor
    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}