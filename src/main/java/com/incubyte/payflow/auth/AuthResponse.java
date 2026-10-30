package com.incubyte.payflow.auth;

public record AuthResponse(String token, String username, String role) {
}
