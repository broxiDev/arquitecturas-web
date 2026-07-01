package com.farmacyfood.kitchen.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.kitchen.client.UserClient;
import com.farmacyfood.kitchen.constants.AuditMessages;
import com.farmacyfood.kitchen.dto.CocinaCreateDTO;
import com.farmacyfood.kitchen.dto.CocinaResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.Cocina;
import com.farmacyfood.kitchen.exception.CocinaException;
import com.farmacyfood.kitchen.repository.CocinaRepository;
import com.farmacyfood.kitchen.security.AuthContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CocinaServiceImpl implements CocinaService {

    @Autowired
    private AuditLogger auditLogger;

    private final CocinaRepository cocinaRepository;
    private final UserClient userClient;

    @Override
    public CocinaResponseDTO crear(CocinaCreateDTO request) {
        String username = AuthContext.getCurrentUsername();
        String role = AuthContext.getCurrentRole();
        log.info("Creando cocina '{}' para usuario {} (rol {})", request.nombre(), username, role);

        try {
            try {
                userClient.getByAuthUsername(username);
            } catch (Exception e) {
                throw new CocinaException("Usuario autenticado no encontrado en user-service");
            }

            if (cocinaRepository.existsByUsuario(username)) {
                throw new CocinaException("El usuario " + username + " ya tiene una cocina asignada");
            }

            Cocina cocina = Cocina.builder()
                    .nombre(request.nombre())
                    .usuario(username)
                    .build();

            Cocina saved = cocinaRepository.save(cocina);
            log.info("Cocina creada con id {} para usuario {}", saved.getId(), saved.getUsuario());
            CocinaResponseDTO response = toDTO(saved);
            auditLogger.success("CREATE_KITCHEN", AuditMessages.KITCHEN_CREATED, response);
            return response;
        } catch (Exception e) {
            auditLogger.error("CREATE_KITCHEN", "Error al crear cocina: " + e.getMessage(), request);
            throw e;
        }
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
                cocina.getUsuario(),
                cocina.getCreatedAt()
        );
    }
}
