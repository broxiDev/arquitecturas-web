package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.VentaHistoricaResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile("dev")
public class OrdenClientMockImpl implements OrdenClient {

    @Override
    public List<VentaHistoricaResponseDTO> getVentasRecientes(LocalDate desde, LocalDate hasta) {
        return List.of(
            new VentaHistoricaResponseDTO(101L, "Ensalada César", 1L, 12, new BigDecimal("6000.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(101L, "Ensalada César", 2L, 8, new BigDecimal("4000.00"), LocalDate.now().minusDays(2)),
            new VentaHistoricaResponseDTO(102L, "Bowl Proteico", 1L, 5, new BigDecimal("3500.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(102L, "Bowl Proteico", 1L, 7, new BigDecimal("4900.00"), LocalDate.now().minusDays(3)),
            new VentaHistoricaResponseDTO(103L, "Wrap de Pollo", 3L, 10, new BigDecimal("4500.00"), LocalDate.now().minusDays(1)),
            new VentaHistoricaResponseDTO(103L, "Wrap de Pollo", 2L, 6, new BigDecimal("2700.00"), LocalDate.now().minusDays(2))
        );
    }
}
