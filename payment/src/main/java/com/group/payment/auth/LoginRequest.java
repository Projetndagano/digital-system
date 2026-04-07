package com.group.payment.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}