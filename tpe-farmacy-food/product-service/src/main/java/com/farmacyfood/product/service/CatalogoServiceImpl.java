package com.farmacyfood.product.service;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;
import com.farmacyfood.product.entity.Catalogo;
import com.farmacyfood.product.entity.Product;
import com.farmacyfood.product.exception.InvalidProductDataException;
import com.farmacyfood.product.repository.CatalogoRepository;
import com.farmacyfood.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogoRepository catalogoRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createOrUpdateProduct(String cocinaId, ProductRequest productRequest) {
        log.info("Creando/actualizando producto '{}' en catálogo de cocina '{}'", productRequest.name(), cocinaId);

        validateProductRequest(productRequest);

        Catalogo catalogo = catalogoRepository.findByCocinaId(cocinaId)
                .orElseGet(() -> {
                    log.info("Creando nuevo catálogo para cocina '{}'", cocinaId);
                    return catalogoRepository.save(Catalogo.builder()
                            .cocinaId(cocinaId)
                            .build());
                });

        Optional<Product> existingProduct = productRepository.findByCocinaIdAndName(cocinaId, productRequest.name());

        Product product;
        boolean isUpdate = existingProduct.isPresent();

        if (isUpdate) {
            log.info("Actualizando producto existente en catálogo de cocina '{}'", cocinaId);
            product = existingProduct.get();
            product.setDescription(productRequest.description());
            product.setDietaryCategory(productRequest.dietaryCategory());
            product.setPrice(productRequest.price());
            product.setImageUrl(productRequest.imageUrl());
            product.setNutritionalInfo(productRequest.nutritionalInfo());
            product.setConservacionTemperature(productRequest.conservacionTemperature());
        } else {
            log.info("Creando nuevo producto en catálogo de cocina '{}'", cocinaId);
            product = Product.builder()
                    .name(productRequest.name())
                    .description(productRequest.description())
                    .dietaryCategory(productRequest.dietaryCategory())
                    .price(productRequest.price())
                    .imageUrl(productRequest.imageUrl())
                    .nutritionalInfo(productRequest.nutritionalInfo())
                    .conservacionTemperature(productRequest.conservacionTemperature())
                    .catalogo(catalogo)
                    .build();
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCocina(String cocinaId) {
        log.info("Obteniendo productos para cocina: {}", cocinaId);
        return productRepository.findByCocinaId(cocinaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateProductRequest(ProductRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidProductDataException("El nombre del producto es obligatorio");
        }
        if (request.dietaryCategory() == null || request.dietaryCategory().isBlank()) {
            throw new InvalidProductDataException("La categoría dietaria es obligatoria");
        }
        if (request.price() == null || request.price().signum() <= 0) {
            throw new InvalidProductDataException("El precio debe ser mayor a 0");
        }
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getDietaryCategory(),
                product.getPrice(),
                product.getImageUrl(),
                product.getNutritionalInfo(),
                product.getConservacionTemperature(),
                product.getCatalogo() != null ? product.getCatalogo().getCocinaId() : null,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
