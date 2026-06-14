package com.farmacyfood.user.dto;

import com.farmacyfood.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String email,
        List<String> dietaryPreferences,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDietaryPreferences(),
                user.getCreatedAt()
        );
    }
}
