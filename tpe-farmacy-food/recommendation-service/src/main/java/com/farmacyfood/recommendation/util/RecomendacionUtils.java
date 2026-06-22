package com.farmacyfood.recommendation.util;

import com.farmacyfood.recommendation.dto.ProductoDTO;
import com.farmacyfood.recommendation.dto.ProductoRecomendadoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecomendacionUtils {

    private RecomendacionUtils() {
    }

    public static List<ProductoRecomendadoDTO> generarRecomendaciones(
            List<ProductoDTO> productosCandidatos,
            List<Long> productosComprados,
            int topN) {

        Map<Long, Integer> contadorPopularidad = new HashMap<>();

        for (ProductoDTO producto : productosCandidatos) {
            if (productosComprados.contains(producto.id())) {
                continue;
            }

            Integer count = contadorPopularidad.get(producto.id());
            if (count == null) {
                count = 0;
            }
            contadorPopularidad.put(producto.id(), count + 1);
        }

        List<Long> productosOrdenados = ordenarPorPopularidad(contadorPopularidad);

        List<ProductoRecomendadoDTO> recomendaciones = new ArrayList<>();
        for (int i = 0; i < productosOrdenados.size() && i < topN; i++) {
            Long productId = productosOrdenados.get(i);
            ProductoDTO producto = buscarProducto(productosCandidatos, productId);

            if (producto != null) {
                recomendaciones.add(new ProductoRecomendadoDTO(
                        producto.id(),
                        producto.name(),
                        "Producto recomendado basado en tus preferencias dietarias",
                        producto.dietaryCategory()
                ));
            }
        }

        return recomendaciones;
    }

    private static List<Long> ordenarPorPopularidad(Map<Long, Integer> contadorPopularidad) {
        List<Long> productos = new ArrayList<>(contadorPopularidad.keySet());

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

    private static ProductoDTO buscarProducto(List<ProductoDTO> productos, Long productId) {
        for (ProductoDTO producto : productos) {
            if (producto.id().equals(productId)) {
                return producto;
            }
        }
        return null;
    }
}
