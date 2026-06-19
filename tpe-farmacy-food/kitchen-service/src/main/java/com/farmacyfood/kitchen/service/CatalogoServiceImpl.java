package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.client.ProductoClient;
import com.farmacyfood.kitchen.dto.ProductoRequest;
import com.farmacyfood.kitchen.dto.ProductoResponse;
import com.farmacyfood.kitchen.exception.CatalogoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final ProductoClient productoClient;

    @Override
    public ProductoResponse registrarProducto(String cocinaId, ProductoRequest request) {
        log.info("Service: registrando producto '{}' en catálogo de cocina '{}'", request.name(), cocinaId);

        if (request.name() == null || request.name().isBlank()) {
            throw new CatalogoException("El nombre del producto es obligatorio");
        }
        if (request.dietaryCategory() == null || request.dietaryCategory().isBlank()) {
            throw new CatalogoException("La categoría dietaria es obligatoria");
        }
        if (request.price() == null || request.price().signum() <= 0) {
            throw new CatalogoException("El precio debe ser mayor a 0");
        }

        try {
            ProductoResponse response = productoClient.registrarProductoEnCatalogo(cocinaId, request);
            log.info("Producto '{}' registrado exitosamente con ID {} en catálogo de cocina '{}'",
                    request.name(), response.id(), cocinaId);
            return response;
        } catch (Exception e) {
            log.error("Error al registrar producto '{}' en catálogo de cocina '{}': {}",
                    request.name(), cocinaId, e.getMessage());
            throw new CatalogoException("Error al registrar producto en el catálogo: " + e.getMessage());
        }
    }
}
