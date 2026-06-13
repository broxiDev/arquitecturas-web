package com.farmacyfood.user.service;

import com.farmacyfood.user.client.OrderServiceClient;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final OrderServiceClient orderClient;

    public UserServiceImpl(UserRepository repository, OrderServiceClient orderClient) {
        this.repository = repository;
        this.orderClient = orderClient;
    }

    @Override
    @Transactional
    public User register(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public User updatePreferences(Long id, List<String> dietaryPreferences) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        user.setDietaryPreferences(dietaryPreferences);
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getPurchaseHistory(Long userId) {
        return orderClient.getOrdersByUserId(userId);
    }
}
