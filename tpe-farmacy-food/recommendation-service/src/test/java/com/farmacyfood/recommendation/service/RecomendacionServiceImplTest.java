package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.client.OrdenClient;
import com.farmacyfood.recommendation.client.ProductoClient;
import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.OrderItemDTO;
import com.farmacyfood.recommendation.dto.PerfilPreferenciaDTO;
import com.farmacyfood.recommendation.dto.ProductoDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceImplTest {

    @Mock
    private PreferenciaService preferenciaService;

    @Mock
    private CacheService cacheService;

    @Mock
    private OrdenClient ordenClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private RecomendacionServiceImpl recomendacionService;

    @Test
    void getRecomendaciones_retornaCacheCuandoEsValido() {
        Long userId = 1L;
        List<ProductoRecomendadoDTO> productos = new ArrayList<>();
        productos.add(new ProductoRecomendadoDTO(101L, "Producto Cacheado", "Razón cache", "VEGANO"));
        RecomendacionResponseDTO cacheado = new RecomendacionResponseDTO(userId, productos, LocalDateTime.now());

        when(cacheService.obtenerRecomendacionesCacheadas(userId)).thenReturn(cacheado);

        RecomendacionResponseDTO resultado = recomendacionService.getRecomendaciones(userId);

        assertNotNull(resultado);
        assertEquals(userId, resultado.userId());
        assertEquals(1, resultado.productos().size());
        assertEquals("Producto Cacheado", resultado.productos().get(0).productName());
        verify(preferenciaService, never()).obtenerPreferencias(any());
    }

    @Test
    void getRecomendaciones_generaNuevasCuandoCacheExpiro() {
        Long userId = 1L;

        when(cacheService.obtenerRecomendacionesCacheadas(userId)).thenReturn(null);

        List<String> preferencias = new ArrayList<>();
        preferencias.add("VEGANO");
        PerfilPreferenciaDTO perfilDTO = new PerfilPreferenciaDTO(userId, preferencias);
        when(preferenciaService.obtenerPreferencias(userId)).thenReturn(perfilDTO);

        when(cacheService.obtenerHistorialCacheado(userId)).thenReturn(null);

        List<OrderItemDTO> items = new ArrayList<>();
        items.add(new OrderItemDTO(101L, "Producto Comprado", 1, 8500.0));
        List<OrdenDTO> historial = new ArrayList<>();
        historial.add(new OrdenDTO(1L, userId, 1L, items, 8500.0, "COMPLETED", "PAY001", LocalDateTime.now(), LocalDateTime.now()));
        when(ordenClient.getOrdenesByUsuario(userId)).thenReturn(historial);

        List<ProductoDTO> productosDisponibles = new ArrayList<>();
        productosDisponibles.add(new ProductoDTO(102L, "Producto Nuevo", "Descripción", "VEGANO", new BigDecimal("8000"), "/img.jpg", LocalDateTime.now(), LocalDateTime.now()));
        productosDisponibles.add(new ProductoDTO(101L, "Producto Comprado", "Descripción", "VEGANO", new BigDecimal("7000"), "/img2.jpg", LocalDateTime.now(), LocalDateTime.now()));
        when(productoClient.getProductosByCategoria("VEGANO")).thenReturn(productosDisponibles);

        RecomendacionResponseDTO resultado = recomendacionService.getRecomendaciones(userId);

        assertNotNull(resultado);
        assertEquals(userId, resultado.userId());
        assertTrue(resultado.productos().size() > 0);

        boolean contieneComprado = false;
        for (ProductoRecomendadoDTO prod : resultado.productos()) {
            if (prod.productId().equals(101L)) {
                contieneComprado = true;
            }
        }
        assertFalse(contieneComprado, "No debería recomendar productos ya comprados");

        verify(cacheService).guardarRecomendacionesEnCache(eq(userId), any());
    }

    @Test
    void getRecomendaciones_filtraPorPreferenciasDietarias() {
        Long userId = 2L;

        when(cacheService.obtenerRecomendacionesCacheadas(userId)).thenReturn(null);

        List<String> preferencias = new ArrayList<>();
        preferencias.add("VEGETARIANO");
        PerfilPreferenciaDTO perfilDTO = new PerfilPreferenciaDTO(userId, preferencias);
        when(preferenciaService.obtenerPreferencias(userId)).thenReturn(perfilDTO);

        when(cacheService.obtenerHistorialCacheado(userId)).thenReturn(new ArrayList<>());

        List<ProductoDTO> productosVegetarianos = new ArrayList<>();
        productosVegetarianos.add(new ProductoDTO(201L, "Ensalada Vegetariana", "Desc", "VEGETARIANO", new BigDecimal("7000"), "/img.jpg", LocalDateTime.now(), LocalDateTime.now()));
        when(productoClient.getProductosByCategoria("VEGETARIANO")).thenReturn(productosVegetarianos);

        RecomendacionResponseDTO resultado = recomendacionService.getRecomendaciones(userId);

        assertNotNull(resultado);
        verify(productoClient).getProductosByCategoria("VEGETARIANO");
        verify(productoClient, never()).getProductosByCategoria("VEGANO");
    }
}
