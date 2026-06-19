package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
public class UsuarioClientMockImpl implements UsuarioClient {

    @Override
    public UsuarioResponseDTO getUsuario(Long id) {
        if (id == 1L) {
            return new UsuarioResponseDTO(
                    1L,
                    "Juan Perez",
                    "juan@example.com",
                    List.of("VEGANO", "SIN_GLUTEN"),
                    LocalDateTime.now().minusDays(30)
            );
        }
        if (id == 2L) {
            return new UsuarioResponseDTO(
                    2L,
                    "Maria Lopez",
                    "maria@example.com",
                    List.of("VEGETARIANO"),
                    LocalDateTime.now().minusDays(15)
            );
        }
        return new UsuarioResponseDTO(
                id,
                "Usuario Test",
                "test@example.com",
                List.of("VEGANO"),
                LocalDateTime.now().minusDays(7)
        );
    }
}
