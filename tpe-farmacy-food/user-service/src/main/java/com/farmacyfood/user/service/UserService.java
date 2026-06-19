package com.farmacyfood.user.service;

import com.farmacyfood.user.dto.OrderSummaryDTO;
import com.farmacyfood.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(User user);

    Optional<User> findById(Long id);

    User updatePreferences(Long id, List<String> dietaryPreferences);

    List<OrderSummaryDTO> getPurchaseHistory(Long userId);
}
