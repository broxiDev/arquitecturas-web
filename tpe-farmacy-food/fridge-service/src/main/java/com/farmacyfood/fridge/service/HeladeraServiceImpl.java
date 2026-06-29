package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.client.DisponibilidadNotificacionDTO;
import com.farmacyfood.fridge.client.HeladeraStatusChangeDTO;
import com.farmacyfood.fridge.client.KitchenClient;
import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;
import com.farmacyfood.fridge.entity.EventoEstadoHeladera;
import com.farmacyfood.fridge.entity.Heladera;
import com.farmacyfood.fridge.exception.CocinaNotFoundException;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.repository.HeladeraRepository;
import com.farmacyfood.fridge.repository.StatusEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeladeraServiceImpl implements HeladeraService {

    private final HeladeraRepository heladeraRepository;
    private final StatusEventRepository statusEventRepository;
    private final NotificacionClient notificacionClient;
    private final KitchenClient kitchenClient;

    @Override
    @Transactional(readOnly = true)
    public List<HeladeraResponseDTO> findAll(String status, Double lat, Double lng, Double radius) {
        List<Heladera> heladeras;

        if (lat != null && lng != null && radius != null) {
            heladeras = heladeraRepository.findNearby(lat, lng, radius);
        } else if (status != null && !status.isBlank()) {
            heladeras = heladeraRepository.findByStatus(status);
        } else {
            heladeras = heladeraRepository.findAll();
        }

        return heladeras.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HeladeraResponseDTO findById(Long id) {
        Heladera heladera = heladeraRepository.findById(id)
            .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + id));
        return toDTO(heladera);
    }

    @Override
    @Transactional
    public HeladeraResponseDTO create(HeladeraCreateDTO dto) {
        Heladera heladera = Heladera.builder()
            .name(dto.name())
            .latitude(dto.latitude())
            .longitude(dto.longitude())
            .address(dto.address())
            .status(dto.status())
            .build();
        Heladera saved = heladeraRepository.save(heladera);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public HeladeraResponseDTO update(Long id, HeladeraUpdateDTO dto) {
        Heladera heladera = heladeraRepository.findById(id)
            .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + id));

        String oldStatus = heladera.getStatus();

        if (dto.name() != null) heladera.setName(dto.name());
        if (dto.latitude() != null) heladera.setLatitude(dto.latitude());
        if (dto.longitude() != null) heladera.setLongitude(dto.longitude());
        if (dto.address() != null) heladera.setAddress(dto.address());
        if (dto.status() != null) heladera.setStatus(dto.status());

        Heladera saved = heladeraRepository.save(heladera);

        if (dto.status() != null && !dto.status().equals(oldStatus)) {
            statusEventRepository.save(EventoEstadoHeladera.builder()
                .heladeraId(saved.getId())
                .oldStatus(oldStatus)
                .newStatus(dto.status())
                .build());

            if ("ACTIVE".equals(dto.status()) && ("OUT_OF_SERVICE".equals(oldStatus) || "MAINTENANCE".equals(oldStatus))) {
                List<Long> productIds = saved.getStockItems() != null
                    ? saved.getStockItems().stream().map(s -> s.getProductId()).toList()
                    : List.of();
                notificacionClient.notificarProductoDisponible(
                    new DisponibilidadNotificacionDTO(saved.getId(), productIds));
            }

            if (("OUT_OF_SERVICE".equals(dto.status()) || "MAINTENANCE".equals(dto.status()))
                && !dto.status().equals(oldStatus)) {
                notificacionClient.notificarHeladeraStatusChange(
                    new HeladeraStatusChangeDTO(saved.getId(), saved.getName(), dto.status(), oldStatus));
            }
        }

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!heladeraRepository.existsById(id)) {
            throw new HeladeraNotFoundException("No existe heladera con id: " + id);
        }
        heladeraRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HeladeraResponseDTO linkCocina(Long heladeraId, Long cocinaId) {
        Heladera heladera = heladeraRepository.findById(heladeraId)
            .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

        if (!kitchenClient.cocinaExists(cocinaId)) {
            throw new CocinaNotFoundException("No existe cocina con id: " + cocinaId);
        }

        heladera.getCocinaIds().add(cocinaId);
        Heladera saved = heladeraRepository.save(heladera);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public HeladeraResponseDTO unlinkCocina(Long heladeraId, Long cocinaId) {
        Heladera heladera = heladeraRepository.findById(heladeraId)
            .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

        heladera.getCocinaIds().remove(cocinaId);
        Heladera saved = heladeraRepository.save(heladera);
        return toDTO(saved);
    }

    private HeladeraResponseDTO toDTO(Heladera heladera) {
        return new HeladeraResponseDTO(
            heladera.getId(),
            heladera.getName(),
            heladera.getLatitude(),
            heladera.getLongitude(),
            heladera.getAddress(),
            heladera.getStatus(),
            heladera.getCocinaIds(),
            heladera.getLastMaintenance(),
            heladera.getCreatedAt(),
            heladera.getUpdatedAt()
        );
    }
}
