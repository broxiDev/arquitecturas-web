package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.client.OrdenClient;
import com.farmacyfood.recommendation.client.ProductoClient;
import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.OrderItemDTO;
import com.farmacyfood.recommendation.dto.PerfilPreferenciaDTO;
import com.farmacyfood.recommendation.dto.ProductoDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecomendacionServiceImpl implements RecomendacionService {

    private final PreferenciaService preferenciaService;
    private final CacheService cacheService;
    private final OrdenClient ordenClient;
    private final ProductoClient productoClient;

    private static final int TOP_N = 5;

    @Override
    public RecomendacionResponseDTO getRecomendaciones(Long userId) {
        // Primero verifico si hay recomendaciones cacheadas válidas
        RecomendacionResponseDTO cacheado = cacheService.obtenerRecomendacionesCacheadas(userId);
        if (cacheado != null) {
            return cacheado;
        }

        // Si no hay cache, genero nuevas recomendaciones
        List<ProductoRecomendadoDTO> recomendaciones = generarRecomendaciones(userId);

        // Guardo en cache
        cacheService.guardarRecomendacionesEnCache(userId, recomendaciones);

        return new RecomendacionResponseDTO(userId, recomendaciones, java.time.LocalDateTime.now());
    }

    private List<ProductoRecomendadoDTO> generarRecomendaciones(Long userId) {
        // Obtengo las preferencias del usuario
        PerfilPreferenciaDTO preferencias = preferenciaService.obtenerPreferencias(userId);

        // Obtengo el historial de compras del usuario
        List<OrdenDTO> historial = obtenerHistorial(userId);

        // Extraigo los IDs de productos ya comprados (iterando sobre los items de cada orden)
        List<Long> productosComprados = new ArrayList<>();
        for (OrdenDTO orden : historial) {
            if (orden.items() != null) {
                for (OrderItemDTO item : orden.items()) {
                    productosComprados.add(item.productId());
                }
            }
        }

        // Obtengo productos candidatos de cada categoría de preferencia
        List<ProductoDTO> productosCandidatos = new ArrayList<>();
        for (String categoria : preferencias.dietaryPreferences()) {
            List<ProductoDTO> productos = productoClient.getProductosByCategoria(categoria);
            for (ProductoDTO producto : productos) {
                productosCandidatos.add(producto);
            }
        }

        // Filtrar productos ya comprados y generar recomendaciones
        List<ProductoRecomendadoDTO> recomendaciones = new ArrayList<>();
        Map<Long, Integer> contadorPopularidad = new HashMap<>();

        for (ProductoDTO producto : productosCandidatos) {
            // Excluir si ya fue comprado
            if (productosComprados.contains(producto.id())) {
                continue;
            }

            // Contar popularidad (en MVP, cada aparición cuenta como 1)
            Integer count = contadorPopularidad.get(producto.id());
            if (count == null) {
                count = 0;
            }
            contadorPopularidad.put(producto.id(), count + 1);
        }

        // Ordenar por popularidad y tomar los top N
        List<Long> productosOrdenados = ordenarPorPopularidad(contadorPopularidad);

        // Generar los DTOs de recomendación
        for (int i = 0; i < productosOrdenados.size() && i < TOP_N; i++) {
            Long productId = productosOrdenados.get(i);
            ProductoDTO producto = buscarProducto(productosCandidatos, productId);

            if (producto != null) {
                String reason = "Producto recomendado basado en tus preferencias dietarias";
                recomendaciones.add(new ProductoRecomendadoDTO(
                        producto.id(),
                        producto.name(),
                        reason,
                        producto.dietaryCategory()
                ));
            }
        }

        return recomendaciones;
    }

    private List<OrdenDTO> obtenerHistorial(Long userId) {
        // Primero verifico si hay historial cacheado
        List<OrdenDTO> historialCacheado = cacheService.obtenerHistorialCacheado(userId);
        if (historialCacheado != null) {
            return historialCacheado;
        }

        // Si no hay cache, consulto al order-service
        List<OrdenDTO> historial = ordenClient.getOrdenesByUsuario(userId);

        // Guardo en cache
        cacheService.guardarHistorialEnCache(userId, historial);

        return historial;
    }

    private List<Long> ordenarPorPopularidad(Map<Long, Integer> contadorPopularidad) {
        List<Long> productos = new ArrayList<>(contadorPopularidad.keySet());

        // Ordenamiento burbuja simple por popularidad (descendente)
        for (int i = 0; i < productos.size(); i++) {
            for (int j = i + 1; j < productos.size(); j++) {
                Integer countI = contadorPopularidad.get(productos.get(i));
                Integer countJ = contadorPopularidad.get(productos.get(j));
                if (countJ > countI) {
                    Long temp = productos.get(i);
                    productos.set(i, productos.get(j));
                    productos.set(j, temp);
                }
            }
        }

        return productos;
    }

    private ProductoDTO buscarProducto(List<ProductoDTO> productos, Long productId) {
        for (ProductoDTO producto : productos) {
            if (producto.id().equals(productId)) {
                return producto;
            }
        }
        return null;
    }
}
