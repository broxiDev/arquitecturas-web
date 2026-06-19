package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.OrderItemDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class OrdenClientMockImpl implements OrdenClient {

    @Override
    public List<OrdenDTO> getOrdenesByUsuario(Long userId) {
        List<OrdenDTO> ordenes = new ArrayList<>();

        if (userId == 1L) {
            List<OrderItemDTO> items1 = new ArrayList<>();
            items1.add(new OrderItemDTO(101L, "Ensalada Vegana", 2, 8500.0));
            ordenes.add(new OrdenDTO(1L, 1L, 1L, items1, 17000.0, "COMPLETED", "PAY001", LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5)));

            List<OrderItemDTO> items2 = new ArrayList<>();
            items2.add(new OrderItemDTO(102L, "Bowl Sin Gluten", 1, 9200.0));
            ordenes.add(new OrdenDTO(2L, 1L, 1L, items2, 9200.0, "COMPLETED", "PAY002", LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3)));

            List<OrderItemDTO> items3 = new ArrayList<>();
            items3.add(new OrderItemDTO(103L, "Wrap Vegano", 1, 7800.0));
            ordenes.add(new OrdenDTO(3L, 1L, 2L, items3, 7800.0, "COMPLETED", "PAY003", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1)));
        } else if (userId == 2L) {
            List<OrderItemDTO> items1 = new ArrayList<>();
            items1.add(new OrderItemDTO(201L, "Ensalada Vegetariana", 1, 7200.0));
            ordenes.add(new OrdenDTO(4L, 2L, 1L, items1, 7200.0, "COMPLETED", "PAY004", LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7)));

            List<OrderItemDTO> items2 = new ArrayList<>();
            items2.add(new OrderItemDTO(202L, "Bowl Vegetariano", 2, 8800.0));
            ordenes.add(new OrdenDTO(5L, 2L, 2L, items2, 17600.0, "COMPLETED", "PAY005", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2)));
        } else {
            List<OrderItemDTO> items = new ArrayList<>();
            items.add(new OrderItemDTO(101L, "Ensalada Vegana", 1, 8500.0));
            ordenes.add(new OrdenDTO(6L, userId, 1L, items, 8500.0, "COMPLETED", "PAY006", LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(10)));
        }

        return ordenes;
    }
}
