package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.client.DisponibilidadNotificacionDTO;
import com.farmacyfood.fridge.client.HeladeraStatusChangeDTO;
import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.dto.HeladeraCreateDTO;
import com.farmacyfood.fridge.dto.HeladeraResponseDTO;
import com.farmacyfood.fridge.dto.HeladeraUpdateDTO;
import com.farmacyfood.fridge.entity.Heladera;
import com.farmacyfood.fridge.entity.StockHeladera;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.repository.HeladeraRepository;
import com.farmacyfood.fridge.repository.StatusEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class HeladeraServiceImplTest {

    @Mock
    private HeladeraRepository heladeraRepository;

    @Mock
    private StatusEventRepository statusEventRepository;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private HeladeraServiceImpl heladeraService;

    @Test
    void findById_returnsHeladera() {
        Heladera heladera = Heladera.builder().id(1L).name("Heladera Test").latitude(-34.6).longitude(-58.4)
            .address("Av. Siempre Viva 123").status("ACTIVE").cocinaId("COCINA-DULCE").createdAt(LocalDateTime.now()).build();

        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(heladera));

        HeladeraResponseDTO result = heladeraService.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Heladera Test", result.name());
        assertEquals("ACTIVE", result.status());
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(heladeraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class, () -> heladeraService.findById(99L));
    }

    @Test
    void findAll_returnsAll() {
        when(heladeraRepository.findAll()).thenReturn(List.of(
            Heladera.builder().id(1L).name("H1").latitude(-34.6).longitude(-58.4).address("Dir 1").status("ACTIVE").cocinaId("COCINA-DULCE").build(),
            Heladera.builder().id(2L).name("H2").latitude(-34.7).longitude(-58.5).address("Dir 2").status("MAINTENANCE").cocinaId("COCINA-CELIACA").build()
        ));

        List<HeladeraResponseDTO> result = heladeraService.findAll(null, null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void findAll_filtersByStatus() {
        when(heladeraRepository.findByStatus("MAINTENANCE")).thenReturn(List.of(
            Heladera.builder().id(2L).name("H2").latitude(-34.7).longitude(-58.5).address("Dir 2").status("MAINTENANCE").cocinaId("COCINA-CELIACA").build()
        ));

        List<HeladeraResponseDTO> result = heladeraService.findAll("MAINTENANCE", null, null, null);

        assertEquals(1, result.size());
        assertEquals("MAINTENANCE", result.get(0).status());
    }

    @Test
    void create_savesAndReturns() {
        HeladeraCreateDTO dto = new HeladeraCreateDTO("Nueva Heladera", -34.6, -58.4, "Dir 123", "ACTIVE", "COCINA-DULCE");

        when(heladeraRepository.save(any(Heladera.class))).thenAnswer(inv -> {
            Heladera h = inv.getArgument(0);
            h.setId(1L);
            h.setCreatedAt(LocalDateTime.now());
            h.setUpdatedAt(LocalDateTime.now());
            return h;
        });

        HeladeraResponseDTO result = heladeraService.create(dto);

        assertEquals("Nueva Heladera", result.name());
        assertEquals(-34.6, result.latitude());
        assertEquals("COCINA-DULCE", result.cocinaId());
        verify(heladeraRepository).save(any(Heladera.class));
    }

    @Test
    void update_changesNameAndStatus() {
        Heladera existing = Heladera.builder().id(1L).name("Old Name").latitude(-34.6).longitude(-58.4)
            .address("Same Dir").status("OUT_OF_SERVICE").cocinaId("COCINA-DULCE").createdAt(LocalDateTime.now()).build();

        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(heladeraRepository.save(any(Heladera.class))).thenAnswer(inv -> inv.getArgument(0));

        HeladeraUpdateDTO dto = new HeladeraUpdateDTO("New Name", null, null, null, "ACTIVE", null);
        HeladeraResponseDTO result = heladeraService.update(1L, dto);

        assertEquals("New Name", result.name());
        assertEquals("ACTIVE", result.status());
        verify(statusEventRepository).save(any());
        verify(notificacionClient).notificarProductoDisponible(any(DisponibilidadNotificacionDTO.class));
    }

    @Test
    void update_throwsWhenNotFound() {
        when(heladeraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class,
            () -> heladeraService.update(99L, new HeladeraUpdateDTO(null, null, null, null, null, null)));
    }

    @Test
    void delete_removesHeladera() {
        when(heladeraRepository.existsById(1L)).thenReturn(true);

        heladeraService.delete(1L);

        verify(heladeraRepository).deleteById(1L);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(heladeraRepository.existsById(99L)).thenReturn(false);

        assertThrows(HeladeraNotFoundException.class, () -> heladeraService.delete(99L));
    }

    @Test
    void update_cuandoPasaAOutOfService_enviaAlerta() {
        Heladera existing = Heladera.builder().id(1L).name("Heladera 1").latitude(-34.6).longitude(-58.4)
            .address("Dir").status("ACTIVE").cocinaId("COCINA-DULCE").createdAt(LocalDateTime.now()).build();

        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(heladeraRepository.save(any(Heladera.class))).thenAnswer(inv -> inv.getArgument(0));

        heladeraService.update(1L, new HeladeraUpdateDTO(null, null, null, null, "OUT_OF_SERVICE", null));

        verify(notificacionClient).notificarHeladeraStatusChange(any(HeladeraStatusChangeDTO.class));
    }

    @Test
    void update_cuandoPasaAMaintenance_enviaAlerta() {
        Heladera existing = Heladera.builder().id(1L).name("Heladera 1").latitude(-34.6).longitude(-58.4)
            .address("Dir").status("ACTIVE").cocinaId("COCINA-DULCE").createdAt(LocalDateTime.now()).build();

        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(heladeraRepository.save(any(Heladera.class))).thenAnswer(inv -> inv.getArgument(0));

        heladeraService.update(1L, new HeladeraUpdateDTO(null, null, null, null, "MAINTENANCE", null));

        verify(notificacionClient).notificarHeladeraStatusChange(any(HeladeraStatusChangeDTO.class));
    }

    @Test
    void update_cuandoMismoStatus_noEnviaAlerta() {
        Heladera existing = Heladera.builder().id(1L).name("Heladera 1").latitude(-34.6).longitude(-58.4)
            .address("Dir").status("ACTIVE").cocinaId("COCINA-DULCE").createdAt(LocalDateTime.now()).build();

        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(heladeraRepository.save(any(Heladera.class))).thenAnswer(inv -> inv.getArgument(0));

        heladeraService.update(1L, new HeladeraUpdateDTO(null, null, null, null, "ACTIVE", null));

        verify(notificacionClient, never()).notificarHeladeraStatusChange(any());
    }
}
