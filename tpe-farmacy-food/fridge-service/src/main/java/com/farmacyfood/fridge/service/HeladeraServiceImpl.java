package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.client.DisponibilidadNotificacionDTO;
import com.farmacyfood.fridge.client.HeladeraStatusChangeDTO;
import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;
import com.farmacyfood.fridge.entity.EventoEstadoHeladera;
import com.farmacyfood.fridge.entity.Heladera;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.repository.HeladeraRepository;
import com.farmacyfood.fridge.repository.StatusEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeladeraServiceImpl implements HeladeraService {

    private final HeladeraRepository heladeraRepository;
    private final StatusEventRepository statusEventRepository;
    private final NotificacionClient notificacionClient;

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
        try {
            Heladera heladera = Heladera.builder()
                .name(dto.name())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .address(dto.address())
                .status(dto.status())
                .build();
            Heladera saved = heladeraRepository.save(heladera);
            return toDTO(saved);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public HeladeraResponseDTO update(Long id, HeladeraUpdateDTO dto) {
        try {
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
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!heladeraRepository.existsById(id)) {
                throw new HeladeraNotFoundException("No existe heladera con id: " + id);
            }
            heladeraRepository.deleteById(id);
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public HeladeraResponseDTO linkCocina(Long heladeraId, String username) {
        try {
            Heladera heladera = heladeraRepository.findById(heladeraId)
                .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

            heladera.getUsernames().add(username);
            Heladera saved = heladeraRepository.save(heladera);
            return toDTO(saved);
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public HeladeraResponseDTO unlinkCocina(Long heladeraId, String username) {
        try {
            Heladera heladera = heladeraRepository.findById(heladeraId)
                .orElseThrow(() -> new HeladeraNotFoundException("No existe heladera con id: " + heladeraId));

            heladera.getUsernames().remove(username);
            Heladera saved = heladeraRepository.save(heladera);
            return toDTO(saved);
        } catch (HeladeraNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private HeladeraResponseDTO toDTO(Heladera heladera) {
        Set<String> usernames = heladera.getUsernames();
        return new HeladeraResponseDTO(
            heladera.getId(),
            heladera.getName(),
            heladera.getLatitude(),
            heladera.getLongitude(),
            heladera.getAddress(),
            heladera.getStatus(),
            usernames != null ? new HashSet<>(usernames) : new HashSet<>(),
            heladera.getLastMaintenance(),
            heladera.getCreatedAt(),
            heladera.getUpdatedAt()
        );
    }
}
