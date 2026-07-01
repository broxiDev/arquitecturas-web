package com.farmacyfood.auth.dto;

import java.util.List;

public record UserRegistrationRequest(String name, String email, String authUsername, List<String> dietaryPreferences) {
}
