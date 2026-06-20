package com.farmacyfood.kitchen.client;

import com.farmacyfood.kitchen.dto.FridgeRemainderDTO;
import com.farmacyfood.kitchen.dto.ProductRemainderDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Implementación mock del cliente de fridge-service (activa en profile dev)
// Retorna datos diferenciados por cocina para desarrollo individual sin depender de fridge-service
@Component
@Profile("dev")
public class FridgeClientMockImpl implements FridgeClient {

    @Override
    public List<FridgeRemainderDTO> getRemainderByKitchen(String cocinaId) {
        List<FridgeRemainderDTO> result = new ArrayList<>();

        switch (cocinaId) {
            case "COCINA-DULCE" -> {
                List<ProductRemainderDTO> products1 = new ArrayList<>();
                products1.add(new ProductRemainderDTO(101L, "Brownie de Chocolate", 3));
                products1.add(new ProductRemainderDTO(102L, "Cheesecake", 2));
                products1.add(new ProductRemainderDTO(103L, "Tiramisú", 2));
                result.add(new FridgeRemainderDTO(1L, products1));

                List<ProductRemainderDTO> products2 = new ArrayList<>();
                products2.add(new ProductRemainderDTO(101L, "Brownie de Chocolate", 2));
                products2.add(new ProductRemainderDTO(102L, "Cheesecake", 1));
                products2.add(new ProductRemainderDTO(103L, "Tiramisú", 3));
                result.add(new FridgeRemainderDTO(2L, products2));
            }
            case "COCINA-CELIACA" -> {
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
            }
            case "COCINA-VEGANA" -> {
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
            }
            default -> {
                List<ProductRemainderDTO> products1 = new ArrayList<>();
                products1.add(new ProductRemainderDTO(101L, "Producto Genérico 1", 1));
                products1.add(new ProductRemainderDTO(102L, "Producto Genérico 2", 1));
                result.add(new FridgeRemainderDTO(1L, products1));
            }
        }

        return result;
    }
}