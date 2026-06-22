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
import com.farmacyfood.recommendation.exception.UsuarioNotFoundException;
import com.farmacyfood.recommendation.util.RecomendacionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecomendacionServiceImpl implements RecomendacionService {

    private final UsuarioClient usuarioClient;
    private final OrdenClient ordenClient;
    private final ProductoClient productoClient;

    private static final int TOP_N = 5;

    @Override
    public RecomendacionResponseDTO getRecomendaciones(Long userId) {
        UsuarioResponseDTO usuario = usuarioClient.getUsuario(userId);
        if (usuario == null) {
            throw new UsuarioNotFoundException(userId);
        }

        List<OrdenDTO> ordenes = ordenClient.getOrdenesByUsuario(userId);

        List<Long> productosComprados = new ArrayList<>();
        for (OrdenDTO orden : ordenes) {
            if (orden.items() != null) {
                for (OrderItemDTO item : orden.items()) {
                    productosComprados.add(item.productId());
                }
            }
        }

        List<ProductoDTO> productosCandidatos = new ArrayList<>();
        for (String categoria : usuario.dietaryPreferences()) {
            List<ProductoDTO> productos = productoClient.getProductosByCategoria(categoria);
            for (ProductoDTO producto : productos) {
                productosCandidatos.add(producto);
            }
        }

        List<ProductoRecomendadoDTO> recomendaciones =
                RecomendacionUtils.generarRecomendaciones(productosCandidatos, productosComprados, TOP_N);

        return new RecomendacionResponseDTO(userId, recomendaciones, java.time.LocalDateTime.now());
    }
}
