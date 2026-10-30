package com.incubyte.payflow.auth;

public record RegisterRequest(
    String username,
    String password,
    String email,
    String firstName,
    String lastName
) {
}
