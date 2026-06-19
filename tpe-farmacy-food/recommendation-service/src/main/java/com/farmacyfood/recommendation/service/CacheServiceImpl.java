package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.OrderItemDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import com.farmacyfood.recommendation.entity.HistorialComprasCache;
import com.farmacyfood.recommendation.entity.OrdenCache;
import com.farmacyfood.recommendation.entity.ProductoRecomendado;
import com.farmacyfood.recommendation.entity.RecomendacionResultado;
import com.farmacyfood.recommendation.repository.HistorialComprasCacheRepository;
import com.farmacyfood.recommendation.repository.RecomendacionCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RecomendacionCacheRepository recomendacionCacheRepository;
    private final HistorialComprasCacheRepository historialComprasCacheRepository;

    @Value("${recommendation.cache.ttl-hours:6}")
    private int ttlHours;

    @Override
    @Transactional(readOnly = true)
    public RecomendacionResponseDTO obtenerRecomendacionesCacheadas(Long userId) {
        Optional<RecomendacionResultado> cacheOpt = recomendacionCacheRepository.findByUserId(userId);

        if (cacheOpt.isEmpty()) {
            return null;
        }

        RecomendacionResultado cache = cacheOpt.get();

        // Verifico si el cache expiró
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracion = cache.getGeneratedAt().plusHours(ttlHours);

        if (ahora.isAfter(expiracion)) {
            // Cache expirado
            return null;
        }

        // Convierto las entidades a DTOs
        List<ProductoRecomendadoDTO> productosDTO = new ArrayList<>();
        for (ProductoRecomendado producto : cache.getProductos()) {
            productosDTO.add(new ProductoRecomendadoDTO(
                    producto.getProductId(),
                    producto.getProductName(),
                    producto.getReason(),
                    producto.getDietaryCategory()
            ));
        }

        return new RecomendacionResponseDTO(userId, productosDTO, cache.getGeneratedAt());
    }

    @Override
    @Transactional
    public void guardarRecomendacionesEnCache(Long userId, List<ProductoRecomendadoDTO> productos) {
        // Busco si ya existe un cache para este usuario
        Optional<RecomendacionResultado> cacheOpt = recomendacionCacheRepository.findByUserId(userId);

        RecomendacionResultado cache;
        if (cacheOpt.isPresent()) {
            cache = cacheOpt.get();
            cache.clearProductos();
        } else {
            cache = new RecomendacionResultado();
            cache.setUserId(userId);
        }

        cache.setGeneratedAt(LocalDateTime.now());

        // Agrego los productos recomendados
        for (ProductoRecomendadoDTO dto : productos) {
            ProductoRecomendado producto = ProductoRecomendado.builder()
                    .productId(dto.productId())
                    .productName(dto.productName())
                    .reason(dto.reason())
                    .dietaryCategory(dto.dietaryCategory())
                    .build();
            cache.addProducto(producto);
        }

        recomendacionCacheRepository.save(cache);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> obtenerHistorialCacheado(Long userId) {
        Optional<HistorialComprasCache> cacheOpt = historialComprasCacheRepository.findByUserId(userId);

        if (cacheOpt.isEmpty()) {
            return null;
        }

        HistorialComprasCache cache = cacheOpt.get();

        // Verifico si el cache expiró (mismo TTL que recomendaciones)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracion = cache.getLastUpdated().plusHours(ttlHours);

        if (ahora.isAfter(expiracion)) {
            return null;
        }

        // Agrupo las órdenes cacheadas por orderId para reconstruir las OrdenDTO
        Map<Long, List<OrdenCache>> ordenesAgrupadas = new HashMap<>();
        for (OrdenCache orden : cache.getOrdenes()) {
            Long orderId = orden.getOrderId();
            List<OrdenCache> lista = ordenesAgrupadas.get(orderId);
            if (lista == null) {
                lista = new ArrayList<>();
                ordenesAgrupadas.put(orderId, lista);
            }
            lista.add(orden);
        }

        // Convierto a DTOs
        List<OrdenDTO> ordenesDTO = new ArrayList<>();
        for (Map.Entry<Long, List<OrdenCache>> entry : ordenesAgrupadas.entrySet()) {
            Long orderId = entry.getKey();
            List<OrdenCache> items = entry.getValue();

            List<OrderItemDTO> itemsDTO = new ArrayList<>();
            double total = 0;
            LocalDateTime createdAt = null;

            for (OrdenCache item : items) {
                itemsDTO.add(new OrderItemDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ));
                total += item.getUnitPrice() * item.getQuantity();
                if (createdAt == null || item.getPurchasedAt().isBefore(createdAt)) {
                    createdAt = item.getPurchasedAt();
                }
            }

            ordenesDTO.add(new OrdenDTO(
                    orderId,
                    userId,
                    null,
                    itemsDTO,
                    total,
                    "COMPLETED",
                    null,
                    createdAt,
                    createdAt
            ));
        }

        return ordenesDTO;
    }

    @Override
    @Transactional
    public void guardarHistorialEnCache(Long userId, List<OrdenDTO> ordenes) {
        Optional<HistorialComprasCache> cacheOpt = historialComprasCacheRepository.findByUserId(userId);

        HistorialComprasCache cache;
        if (cacheOpt.isPresent()) {
            cache = cacheOpt.get();
            cache.clearOrdenes();
        } else {
            cache = new HistorialComprasCache();
            cache.setUserId(userId);
        }

        cache.setLastUpdated(LocalDateTime.now());

        // Descompongo cada orden en sus items para guardar en cache
        for (OrdenDTO orden : ordenes) {
            if (orden.items() != null) {
                for (OrderItemDTO item : orden.items()) {
                    OrdenCache ordenCache = OrdenCache.builder()
                            .orderId(orden.orderId())
                            .productId(item.productId())
                            .productName(item.productName())
                            .quantity(item.quantity())
                            .unitPrice(item.unitPrice())
                            .purchasedAt(orden.createdAt())
                            .build();
                    cache.addOrden(ordenCache);
                }
            }
        }

        historialComprasCacheRepository.save(cache);
    }
}
