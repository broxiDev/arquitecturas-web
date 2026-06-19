package com.farmacyfood.fridge.service;

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

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getStockByHeladera_returnsStockList() {
        Heladera heladera = Heladera.builder().id(1L).build();
        when(stockRepository.findByHeladeraId(1L)).thenReturn(List.of(
            StockHeladera.builder().id(1L).heladera(heladera).productId(101L).quantity(10).build(),
            StockHeladera.builder().id(2L).heladera(heladera).productId(102L).quantity(5).build()
        ));

        List<StockResponseDTO> result = stockService.getStockByHeladera(1L);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).productId());
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

        StockCreateDTO dto = new StockCreateDTO(101L, 20);
        StockResponseDTO result = stockService.addStock(1L, dto);

        assertEquals(101L, result.productId());
        assertEquals(20, result.quantity());
        assertEquals(1L, result.fridgeId());
    }

    @Test
    void addStock_throwsWhenHeladeraNotFound() {
        when(heladeraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class,
            () -> stockService.addStock(99L, new StockCreateDTO(101L, 5)));
    }

    @Test
    void updateStock_updatesQuantity() {
        Heladera heladera = Heladera.builder().id(1L).build();
        StockHeladera stock = StockHeladera.builder().id(1L).heladera(heladera).productId(101L).quantity(10).build();

        when(stockRepository.findByHeladeraIdAndProductId(1L, 101L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(StockHeladera.class))).thenAnswer(inv -> inv.getArgument(0));

        StockUpdateDTO dto = new StockUpdateDTO(101L, 5);
        StockResponseDTO result = stockService.updateStock(1L, dto);

        assertEquals(5, result.quantity());
    }

    @Test
    void updateStock_throwsWhenStockNotFound() {
        when(stockRepository.findByHeladeraIdAndProductId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(HeladeraNotFoundException.class,
            () -> stockService.updateStock(1L, new StockUpdateDTO(99L, 5)));
    }
}
