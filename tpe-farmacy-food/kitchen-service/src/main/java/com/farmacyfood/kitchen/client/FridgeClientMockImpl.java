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
// Retorna datos diferenciados por cocina para desarrollo individual sin depender de fridge-service
@Slf4j
@Component
@Profile("dev")
public class FridgeClientMockImpl implements FridgeClient {

    // Catalogos mock: 1=dulce, 2=celiaca, 3=vegana
    private static final Long COCINA_DULCE = 1L;
    private static final Long COCINA_CELIACA = 2L;
    private static final Long COCINA_VEGANA = 3L;

    @Override
    public List<FridgeRemainderDTO> getRemainderByKitchen(Long cocinaId) {
        List<FridgeRemainderDTO> result = new ArrayList<>();

        if (COCINA_DULCE.equals(cocinaId)) {
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
        } else if (COCINA_CELIACA.equals(cocinaId)) {
            List<ProductRemainderDTO> products1 = new ArrayList<>();
            products1.add(new ProductRemainderDTO(201L, "Tostada de Palta Sin Gluten", 3));
            products1.add(new ProductRemainderDTO(202L, "Bowl de Quinoa Sin Gluten", 2));
            products1.add(new ProductRemainderDTO(203L, "Rolls de Primavera de Arroz", 2));
            result.add(new FridgeRemainderDTO(3L, products1));

            List<ProductRemainderDTO> products2 = new ArrayList<>();
            products2.add(new ProductRemainderDTO(201L, "Tostada de Palta Sin Gluten", 1));
            products2.add(new ProductRemainderDTO(202L, "Bowl de Quinoa Sin Gluten", 2));
            products2.add(new ProductRemainderDTO(203L, "Rolls de Primavera de Arroz", 3));
            result.add(new FridgeRemainderDTO(4L, products2));
        } else if (COCINA_VEGANA.equals(cocinaId)) {
            List<ProductRemainderDTO> products1 = new ArrayList<>();
            products1.add(new ProductRemainderDTO(301L, "Buddha Bowl Vegano", 3));
            products1.add(new ProductRemainderDTO(302L, "Salteado de Tofu", 2));
            products1.add(new ProductRemainderDTO(303L, "Curry de Garbanzos", 3));
            result.add(new FridgeRemainderDTO(5L, products1));

            List<ProductRemainderDTO> products2 = new ArrayList<>();
            products2.add(new ProductRemainderDTO(301L, "Buddha Bowl Vegano", 2));
            products2.add(new ProductRemainderDTO(302L, "Salteado de Tofu", 1));
            products2.add(new ProductRemainderDTO(303L, "Curry de Garbanzos", 2));
            result.add(new FridgeRemainderDTO(6L, products2));
        } else {
            List<ProductRemainderDTO> products1 = new ArrayList<>();
            products1.add(new ProductRemainderDTO(101L, "Producto Generico 1", 1));
            products1.add(new ProductRemainderDTO(102L, "Producto Generico 2", 1));
            result.add(new FridgeRemainderDTO(1L, products1));
        }

        return result;
    }

    @Override
    public void cargarStockEnHeladera(Long heladeraId, Long cocinaId, List<CargaProductoDTO> productos) {
        log.info("Mock: cargando {} productos en heladera {} para cocina {}",
                productos.size(), heladeraId, cocinaId);
        for (CargaProductoDTO producto : productos) {
            log.info("  -> heladera={}, catalogo={}, productId={}, name={}, qty={}, price={}",
                    heladeraId, cocinaId, producto.productId(), producto.productName(),
                    producto.quantity(), producto.price());
        }
    }
}
