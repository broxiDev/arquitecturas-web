package com.farmacyfood.user.service;

import com.farmacyfood.user.dto.OrderSummaryDTO;
import com.farmacyfood.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    User update(Long id, User user);

    User updatePreferences(Long id, List<String> dietaryPreferences);

    void delete(Long id);

    List<OrderSummaryDTO> getPurchaseHistory(Long userId);
}
