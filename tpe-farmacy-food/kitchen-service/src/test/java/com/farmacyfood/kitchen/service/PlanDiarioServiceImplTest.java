package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.FridgeClient;
import com.farmacyfood.kitchen.client.OrdenClient;
import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.PlanDiarioResponseDTO;
import com.farmacyfood.kitchen.dto.ProductRemainderDTO;
import com.farmacyfood.kitchen.dto.ProductoVentaDTO;
import com.farmacyfood.kitchen.entity.postgres.DailyPlan;
import com.farmacyfood.kitchen.entity.postgres.PlanItem;
import com.farmacyfood.kitchen.exception.PlanAlreadyExistsException;
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
import java.util.ArrayList;
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

    @Mock
    private FridgeClient fridgeClient;

    @InjectMocks
    private PlanDiarioServiceImpl planDiarioService;

    private static final Long COCINA_ID = 1L;

    @Test
    void getPlanByDate_returnsPlan() {
        LocalDate today = LocalDate.now();
        DailyPlan plan = DailyPlan.builder()
            .id(1L)
            .date(today)
            .cocinaId(COCINA_ID)
            .createdAt(LocalDateTime.now())
            .items(List.of(
                PlanItem.builder().productId(101L).productName("Brownie de Chocolate").suggestedQuantity(10).build()
            ))
            .build();

        when(planDiarioRepository.findByDateAndCocinaId(today, COCINA_ID)).thenReturn(Optional.of(plan));

        PlanDiarioResponseDTO result = planDiarioService.getPlanByDate(today, COCINA_ID);

        assertEquals(1L, result.id());
        assertEquals(today, result.date());
        assertEquals(COCINA_ID, result.cocinaId());
        assertEquals(1, result.items().size());
        assertEquals(101L, result.items().get(0).productId());
    }

    @Test
    void getPlanByDate_throwsWhenNotFound() {
        LocalDate today = LocalDate.now();
        when(planDiarioRepository.findByDateAndCocinaId(today, COCINA_ID)).thenReturn(Optional.empty());

        assertThrows(PlanNotFoundException.class, () -> planDiarioService.getPlanByDate(today, COCINA_ID));
    }

    @Test
    void generarPlan_calculatesAverageAndSaves() {
        LocalDate date = LocalDate.now();
        LocalDate from = date.minusDays(7);

        List<ProductoVentaDTO> sales = new ArrayList<>();
        sales.add(new ProductoVentaDTO(101L, "Brownie de Chocolate", 20, new BigDecimal("15000")));
        sales.add(new ProductoVentaDTO(102L, "Cheesecake", 12, new BigDecimal("11400")));

        List<FridgeRemainderDTO> fridges = new ArrayList<>();
        List<ProductRemainderDTO> products = new ArrayList<>();
        products.add(new ProductRemainderDTO(101L, "Brownie de Chocolate", 3));
        fridges.add(new FridgeRemainderDTO(1L, products));

        when(ordenClient.getSalesByKitchen(COCINA_ID, from, date.minusDays(1))).thenReturn(sales);
        when(fridgeClient.getRemainderByKitchen(COCINA_ID)).thenReturn(fridges);
        when(planDiarioRepository.findByDateAndCocinaId(date, COCINA_ID)).thenReturn(Optional.empty());
        when(planDiarioRepository.save(any(DailyPlan.class))).thenAnswer(inv -> {
            DailyPlan p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PlanDiarioResponseDTO result = planDiarioService.generarPlan(date, COCINA_ID);

        assertNotNull(result);
        assertEquals(date, result.date());
        assertEquals(COCINA_ID, result.cocinaId());
        verify(planDiarioRepository).save(any(DailyPlan.class));
    }

    @Test
    void generarPlan_throwsWhenPlanAlreadyExists() {
        LocalDate date = LocalDate.now();
        DailyPlan existingPlan = DailyPlan.builder().id(1L).date(date).cocinaId(COCINA_ID).items(List.of()).build();

        when(planDiarioRepository.findByDateAndCocinaId(date, COCINA_ID)).thenReturn(Optional.of(existingPlan));

        assertThrows(PlanAlreadyExistsException.class, () -> planDiarioService.generarPlan(date, COCINA_ID));
        verify(planDiarioRepository, never()).save(any());
    }
}
