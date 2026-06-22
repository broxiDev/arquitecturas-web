package com.farmacyfood.recommendation.util;

import com.farmacyfood.recommendation.dto.ProductoDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacionUtilsTest {

    @Test
    void generarRecomendaciones_excluyeProductosComprados() {
        List<ProductoDTO> candidatos = new ArrayList<>();
        candidatos.add(new ProductoDTO(101L, "Producto A", "Desc", "VEGANO",
                new BigDecimal("1000"), "/a.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
        candidatos.add(new ProductoDTO(102L, "Producto B", "Desc", "VEGANO",
                new BigDecimal("2000"), "/b.jpg",
                LocalDateTime.now(), LocalDateTime.now()));

        List<Long> comprados = new ArrayList<>();
        comprados.add(101L);

        List<ProductoRecomendadoDTO> resultados =
                RecomendacionUtils.generarRecomendaciones(candidatos, comprados, 5);

        assertEquals(1, resultados.size());
        assertEquals(102L, resultados.get(0).productId());
    }

    @Test
    void generarRecomendaciones_retornaTopN() {
        List<ProductoDTO> candidatos = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            candidatos.add(new ProductoDTO(i, "Producto " + i, "Desc", "VEGANO",
                    new BigDecimal(i * 100), "/img.jpg",
                    LocalDateTime.now(), LocalDateTime.now()));
        }

        List<ProductoRecomendadoDTO> resultados =
                RecomendacionUtils.generarRecomendaciones(candidatos, new ArrayList<>(), 3);

        assertEquals(3, resultados.size());
    }

    @Test
    void generarRecomendaciones_ordenaPorPopularidad() {
        List<ProductoDTO> candidatos = new ArrayList<>();
        candidatos.add(new ProductoDTO(101L, "Producto A", "Desc", "VEGANO",
                new BigDecimal("1000"), "/a.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
        candidatos.add(new ProductoDTO(101L, "Producto A", "Desc", "VEGANO",
                new BigDecimal("1000"), "/a.jpg",
                LocalDateTime.now(), LocalDateTime.now()));
        candidatos.add(new ProductoDTO(102L, "Producto B", "Desc", "VEGANO",
                new BigDecimal("2000"), "/b.jpg",
                LocalDateTime.now(), LocalDateTime.now()));

        List<ProductoRecomendadoDTO> resultados =
                RecomendacionUtils.generarRecomendaciones(candidatos, new ArrayList<>(), 5);

        assertEquals(2, resultados.size());
        assertEquals(101L, resultados.get(0).productId());
    }

    @Test
    void generarRecomendaciones_cuandoNoHayCandidatos_retornaListaVacia() {
        List<ProductoRecomendadoDTO> resultados =
                RecomendacionUtils.generarRecomendaciones(new ArrayList<>(), new ArrayList<>(), 5);

        assertTrue(resultados.isEmpty());
    }

    @Test
    void generarRecomendaciones_cuandoTodoFueComprado_retornaListaVacia() {
        List<ProductoDTO> candidatos = new ArrayList<>();
        candidatos.add(new ProductoDTO(101L, "Producto A", "Desc", "VEGANO",
                new BigDecimal("1000"), "/a.jpg",
                LocalDateTime.now(), LocalDateTime.now()));

        List<Long> comprados = new ArrayList<>();
        comprados.add(101L);

        List<ProductoRecomendadoDTO> resultados =
                RecomendacionUtils.generarRecomendaciones(candidatos, comprados, 5);

        assertTrue(resultados.isEmpty());
    }
}
