package com.farmacyfood.recommendation.client;

import com.farmacyfood.recommendation.dto.ProductoDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class ProductoClientMockImpl implements ProductoClient {

    @Override
    public List<ProductoDTO> getProductosByCategoria(String categoria) {
        List<ProductoDTO> productos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        if ("VEGANO".equalsIgnoreCase(categoria)) {
            productos.add(new ProductoDTO(301L, "Ensalada Vegana Premium", "Ensalada con quinoa y vegetales", "VEGANO", new BigDecimal("8500.00"), "/images/ensalada-vegana.jpg", now.minusDays(30), now.minusDays(5)));
            productos.add(new ProductoDTO(302L, "Bowl Vegano Energético", "Bowl con arroz integral y tofu", "VEGANO", new BigDecimal("9200.00"), "/images/bowl-vegano.jpg", now.minusDays(25), now.minusDays(3)));
            productos.add(new ProductoDTO(303L, "Wrap Vegano Mediterráneo", "Wrap con hummus y vegetales", "VEGANO", new BigDecimal("7800.00"), "/images/wrap-vegano.jpg", now.minusDays(20), now.minusDays(2)));
            productos.add(new ProductoDTO(304L, "Smoothie Bowl Vegano", "Bowl de frutas con granola", "VEGANO", new BigDecimal("6500.00"), "/images/smoothie-bowl.jpg", now.minusDays(15), now.minusDays(1)));
        } else if ("SIN_GLUTEN".equalsIgnoreCase(categoria)) {
            productos.add(new ProductoDTO(401L, "Ensalada Sin Gluten", "Ensalada fresca sin gluten", "SIN_GLUTEN", new BigDecimal("7500.00"), "/images/ensalada-sin-gluten.jpg", now.minusDays(30), now.minusDays(5)));
            productos.add(new ProductoDTO(402L, "Bowl Sin Gluten Proteico", "Bowl con pollo y quinoa", "SIN_GLUTEN", new BigDecimal("9800.00"), "/images/bowl-sin-gluten.jpg", now.minusDays(25), now.minusDays(3)));
            productos.add(new ProductoDTO(403L, "Wrap Sin Gluten", "Wrap de lechuga con pollo", "SIN_GLUTEN", new BigDecimal("8200.00"), "/images/wrap-sin-gluten.jpg", now.minusDays(20), now.minusDays(2)));
        } else if ("VEGETARIANO".equalsIgnoreCase(categoria)) {
            productos.add(new ProductoDTO(501L, "Ensalada Vegetariana", "Ensalada con queso y nueces", "VEGETARIANO", new BigDecimal("7200.00"), "/images/ensalada-vegetariana.jpg", now.minusDays(30), now.minusDays(5)));
            productos.add(new ProductoDTO(502L, "Bowl Vegetariano", "Bowl con arroz y vegetales", "VEGETARIANO", new BigDecimal("8800.00"), "/images/bowl-vegetariano.jpg", now.minusDays(25), now.minusDays(3)));
            productos.add(new ProductoDTO(503L, "Wrap Vegetariano", "Wrap con queso y vegetales", "VEGETARIANO", new BigDecimal("7500.00"), "/images/wrap-vegetariano.jpg", now.minusDays(20), now.minusDays(2)));
        } else {
            productos.add(new ProductoDTO(601L, "Producto Genérico 1", "Descripción genérica", categoria, new BigDecimal("6000.00"), "/images/generico1.jpg", now.minusDays(30), now.minusDays(5)));
            productos.add(new ProductoDTO(602L, "Producto Genérico 2", "Descripción genérica", categoria, new BigDecimal("7000.00"), "/images/generico2.jpg", now.minusDays(25), now.minusDays(3)));
        }

        return productos;
    }
}
