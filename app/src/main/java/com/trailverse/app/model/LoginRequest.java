package com.trailverse.app.model;

public class LoginRequest {
    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
}
