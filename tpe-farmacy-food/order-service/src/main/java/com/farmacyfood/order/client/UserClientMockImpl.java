package com.farmacyfood.order.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("dev")
public class UserClientMockImpl implements UserClient {

    private static final Map<Long, UserResponseDTO> USERS_BY_ID = Map.ofEntries(
            Map.entry(1L,  new UserResponseDTO(1L,  "Usuario 1",         "usuario1@test.com")),
            Map.entry(2L,  new UserResponseDTO(2L,  "Juan Pérez",        "juan@test.com")),
            Map.entry(3L,  new UserResponseDTO(3L,  "Carolina Ruiz",     "caro@test.com")),
            Map.entry(4L,  new UserResponseDTO(4L,  "Pedro Martínez",    "pedro@test.com")),
            Map.entry(5L,  new UserResponseDTO(5L,  "Ana López",         "ana@test.com")),
            Map.entry(6L,  new UserResponseDTO(6L,  "Matías Bordonaro",  "matias@test.com")),
            Map.entry(7L,  new UserResponseDTO(7L,  "Fiorella Di Fiore", "fiorella@test.com")),
            Map.entry(8L,  new UserResponseDTO(8L,  "Nahuel Di Fiore",   "nahuel@test.com")),
            Map.entry(9L,  new UserResponseDTO(9L,  "Gabriel Marrero",   "gabriel@test.com")),
            Map.entry(10L, new UserResponseDTO(10L, "Ale Machado",       "ale@test.com"))
    );

    private static final Map<String, UserResponseDTO> USERS_BY_USERNAME = Map.of(
            "auth_usuario1",  USERS_BY_ID.get(1L),
            "auth_juan",      USERS_BY_ID.get(2L),
            "auth_caro",      USERS_BY_ID.get(3L),
            "auth_pedro",     USERS_BY_ID.get(4L),
            "auth_ana",       USERS_BY_ID.get(5L),
            "auth_matias",    USERS_BY_ID.get(6L),
            "auth_fiorella",  USERS_BY_ID.get(7L),
            "auth_nahuel",    USERS_BY_ID.get(8L),
            "auth_gabriel",   USERS_BY_ID.get(9L),
            "auth_ale",       USERS_BY_ID.get(10L)
    );

    @Override
    public UserResponseDTO getUser(Long userId) {
        UserResponseDTO user = USERS_BY_ID.get(userId);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + userId);
        }
        return user;
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        UserResponseDTO user = USERS_BY_USERNAME.get(username);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
        return user;
    }
}