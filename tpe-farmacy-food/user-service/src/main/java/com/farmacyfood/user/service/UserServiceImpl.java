package com.farmacyfood.user.service;

import com.farmacyfood.user.client.OrderServiceClient;
import com.farmacyfood.user.dto.OrderSummaryDTO;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.exception.DuplicateEmailException;
import com.farmacyfood.user.exception.UserNotFoundException;
import com.farmacyfood.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private OrderServiceClient orderClient;

    @Override
    @Transactional
    public User register(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("El email " + user.getEmail() + " ya está registrado");
        }
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
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        user.setDietaryPreferences(dietaryPreferences);
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> getPurchaseHistory(Long userId) {
        return orderClient.getOrdersByUserId(userId);
    }
}
