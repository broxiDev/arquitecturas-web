package com.farmacyfood.recommendation.service;

import com.farmacyfood.recommendation.client.OrdenClient;
import com.farmacyfood.recommendation.client.ProductoClient;
import com.farmacyfood.recommendation.client.UsuarioClient;
import com.farmacyfood.recommendation.dto.OrdenDTO;
import com.farmacyfood.recommendation.dto.OrderItemDTO;
import com.farmacyfood.recommendation.dto.ProductoDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import com.farmacyfood.recommendation.dto.RecomendacionResponseDTO;
import com.farmacyfood.recommendation.dto.UsuarioResponseDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceImplTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private OrdenClient ordenClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private RecomendacionServiceImpl recomendacionService;

    @Test
    void getRecomendaciones_retornaRecomendacionesExcluyendoComprados() {
        Long userId = 1L;

        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                userId, "Juan", "juan@test.com",
                List.of("VEGANO"), LocalDateTime.now().minusDays(30));
        when(usuarioClient.getUsuario(userId)).thenReturn(usuario);

        List<OrderItemDTO> items = new ArrayList<>();
        items.add(new OrderItemDTO(101L, "Producto Comprado", 1, 8500.0));
        List<OrdenDTO> ordenes = new ArrayList<>();
        ordenes.add(new OrdenDTO(1L, userId, 1L, items, 8500.0, "COMPLETED",
                "PAY001", LocalDateTime.now(), LocalDateTime.now()));
        when(ordenClient.getOrdenesByUsuario(userId)).thenReturn(ordenes);

        List<ProductoDTO> productosDisponibles = new ArrayList<>();
        productosDisponibles.add(new ProductoDTO(102L, "Producto Nuevo", "Desc",
                "VEGANO", new BigDecimal("8000"), "/img.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
        productosDisponibles.add(new ProductoDTO(101L, "Producto Comprado", "Desc",
                "VEGANO", new BigDecimal("7000"), "/img2.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
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
        assertFalse(contieneComprado, "No deberia recomendar productos ya comprados");
    }

    @Test
    void getRecomendaciones_filtraPorPreferenciasDietarias() {
        Long userId = 2L;

        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                userId, "Maria", "maria@test.com",
                List.of("VEGETARIANO"), LocalDateTime.now().minusDays(15));
        when(usuarioClient.getUsuario(userId)).thenReturn(usuario);

        when(ordenClient.getOrdenesByUsuario(userId)).thenReturn(new ArrayList<>());

        List<ProductoDTO> productosVegetarianos = new ArrayList<>();
        productosVegetarianos.add(new ProductoDTO(201L, "Ensalada Vegetariana", "Desc",
                "VEGETARIANO", new BigDecimal("7000"), "/img.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
        when(productoClient.getProductosByCategoria("VEGETARIANO")).thenReturn(productosVegetarianos);

        recomendacionService.getRecomendaciones(userId);

        verify(productoClient).getProductosByCategoria("VEGETARIANO");
        verify(productoClient, never()).getProductosByCategoria("VEGANO");
    }

    @Test
    void getRecomendaciones_cuandoUsuarioNoExiste_lanzaExcepcion() {
        Long userId = 99L;
        when(usuarioClient.getUsuario(userId)).thenReturn(null);

        assertThrows(com.farmacyfood.recommendation.exception.UsuarioNotFoundException.class,
                () -> recomendacionService.getRecomendaciones(userId));
    }
}
