package com.farmacyfood.auth.controller;

import com.farmacyfood.auth.dto.AuthResponse;
import com.farmacyfood.auth.dto.LoginRequest;
import com.farmacyfood.auth.dto.RegisterRequest;
import com.farmacyfood.auth.exception.DuplicateUserException;
import com.farmacyfood.auth.exception.GlobalExceptionHandler;
import com.farmacyfood.auth.exception.InvalidCredentialsException;
import com.farmacyfood.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registrar_conDatosValidos_retorna201() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("token.jwt.ejemplo"));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "juan",
                                    "password": "pass123",
                                    "rol": "cliente",
                                    "name": "Juan",
                                    "email": "juan@mail.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token.jwt.ejemplo"));
    }

    @Test
    void registrar_conUsernameDuplicado_retorna400() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateUserException("El usuario 'juan' ya existe"));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "juan",
                                    "password": "pass123",
                                    "rol": "cliente"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_conRolAdminDeHeladera_retorna400() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Rol no permitido: 'adminDeHeladera'"));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "juan",
                                    "password": "pass123",
                                    "rol": "adminDeHeladera"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_conCredencialesValidas_retorna200() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("token.jwt.ejemplo"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "juan",
                                    "password": "pass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.ejemplo"));
    }

    @Test
    void login_conPasswordIncorrecto_retorna401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Usuario o contraseña incorrectos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "juan",
                                    "password": "wrongpass"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_conUsuarioInexistente_retorna401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Usuario o contraseña incorrectos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "noexiste",
                                    "password": "pass123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
