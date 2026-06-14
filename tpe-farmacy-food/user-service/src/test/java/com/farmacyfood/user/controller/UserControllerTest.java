package com.farmacyfood.user.controller;

import com.farmacyfood.user.entity.User;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_retorna201() throws Exception {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
        user.setId(1L);
        when(service.register(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/usuarios/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Mati",
                                    "email": "mati@test.com",
                                    "passwordHash": "hash123",
                                    "dietaryPreferences": ["vegano"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mati"));
    }

    @Test
    void getById_retorna200() throws Exception {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano"));
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
    void updatePreferences_retorna200() throws Exception {
        User user = new User("Mati", "mati@test.com", "hash123", List.of("vegano", "gluten-free"));
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
        when(service.updatePreferences(eq(99L), any())).thenThrow(new RuntimeException("Not found"));

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
