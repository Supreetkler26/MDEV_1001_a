package com.example.mdev1001_ice7;// LoginRequestBody.java

import com.squareup.moshi.Json;

public class LoginResponse {

    @Json(name = "username")
    private final String username;

    @Json(name = "password")
    private final String password;

    public LoginResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
