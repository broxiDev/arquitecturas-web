package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import com.farmacyfood.kitchen.exception.PlanNotFoundException;
import com.farmacyfood.kitchen.repository.PlanDiarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanDiarioServiceImplTest {

    @Mock
    private PlanDiarioRepository planDiarioRepository;

    @Mock
    private OrdenClient ordenClient;

    @InjectMocks
    private PlanDiarioServiceImpl planDiarioService;

    @Test
    void getPlanByDate_returnsPlan() {
        LocalDate today = LocalDate.now();
        DailyPlan plan = DailyPlan.builder()
            .id(1L)
            .date(today)
            .createdAt(LocalDateTime.now())
            .items(List.of(
                PlanItem.builder().productId(101L).productName("Ensalada").suggestedQuantity(10).build()
            ))
            .build();

        when(planDiarioRepository.findByDate(today)).thenReturn(Optional.of(plan));

        PlanDiarioResponseDTO result = planDiarioService.getPlanByDate(today);

        assertEquals(1L, result.id());
        assertEquals(today, result.date());
        assertEquals(1, result.items().size());
        assertEquals(101L, result.items().get(0).productId());
    }

    @Test
    void getPlanByDate_throwsWhenNotFound() {
        LocalDate today = LocalDate.now();
        when(planDiarioRepository.findByDate(today)).thenReturn(Optional.empty());

        assertThrows(PlanNotFoundException.class, () -> planDiarioService.getPlanByDate(today));
    }

    @Test
    void generarPlan_calculatesAverageAndSaves() {
        LocalDate date = LocalDate.now();
        LocalDate desde = date.minusDays(7);

        List<VentaHistoricaResponseDTO> ventas = List.of(
            new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 10, new BigDecimal("5000"), date.minusDays(1)),
            new VentaHistoricaResponseDTO(101L, "Ensalada", 1L, 6, new BigDecimal("3000"), date.minusDays(2)),
            new VentaHistoricaResponseDTO(102L, "Bowl", 1L, 4, new BigDecimal("2800"), date.minusDays(1))
        );

        when(ordenClient.getVentasRecientes(desde, date.minusDays(1))).thenReturn(ventas);
        when(planDiarioRepository.findByDate(date)).thenReturn(Optional.empty());
        when(planDiarioRepository.save(any(DailyPlan.class))).thenAnswer(inv -> {
            DailyPlan p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PlanDiarioResponseDTO result = planDiarioService.generarPlan(date);

        assertNotNull(result);
        assertEquals(date, result.date());
        verify(planDiarioRepository).save(any(DailyPlan.class));
    }
}
