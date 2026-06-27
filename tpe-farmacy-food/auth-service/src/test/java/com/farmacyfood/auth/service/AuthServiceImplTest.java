package com.farmacyfood.auth.service;

import com.farmacyfood.auth.config.JwtUtil;
import com.farmacyfood.auth.dto.AuthResponse;
import com.farmacyfood.auth.dto.LoginRequest;
import com.farmacyfood.auth.dto.RegisterRequest;
import com.farmacyfood.auth.entity.AuthUser;
import com.farmacyfood.auth.exception.DuplicateUserException;
import com.farmacyfood.auth.exception.InvalidCredentialsException;
import com.farmacyfood.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_creaUsuarioYRetornaToken() {
        RegisterRequest request = new RegisterRequest("juan", "pass123", "cliente", "Juan", "juan@mail.com");
        when(authUserRepository.existsByUsername("juan")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10hash");
        when(authUserRepository.save(any())).thenReturn(new AuthUser("juan", "$2a$10hash", "cliente"));
        when(jwtUtil.generateToken("juan", "cliente")).thenReturn("token.jwt.ejemplo");

        AuthResponse response = authService.register(request);

        assertEquals("token.jwt.ejemplo", response.token());
        verify(authUserRepository).save(any());
        verify(jwtUtil).generateToken("juan", "cliente");
    }

    @Test
    void register_conUsernameDuplicado_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest("juan", "pass123", "cliente", "Juan", null);
        when(authUserRepository.existsByUsername("juan")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> authService.register(request));
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void register_conRolInvalido_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest("juan", "pass123", "adminDeHeladera", "Juan", null);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void register_hasheaPasswordCorrectamente() {
        RegisterRequest request = new RegisterRequest("juan", "pass123", "cliente", "Juan", null);
        when(authUserRepository.existsByUsername("juan")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10hash");
        when(authUserRepository.save(any())).thenReturn(new AuthUser("juan", "$2a$10hash", "cliente"));
        when(jwtUtil.generateToken("juan", "cliente")).thenReturn("token");

        authService.register(request);

        verify(passwordEncoder).encode("pass123");
    }

    @Test
    void login_conCredencialesValidas_retornaToken() {
        LoginRequest request = new LoginRequest("juan", "pass123");
        AuthUser user = new AuthUser("juan", "$2a$10hash", "cliente");
        when(authUserRepository.findByUsername("juan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "$2a$10hash")).thenReturn(true);
        when(jwtUtil.generateToken("juan", "cliente")).thenReturn("token.jwt.ejemplo");

        AuthResponse response = authService.login(request);

        assertEquals("token.jwt.ejemplo", response.token());
        verify(jwtUtil).generateToken("juan", "cliente");
    }

    @Test
    void login_conPasswordIncorrecto_lanzaExcepcion() {
        LoginRequest request = new LoginRequest("juan", "wrongpass");
        AuthUser user = new AuthUser("juan", "$2a$10hash", "cliente");
        when(authUserRepository.findByUsername("juan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "$2a$10hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_conUsuarioInexistente_lanzaExcepcion() {
        LoginRequest request = new LoginRequest("noexiste", "pass123");
        when(authUserRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}
