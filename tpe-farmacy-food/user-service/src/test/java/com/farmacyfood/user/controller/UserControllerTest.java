package com.farmacyfood.user.controller;

import com.farmacyfood.user.entity.User;
import com.farmacyfood.user.exception.GlobalExceptionHandler;
import com.farmacyfood.user.exception.UserNotFoundException;
import com.farmacyfood.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_retorna201() throws Exception {
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        user.setId(1L);
        when(service.register(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Mati",
                                    "email": "mati@test.com",
                                    "authUsername": "mati_user",
                                    "dietaryPreferences": ["vegano"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mati"));
    }

    @Test
    void getAll_retorna200() throws Exception {
        User user1 = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        user1.setId(1L);
        User user2 = new User("Juan", "juan@test.com", "juan_user", List.of("sin gluten"));
        user2.setId(2L);
        when(service.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Juan"));
    }

    @Test
    void getById_retorna200() throws Exception {
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        user.setId(1L);
        when(service.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_retorna404() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByAuthUsername_retorna200() throws Exception {
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano"));
        user.setId(1L);
        when(service.findByAuthUsername("mati_user")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/usuarios/auth-username/mati_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mati"));
    }

    @Test
    void getByAuthUsername_retorna404() throws Exception {
        when(service.findByAuthUsername("no_existe")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/auth-username/no_existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_retorna200() throws Exception {
        User user = new User("Mati Updated", "mati@test.com", "mati_user", List.of("vegano"));
        user.setId(1L);
        when(service.update(eq(1L), any())).thenReturn(user);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Mati Updated",
                                    "email": "mati@test.com",
                                    "authUsername": "mati_user",
                                    "dietaryPreferences": ["vegano"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mati Updated"));
    }

    @Test
    void delete_retorna204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updatePreferences_retorna200() throws Exception {
        User user = new User("Mati", "mati@test.com", "mati_user", List.of("vegano", "gluten-free"));
        user.setId(1L);
        when(service.updatePreferences(eq(1L), any())).thenReturn(user);

        mockMvc.perform(put("/api/v1/usuarios/1/preferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"vegano\", \"gluten-free\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("vegano"));
    }

    @Test
    void updatePreferences_retorna404() throws Exception {
        when(service.updatePreferences(eq(99L), any())).thenThrow(new UserNotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(put("/api/v1/usuarios/99/preferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"vegano\"]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPurchaseHistory_retorna200() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(new User()));
        when(service.getPurchaseHistory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios/1/historial"))
                .andExpect(status().isOk());
    }

    @Test
    void getPurchaseHistory_retorna404() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99/historial"))
                .andExpect(status().isNotFound());
    }

    @Test
    void health_retorna200() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("user-service"));
    }
}
