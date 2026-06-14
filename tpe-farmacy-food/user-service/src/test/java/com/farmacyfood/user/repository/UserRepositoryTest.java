package com.farmacyfood.user.repository;

import com.farmacyfood.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void save_y_findById() {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        User saved = repository.save(user);

        Optional<User> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Mati", found.get().getName());
        assertEquals("mati@test.com", found.get().getEmail());
        assertEquals(List.of("vegano"), found.get().getDietaryPreferences());
    }

    @Test
    void findByEmail_cuandoExiste() {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        repository.save(user);

        Optional<User> found = repository.findByEmail("mati@test.com");

        assertTrue(found.isPresent());
        assertEquals("Mati", found.get().getName());
    }

    @Test
    void findByEmail_cuandoNoExiste() {
        Optional<User> found = repository.findByEmail("noexiste@test.com");

        assertTrue(found.isEmpty());
    }
}
