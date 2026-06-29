package com.farmacyfood.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UserRegistrationRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String authUsername,
        List<String> dietaryPreferences
) {}
