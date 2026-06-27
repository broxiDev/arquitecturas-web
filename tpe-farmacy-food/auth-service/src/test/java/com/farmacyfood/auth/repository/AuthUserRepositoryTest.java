package com.farmacyfood.auth.repository;

import com.farmacyfood.auth.entity.AuthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthUserRepositoryTest {

    @Autowired
    private AuthUserRepository repository;

    @Test
    void save_y_findByUsername() {
        AuthUser user = new AuthUser("juan", "hash123", "cliente");
        AuthUser saved = repository.save(user);

        Optional<AuthUser> found = repository.findByUsername("juan");

        assertTrue(found.isPresent());
        assertEquals("juan", found.get().getUsername());
        assertEquals("hash123", found.get().getPassword());
        assertEquals("cliente", found.get().getRol());
    }

    @Test
    void findByUsername_cuandoNoExiste() {
        Optional<AuthUser> found = repository.findByUsername("noexiste");

        assertTrue(found.isEmpty());
    }

    @Test
    void existsByUsername_retornaTrue_cuandoExiste() {
        repository.save(new AuthUser("maria", "hash456", "cocina"));

        assertTrue(repository.existsByUsername("maria"));
    }

    @Test
    void existsByUsername_retornaFalse_cuandoNoExiste() {
        assertFalse(repository.existsByUsername("noexiste"));
    }
}
