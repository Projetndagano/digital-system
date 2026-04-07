package com.group.payment.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @Email(message = "Valid email required")
    private String email;
}