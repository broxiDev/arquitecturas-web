package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import com.farmacyfood.recommendation.entity.ProductoRecomendado;
import com.farmacyfood.recommendation.entity.RecomendacionResultado;
import com.farmacyfood.recommendation.repository.RecomendacionCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {

    @Mock
    private RecomendacionCacheRepository recomendacionCacheRepository;

    @InjectMocks
    private CacheServiceImpl cacheService;

    @Test
    void obtenerRecomendacionesCacheadas_retornaCacheCuandoEsValido() {
        Long userId = 1L;
        RecomendacionResultado cache = new RecomendacionResultado();
        cache.setUserId(userId);
        cache.setGeneratedAt(LocalDateTime.now().minusHours(2));

        ProductoRecomendado producto = ProductoRecomendado.builder()
                .productId(101L)
                .productName("Producto Test")
                .reason("Razón test")
                .dietaryCategory("VEGANO")
                .build();
        cache.addProducto(producto);

        when(recomendacionCacheRepository.findByUserId(userId)).thenReturn(Optional.of(cache));
        ReflectionTestUtils.setField(cacheService, "ttlHours", 6);

        RecomendacionResponseDTO resultado = cacheService.obtenerRecomendacionesCacheadas(userId);

        assertNotNull(resultado);
        assertEquals(userId, resultado.userId());
        assertEquals(1, resultado.productos().size());
        assertEquals("Producto Test", resultado.productos().get(0).productName());
    }

    @Test
    void obtenerRecomendacionesCacheadas_retornaNullCuandoCacheExpiro() {
        Long userId = 1L;
        RecomendacionResultado cache = new RecomendacionResultado();
        cache.setUserId(userId);
        cache.setGeneratedAt(LocalDateTime.now().minusHours(10));

        when(recomendacionCacheRepository.findByUserId(userId)).thenReturn(Optional.of(cache));
        ReflectionTestUtils.setField(cacheService, "ttlHours", 6);

        RecomendacionResponseDTO resultado = cacheService.obtenerRecomendacionesCacheadas(userId);

        assertNull(resultado);
    }

    @Test
    void obtenerRecomendacionesCacheadas_retornaNullCuandoNoHayCache() {
        Long userId = 1L;

        when(recomendacionCacheRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RecomendacionResponseDTO resultado = cacheService.obtenerRecomendacionesCacheadas(userId);

        assertNull(resultado);
    }

    @Test
    void guardarRecomendacionesEnCache_guardaCorrectamente() {
        Long userId = 1L;
        List<ProductoRecomendadoDTO> productos = new ArrayList<>();
        productos.add(new ProductoRecomendadoDTO(101L, "Producto 1", "Razón 1", "VEGANO"));
        productos.add(new ProductoRecomendadoDTO(102L, "Producto 2", "Razón 2", "SIN_GLUTEN"));

        when(recomendacionCacheRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(recomendacionCacheRepository.save(any(RecomendacionResultado.class)))
                .thenAnswer(i -> i.getArgument(0));

        cacheService.guardarRecomendacionesEnCache(userId, productos);

        verify(recomendacionCacheRepository).save(any(RecomendacionResultado.class));
    }

    @Test
    void guardarRecomendacionesEnCache_actualizaCacheExistente() {
        Long userId = 1L;
        RecomendacionResultado cacheExistente = new RecomendacionResultado();
        cacheExistente.setUserId(userId);
        cacheExistente.setGeneratedAt(LocalDateTime.now().minusHours(10));

        List<ProductoRecomendadoDTO> productos = new ArrayList<>();
        productos.add(new ProductoRecomendadoDTO(101L, "Producto Nuevo", "Razón nueva", "VEGANO"));

        when(recomendacionCacheRepository.findByUserId(userId)).thenReturn(Optional.of(cacheExistente));
        when(recomendacionCacheRepository.save(any(RecomendacionResultado.class)))
                .thenAnswer(i -> i.getArgument(0));

        cacheService.guardarRecomendacionesEnCache(userId, productos);

        verify(recomendacionCacheRepository).save(cacheExistente);
        assertEquals(1, cacheExistente.getProductos().size());
    }
}
