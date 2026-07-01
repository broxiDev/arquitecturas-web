package com.farmacyfood.kitchen.service;

import com.farmacyfood.audit.client.AuditLogger;
import com.farmacyfood.kitchen.constants.AuditMessages;
import com.farmacyfood.kitchen.dto.CargaHeladeraRequestDTO;
import com.farmacyfood.kitchen.dto.CargaProductoDTO;
import com.farmacyfood.kitchen.entity.postgres.CatalogoProducto;
import com.farmacyfood.kitchen.exception.CargaHeladerasException;
import com.farmacyfood.kitchen.client.FridgeClient;
import com.farmacyfood.kitchen.repository.CatalogoProductoRepository;
import com.farmacyfood.kitchen.security.CocinaContextResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargaHeladerasServiceImpl implements CargaHeladerasService {

    @Autowired
    private AuditLogger auditLogger;

    private final CatalogoProductoRepository catalogoProductoRepository;
    private final FridgeClient fridgeClient;
    private final CocinaContextResolver cocinaContextResolver;

    @Override
    public void cargar(CargaHeladeraRequestDTO request) {
        Long cocinaId = cocinaContextResolver.resolver().getId();
        log.info("Cargando {} productos de la cocina {} en heladera {}",
                request.productos().size(), cocinaId, request.heladeraId());

        try {
            for (CargaProductoDTO producto : request.productos()) {
                Optional<CatalogoProducto> catalogoEntry = catalogoProductoRepository
                        .findByCocinaIdAndProductId(cocinaId, producto.productId());

                if (catalogoEntry.isEmpty()) {
                    throw new CargaHeladerasException(
                            "El producto " + producto.productId() + " no existe en el catalogo de la cocina "
                                    + cocinaId);
                }

                CatalogoProducto catalogo = catalogoEntry.get();

                if (!catalogo.getProductName().equals(producto.productName())) {
                    log.warn("El nombre del producto {} en el request ('{}') difiere del catalogo ('{}')",
                            producto.productId(), producto.productName(), catalogo.getProductName());
                }
            }

            fridgeClient.cargarStockEnHeladera(request.heladeraId(), cocinaId, request.productos());

            log.info("Carga exitosa de {} productos en heladera {}", request.productos().size(), request.heladeraId());
            auditLogger.success("LOAD_FRIDGE", AuditMessages.FRIDGE_LOADED, request);
        } catch (Exception e) {
            auditLogger.error("LOAD_FRIDGE", "Error al cargar productos en heladera: " + e.getMessage(), request);
            throw e;
        }
    }
}
