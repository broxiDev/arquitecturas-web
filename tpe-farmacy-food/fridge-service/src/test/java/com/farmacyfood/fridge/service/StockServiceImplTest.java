package com.farmacyfood.fridge.service;

import com.farmacyfood.fridge.client.NotificacionClient;
import com.farmacyfood.fridge.dto.FridgeRemainderDTO;
import com.farmacyfood.fridge.dto.StockCreateDTO;
import com.farmacyfood.fridge.dto.StockResponseDTO;
import com.farmacyfood.fridge.dto.StockUpdateDTO;
import com.farmacyfood.fridge.entity.Heladera;
import com.farmacyfood.fridge.entity.StockHeladera;
import com.farmacyfood.fridge.exception.HeladeraNotFoundException;
import com.farmacyfood.fridge.repository.HeladeraRepository;
import com.farmacyfood.fridge.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private HeladeraRepository heladeraRepository;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getStockByHeladera_returnsStockList() {
        Heladera heladera = Heladera.builder().id(1L).build();
        when(stockRepository.findByHeladeraId(1L)).thenReturn(List.of(
            StockHeladera.builder().id(1L).heladera(heladera).productId(101L).productName("Brownie").quantity(10).build(),
            StockHeladera.builder().id(2L).heladera(heladera).productId(102L).productName("Cheesecake").quantity(5).build()
        ));

        List<StockResponseDTO> result = stockService.getStockByHeladera(1L);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).productId());
        assertEquals("Brownie", result.get(0).productName());
        assertEquals(10, result.get(0).quantity());
    }

    @Test
    void addStock_createsAndReturns() {
        Heladera heladera = Heladera.builder().id(1L).build();
        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(heladera));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> {
            StockHeladera s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        StockCreateDTO dto = new StockCreateDTO(101L, "Brownie", 20);
        StockResponseDTO result = stockService.addStock(1L, dto);

        assertEquals(101L, result.productId());
        assertEquals("Brownie", result.productName());
        assertEquals(20, result.quantity());
        assertEquals(1L, result.fridgeId());
    }

    @Test
    void addStock_throwsWhenHeladeraNotFound() {
        when(heladeraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class,
            () -> stockService.addStock(99L, new StockCreateDTO(101L, "Brownie", 5)));
    }

    @Test
    void updateStock_updatesQuantity() {
        Heladera heladera = Heladera.builder().id(1L).build();
        StockHeladera stock = StockHeladera.builder().id(1L).heladera(heladera).productId(101L).productName("Brownie").quantity(10).build();

        when(stockRepository.findByHeladeraIdAndProductId(1L, 101L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> inv.getArgument(0));

        StockUpdateDTO dto = new StockUpdateDTO(101L, null, 5);
        StockResponseDTO result = stockService.updateStock(1L, dto);

        assertEquals(5, result.quantity());
        assertEquals("Brownie", result.productName());
    }

    @Test
    void updateStock_throwsWhenStockNotFound() {
        when(stockRepository.findByHeladeraIdAndProductId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class,
            () -> stockService.updateStock(1L, new StockUpdateDTO(99L, null, 5)));
    }

    @Test
    void getRemainderByCocinaId_returnsRemainder() {
        Heladera heladera1 = Heladera.builder().id(1L).cocinaId("COCINA-DULCE").name("Heladera 1").build();
        Heladera heladera2 = Heladera.builder().id(2L).cocinaId("COCINA-DULCE").name("Heladera 2").build();

        when(heladeraRepository.findByCocinaId("COCINA-DULCE")).thenReturn(List.of(heladera1, heladera2));
        when(stockRepository.findByHeladeraId(1L)).thenReturn(List.of(
            StockHeladera.builder().id(1L).heladera(heladera1).productId(101L).productName("Brownie").quantity(3).build(),
            StockHeladera.builder().id(2L).heladera(heladera1).productId(102L).productName("Cheesecake").quantity(2).build()
        ));
        when(stockRepository.findByHeladeraId(2L)).thenReturn(List.of(
            StockHeladera.builder().id(3L).heladera(heladera2).productId(101L).productName("Brownie").quantity(2).build()
        ));

        List<FridgeRemainderDTO> result = stockService.getRemainderByCocinaId("COCINA-DULCE");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).fridgeId());
        assertEquals(2, result.get(0).products().size());
        assertEquals(101L, result.get(0).products().get(0).productId());
        assertEquals("Brownie", result.get(0).products().get(0).productName());
        assertEquals(3, result.get(0).products().get(0).quantity());
        assertEquals(2L, result.get(1).fridgeId());
        assertEquals(1, result.get(1).products().size());
    }

    @Test
    void getRemainderByCocinaId_returnsEmptyList() {
        when(heladeraRepository.findByCocinaId("COCINA-INEXISTENTE")).thenReturn(List.of());

        List<FridgeRemainderDTO> result = stockService.getRemainderByCocinaId("COCINA-INEXISTENTE");

        assertTrue(result.isEmpty());
    }

    @Test
    void addStock_cuandoHeladeraActiva_notifica() {
        Heladera heladera = Heladera.builder().id(1L).status("ACTIVE").build();
        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(heladera));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> {
            StockHeladera s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        stockService.addStock(1L, new StockCreateDTO(101L, "Brownie", 20));

        verify(notificacionClient).notificarProductoDisponible(any());
    }

    @Test
    void addStock_cuandoHeladeraNoActiva_noNotifica() {
        Heladera heladera = Heladera.builder().id(1L).status("MAINTENANCE").build();
        when(heladeraRepository.findById(1L)).thenReturn(Optional.of(heladera));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> {
            StockHeladera s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        stockService.addStock(1L, new StockCreateDTO(101L, "Brownie", 20));

        verify(notificacionClient, never()).notificarProductoDisponible(any());
    }

    @Test
    void updateStock_cuandoStockPasaDeCeroANotifica() {
        Heladera heladera = Heladera.builder().id(1L).build();
        StockHeladera stock = StockHeladera.builder().id(1L).heladera(heladera).productId(101L).productName("Brownie").quantity(0).build();

        when(stockRepository.findByHeladeraIdAndProductId(1L, 101L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> inv.getArgument(0));

        stockService.updateStock(1L, new StockUpdateDTO(101L, null, 10));

        verify(notificacionClient).notificarProductoDisponible(any());
    }
}
