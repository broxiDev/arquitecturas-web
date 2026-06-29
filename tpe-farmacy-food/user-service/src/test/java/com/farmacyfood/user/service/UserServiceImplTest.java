package com.farmacyfood.user.service;

import com.farmacyfood.user.client.OrderServiceClient;
import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.exception.DuplicateAuthUsernameException;
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
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        when(repository.save(user)).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = service.register(user);

        assertEquals(1L, result.getId());
        assertEquals("Mati", result.getName());
        verify(repository).findByEmail("mati@test.com");
        verify(repository).findByAuthUsername("mati_user");
        verify(repository).save(user);
    }

    @Test
    void register_cuandoEmailYaExiste_lanzaExcepcion() {
        when(repository.findByEmail("mati@test.com")).thenReturn(Optional.of(new User()));
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));

        assertThrows(DuplicateEmailException.class, () -> service.register(user));
        verify(repository, never()).save(any());
    }

    @Test
    void register_cuandoAuthUsernameYaExiste_lanzaExcepcion() {
        when(repository.findByEmail("mati@test.com")).thenReturn(Optional.empty());
        when(repository.findByAuthUsername("mati_user")).thenReturn(Optional.of(new User()));
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));

        assertThrows(DuplicateAuthUsernameException.class, () -> service.register(user));
        verify(repository, never()).save(any());
    }

    @Test
    void findAll_retornaTodos() {
        User user1 = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        user1.setId(1L);
        User user2 = new User("Juan", "juan@test.com", "juan_user", List.of("sin gluten"));
        user2.setId(2L);
        when(repository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Mati", result.get(0).getName());
        assertEquals("Juan", result.get(1).getName());
        verify(repository).findAll();
    }

    @Test
    void update_cuandoExiste_actualizaCampos() {
        User existing = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        existing.setId(1L);
        User updated = new User("Mati Nuevo", "mati@test.com", "mati_user", List.of("vegano", "gluten-free"));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.update(1L, updated);

        assertEquals("Mati Nuevo", result.getName());
        assertEquals(2, result.getDietaryPreferences().size());
        verify(repository).save(existing);
    }

    @Test
    void update_cuandoNoExiste_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.update(99L, new User("Nadie", "x@x.com", "nadie_user", List.of())));
    }

    @Test
    void delete_cuandoExiste_elimina() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_cuandoNoExiste_lanzaExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void findById_cuandoExiste() {
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
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
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
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
