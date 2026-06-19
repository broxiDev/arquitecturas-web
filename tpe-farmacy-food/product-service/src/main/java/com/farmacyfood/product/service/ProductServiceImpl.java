package com.farmacyfood.product.service;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;
import com.farmacyfood.product.entity.Product;
import com.farmacyfood.product.exception.ProductNotFoundException;
import com.farmacyfood.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(String category) {
        log.info("Obteniendo todos los productos. Categoría: {}", category != null ? category : "Todas");
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productRepository.findByDietaryCategory(category);
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Obteniendo producto por ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con id: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creando nuevo producto: {}", productRequest.name());
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .dietaryCategory(productRequest.dietaryCategory())
                .price(productRequest.price())
                .imageUrl(productRequest.imageUrl())
                .nutritionalInfo(productRequest.nutritionalInfo())
                .conservacionTemperature(productRequest.conservacionTemperature())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Actualizando producto con ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con id: " + id));

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setDietaryCategory(productRequest.dietaryCategory());
        product.setPrice(productRequest.price());
        product.setImageUrl(productRequest.imageUrl());
        product.setNutritionalInfo(productRequest.nutritionalInfo());
        product.setConservacionTemperature(productRequest.conservacionTemperature());

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String getProductNameById(Long id) {
        log.info("Obteniendo nombre del producto con ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con id: " + id));
        return product.getName();
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
