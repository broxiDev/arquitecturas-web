package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.UserClient;
import com.farmacyfood.kitchen.dto.CocinaCreateDTO;
import com.farmacyfood.kitchen.dto.CocinaResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.Cocina;
import com.farmacyfood.kitchen.exception.CocinaException;
import com.farmacyfood.kitchen.repository.CocinaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CocinaServiceImpl implements CocinaService {

    private final CocinaRepository cocinaRepository;
    private final UserClient userClient;

    @Override
    public CocinaResponseDTO crear(CocinaCreateDTO request) {
        log.info("Creando cocina '{}' para usuario {}", request.nombre(), request.usuarioId());

        if (!userClient.existeUsuario(request.usuarioId())) {
            throw new CocinaException("El usuario " + request.usuarioId() + " no existe");
        }

        if (cocinaRepository.existsByUsuarioId(request.usuarioId())) {
            throw new CocinaException("El usuario " + request.usuarioId() + " ya tiene una cocina asignada");
        }

        Cocina cocina = Cocina.builder()
                .nombre(request.nombre())
                .usuarioId(request.usuarioId())
                .build();

        Cocina saved = cocinaRepository.save(cocina);
        log.info("Cocina creada con id {} para usuario {}", saved.getId(), saved.getUsuarioId());
        return toDTO(saved);
    }

    @Override
    public CocinaResponseDTO buscar(Long cocinaId) {
        Cocina cocina = cocinaRepository.findById(cocinaId)
                .orElseThrow(() -> new CocinaException("No existe cocina con id: " + cocinaId));
        return toDTO(cocina);
    }

    private CocinaResponseDTO toDTO(Cocina cocina) {
        return new CocinaResponseDTO(
                cocina.getId(),
                cocina.getNombre(),
                cocina.getUsuarioId(),
                cocina.getCreatedAt()
        );
    }
}
