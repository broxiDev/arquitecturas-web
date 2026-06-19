package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.client.UsuarioClient;
import com.farmacyfood.recommendation.dto.PerfilPreferenciaDTO;
import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;
import com.farmacyfood.recommendation.entity.PerfilPreferencia;
import com.farmacyfood.recommendation.repository.PerfilPreferenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenciaServiceImplTest {

    @Mock
    private PerfilPreferenciaRepository perfilPreferenciaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PreferenciaServiceImpl preferenciaService;

    @Test
    void obtenerPreferencias_retornaCacheLocalCuandoExiste() {
        Long userId = 1L;
        PerfilPreferencia perfil = PerfilPreferencia.builder()
                .userId(userId)
                .dietaryPreferences("VEGANO,SIN_GLUTEN")
                .lastUpdated(LocalDateTime.now())
                .build();

        when(perfilPreferenciaRepository.findByUserId(userId)).thenReturn(Optional.of(perfil));

        PerfilPreferenciaDTO resultado = preferenciaService.obtenerPreferencias(userId);

        assertNotNull(resultado);
        assertEquals(userId, resultado.userId());
        assertEquals(2, resultado.dietaryPreferences().size());
        assertTrue(resultado.dietaryPreferences().contains("VEGANO"));
        assertTrue(resultado.dietaryPreferences().contains("SIN_GLUTEN"));
        verify(usuarioClient, never()).getUsuario(any());
    }

    @Test
    void obtenerPreferencias_consultaUsuarioClientCuandoNoHayCache() {
        Long userId = 2L;
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                userId, "Test User", "test@example.com", List.of("VEGETARIANO"), LocalDateTime.now()
        );

        when(perfilPreferenciaRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(usuarioClient.getUsuario(userId)).thenReturn(usuario);
        when(perfilPreferenciaRepository.save(any(PerfilPreferencia.class))).thenAnswer(i -> i.getArgument(0));

        PerfilPreferenciaDTO resultado = preferenciaService.obtenerPreferencias(userId);

        assertNotNull(resultado);
        assertEquals(userId, resultado.userId());
        assertEquals(1, resultado.dietaryPreferences().size());
        assertTrue(resultado.dietaryPreferences().contains("VEGETARIANO"));
        verify(perfilPreferenciaRepository).save(any(PerfilPreferencia.class));
    }

    @Test
    void obtenerPreferencias_manejaPreferenciasVacias() {
        Long userId = 3L;
        PerfilPreferencia perfil = PerfilPreferencia.builder()
                .userId(userId)
                .dietaryPreferences("")
                .lastUpdated(LocalDateTime.now())
                .build();

        when(perfilPreferenciaRepository.findByUserId(userId)).thenReturn(Optional.of(perfil));

        PerfilPreferenciaDTO resultado = preferenciaService.obtenerPreferencias(userId);

        assertNotNull(resultado);
        assertTrue(resultado.dietaryPreferences().isEmpty());
    }
}
