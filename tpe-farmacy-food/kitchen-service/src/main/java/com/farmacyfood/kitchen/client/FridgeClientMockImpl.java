package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.CargaProductoDTO;
import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.ProductRemainderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Implementacion mock del cliente de fridge-service (activa en profile dev)
@Slf4j
@Component
@Profile("dev")
public class FridgeClientMockImpl implements FridgeClient {

    @Override
    public List<FridgeRemainderDTO> getRemainderByKitchen() {
        List<FridgeRemainderDTO> result = new ArrayList<>();

        List<ProductRemainderDTO> products1 = new ArrayList<>();
        products1.add(new ProductRemainderDTO(101L, "Brownie de Chocolate", 3));
        products1.add(new ProductRemainderDTO(102L, "Cheesecake", 2));
        products1.add(new ProductRemainderDTO(103L, "Tiramisu", 2));
        result.add(new FridgeRemainderDTO(1L, products1));

        List<ProductRemainderDTO> products2 = new ArrayList<>();
        products2.add(new ProductRemainderDTO(101L, "Brownie de Chocolate", 2));
        products2.add(new ProductRemainderDTO(102L, "Cheesecake", 1));
        products2.add(new ProductRemainderDTO(103L, "Tiramisu", 3));
        result.add(new FridgeRemainderDTO(2L, products2));

        return result;
    }

    @Override
    public void cargarStockEnHeladera(Long heladeraId, List<CargaProductoDTO> productos) {
        log.info("Mock: cargando {} productos en heladera {}", productos.size(), heladeraId);
        for (CargaProductoDTO producto : productos) {
            log.info("  -> heladera={}, productId={}, name={}, qty={}, price={}",
                    heladeraId, producto.productId(), producto.productName(),
                    producto.quantity(), producto.price());
        }
    }
}
