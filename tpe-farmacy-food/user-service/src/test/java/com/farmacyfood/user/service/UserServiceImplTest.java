package com.farmacyfood.user.service;

import com.farmacyfood.user.client.OrderServiceClient;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.exception.DuplicateEmailException;
import com.farmacyfood.user.exception.UserNotFoundException;
import com.farmacyfood.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private OrderServiceClient orderClient;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void register_guardaUsuario() {
        when(repository.findByEmail("mati@test.com")).thenReturn(Optional.empty());
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        when(repository.save(user)).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = service.register(user);

        assertEquals(1L, result.getId());
        assertEquals("Mati", result.getName());
        verify(repository).findByEmail("mati@test.com");
        verify(repository).save(user);
    }

    @Test
    void register_cuandoEmailYaExiste_lanzaExcepcion() {
        when(repository.findByEmail("mati@test.com")).thenReturn(Optional.of(new User()));
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));

        assertThrows(DuplicateEmailException.class, () -> service.register(user));
        verify(repository, never()).save(any());
    }

    @Test
    void findById_cuandoExiste() {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        user.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = service.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updatePreferences_cuandoExiste() {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        user.setId(1L);
        List<String> newPrefs = List.of("vegano", "gluten-free");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        User result = service.updatePreferences(1L, newPrefs);

        assertEquals(newPrefs, result.getDietaryPreferences());
        verify(repository).save(user);
    }

    @Test
    void updatePreferences_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updatePreferences(99L, List.of("vegano")));
    }

    @Test
    void getPurchaseHistory() {
        List<String> mockHistory = List.of("order1", "order2");
        doReturn(mockHistory).when(orderClient).getOrdersByUserId(1L);

        List<?> result = service.getPurchaseHistory(1L);

        assertEquals(2, result.size());
        verify(orderClient).getOrdersByUserId(1L);
    }
}
