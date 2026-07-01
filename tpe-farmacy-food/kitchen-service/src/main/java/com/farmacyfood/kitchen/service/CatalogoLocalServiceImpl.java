package com.farmacyfood.kitchen.service;

import com.farmacyfood.kitchen.dto.CatalogoLocalRequestDTO;
import com.farmacyfood.kitchen.dto.CatalogoLocalResponseDTO;
import com.farmacyfood.kitchen.entity.postgres.Cocina;
import com.farmacyfood.kitchen.entity.postgres.CatalogoProducto;
import com.farmacyfood.kitchen.exception.CatalogoException;
import com.farmacyfood.kitchen.repository.CatalogoProductoRepository;
import com.farmacyfood.kitchen.repository.CocinaRepository;
import com.farmacyfood.kitchen.security.AuthContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogoLocalServiceImpl implements CatalogoLocalService {

    private final CatalogoProductoRepository catalogoProductoRepository;
    private final CocinaRepository cocinaRepository;

    @Override
    public CatalogoLocalResponseDTO registrar(CatalogoLocalRequestDTO request) {
        Long cocinaId = cocinaDelUsuarioLogueado().getId();
        log.info("Registrando producto '{}' en cocina {}", request.productId(), cocinaId);

        if (request.productId() == null) {
            throw new CatalogoException("El productId es obligatorio");
        }
        if (request.productName() == null || request.productName().isBlank()) {
            throw new CatalogoException("El nombre del producto es obligatorio");
        }

        Optional<CatalogoProducto> existing = catalogoProductoRepository
                .findByCocinaIdAndProductId(cocinaId, request.productId());

        if (existing.isPresent()) {
            throw new CatalogoException(
                    "La cocina " + cocinaId + " ya tiene el producto " + request.productId()
                            + " agregado al catalogo");
        }

        CatalogoProducto producto = CatalogoProducto.builder()
                .productId(request.productId())
                .productName(request.productName())
                .cocinaId(cocinaId)
                .build();
        log.info("Producto {} creado en cocina {}", request.productId(), cocinaId);

        CatalogoProducto saved = catalogoProductoRepository.save(producto);
        return toDTO(saved);
    }

    @Override
    public List<CatalogoLocalResponseDTO> listarDeMiCocina() {
        Long cocinaId = cocinaDelUsuarioLogueado().getId();

        List<CatalogoProducto> productos = catalogoProductoRepository.findByCocinaId(cocinaId);
        List<CatalogoLocalResponseDTO> result = new ArrayList<>();
        for (CatalogoProducto producto : productos) {
            result.add(toDTO(producto));
        }
        return result;
    }

    private Cocina cocinaDelUsuarioLogueado() {
        String username = AuthContext.getCurrentUsername();
        return cocinaRepository.findByUsuario(username)
                .orElseThrow(() -> new CatalogoException("El usuario " + username + " no tiene una cocina asignada"));
    }

    private CatalogoLocalResponseDTO toDTO(CatalogoProducto producto) {
        return new CatalogoLocalResponseDTO(
                producto.getId(),
                producto.getProductId(),
                producto.getProductName(),
                producto.getCocinaId()
        );
    }
}
