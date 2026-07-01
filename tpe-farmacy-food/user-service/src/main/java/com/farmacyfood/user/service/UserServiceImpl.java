package com.farmacyfood.user.service;

import com.farmacyfood.user.client.OrderServiceClient;
import com.farmacyfood.user.dto.OrderSummaryDTO;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.exception.DuplicateAuthUsernameException;
import com.farmacyfood.user.exception.DuplicateEmailException;
import com.farmacyfood.user.exception.InvalidRoleException;
import com.farmacyfood.user.exception.UserNotFoundException;
import com.farmacyfood.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (repository.findByAuthUsername(user.getAuthUsername()).isPresent()) {
            throw new DuplicateAuthUsernameException("El usuario " + user.getAuthUsername() + " ya tiene un perfil");
        }
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByAuthUsername(String authUsername) {
        return repository.findByAuthUsername(authUsername);
    }

    @Override
    @Transactional
    public User update(Long id, User updated) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        validarPreferencias(
                updated.getDietaryPreferences() != null ? updated.getDietaryPreferences() : user.getDietaryPreferences()
        );
        if (updated.getName() != null) user.setName(updated.getName());
        if (updated.getEmail() != null) user.setEmail(updated.getEmail());
        if (updated.getAuthUsername() != null) user.setAuthUsername(updated.getAuthUsername());
        if (updated.getDietaryPreferences() != null) user.setDietaryPreferences(updated.getDietaryPreferences());
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updatePreferences(Long id, List<String> dietaryPreferences) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        validarPreferencias(dietaryPreferences);
        user.setDietaryPreferences(dietaryPreferences);
        return repository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> getPurchaseHistory(Long userId) {
        return orderClient.getOrdersByUserId(userId);
    }

    private void validarPreferencias(List<String> dietaryPreferences) {
        if (dietaryPreferences == null || dietaryPreferences.isEmpty()) return;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esCliente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("cliente"));
        if (!esCliente) {
            throw new InvalidRoleException("Solo los usuarios con rol 'cliente' pueden tener preferencias dietarias");
        }
    }
}
