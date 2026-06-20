package com.farmacyfood.product.service;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;
import com.farmacyfood.product.entity.Catalogo;
import com.farmacyfood.product.entity.Product;
import com.farmacyfood.product.exception.InvalidProductDataException;
import com.farmacyfood.product.repository.CatalogoRepository;
import com.farmacyfood.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceImplTest {

    @Mock
    private CatalogoRepository catalogoRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CatalogoServiceImpl catalogoService;

    private ProductRequest validRequest;
    private Catalogo testCatalogo;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        validRequest = new ProductRequest(
                "Ensalada Test",
                "Descripción test",
                "VEGANO",
                new BigDecimal("8500.00"),
                "/images/test.jpg",
                "Calorías: 350kcal",
                new BigDecimal("4.00")
        );

        testCatalogo = Catalogo.builder()
                .id(1L)
                .cocinaId("cocina-sur")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Ensalada Test")
                .description("Descripción test")
                .dietaryCategory("VEGANO")
                .price(new BigDecimal("8500.00"))
                .imageUrl("/images/test.jpg")
                .nutritionalInfo("Calorías: 350kcal")
                .conservacionTemperature(new BigDecimal("4.00"))
                .catalogo(testCatalogo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createOrUpdateProduct_whenNewProduct_createsProduct() {
        when(catalogoRepository.findByCocinaId("cocina-sur")).thenReturn(Optional.of(testCatalogo));
        when(productRepository.findByCocinaIdAndName("cocina-sur", "Ensalada Test")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponse response = catalogoService.createOrUpdateProduct("cocina-sur", validRequest);

        assertNotNull(response);
        assertEquals("Ensalada Test", response.name());
        assertEquals("VEGANO", response.dietaryCategory());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createOrUpdateProduct_whenExistingProduct_updatesProduct() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Ensalada Test")
                .description("Descripción vieja")
                .dietaryCategory("CLASICA")
                .price(new BigDecimal("7500.00"))
                .catalogo(testCatalogo)
                .createdAt(LocalDateTime.now())
                .build();

        when(catalogoRepository.findByCocinaId("cocina-sur")).thenReturn(Optional.of(testCatalogo));
        when(productRepository.findByCocinaIdAndName("cocina-sur", "Ensalada Test")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponse response = catalogoService.createOrUpdateProduct("cocina-sur", validRequest);

        assertNotNull(response);
        assertEquals("VEGANO", existingProduct.getDietaryCategory());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void createOrUpdateProduct_whenNewKitchen_createsCatalogo() {
        when(catalogoRepository.findByCocinaId("cocina-nueva")).thenReturn(Optional.empty());
        when(catalogoRepository.save(any(Catalogo.class))).thenReturn(testCatalogo);
        when(productRepository.findByCocinaIdAndName("cocina-nueva", "Ensalada Test")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResponse response = catalogoService.createOrUpdateProduct("cocina-nueva", validRequest);

        assertNotNull(response);
        verify(catalogoRepository).save(any(Catalogo.class));
    }

    @Test
    void getProductsByCocina_whenKitchenHasProducts_returnsList() {
        when(productRepository.findByCocinaId("cocina-sur")).thenReturn(List.of(testProduct));

        List<ProductResponse> responses = catalogoService.getProductsByCocina("cocina-sur");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Ensalada Test", responses.getFirst().name());
        verify(productRepository).findByCocinaId("cocina-sur");
    }

    @Test
    void getProductsByCocina_whenNoProducts_returnsEmptyList() {
        when(productRepository.findByCocinaId("cocina-vacia")).thenReturn(List.of());

        List<ProductResponse> responses = catalogoService.getProductsByCocina("cocina-vacia");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(productRepository).findByCocinaId("cocina-vacia");
    }

    @Test
    void createOrUpdateProduct_whenMissingName_throwsException() {
        ProductRequest invalidRequest = new ProductRequest(
                null, "test", "VEGANO", new BigDecimal("8500.00"), null, null, null
        );

        assertThrows(InvalidProductDataException.class,
                () -> catalogoService.createOrUpdateProduct("cocina-sur", invalidRequest));
    }

    @Test
    void createOrUpdateProduct_whenMissingCategory_throwsException() {
        ProductRequest invalidRequest = new ProductRequest(
                "Test", "test", null, new BigDecimal("8500.00"), null, null, null
        );

        assertThrows(InvalidProductDataException.class,
                () -> catalogoService.createOrUpdateProduct("cocina-sur", invalidRequest));
    }

    @Test
    void createOrUpdateProduct_whenZeroPrice_throwsException() {
        ProductRequest invalidRequest = new ProductRequest(
                "Test", "test", "VEGANO", BigDecimal.ZERO, null, null, null
        );

        assertThrows(InvalidProductDataException.class,
                () -> catalogoService.createOrUpdateProduct("cocina-sur", invalidRequest));
    }

    @Test
    void createOrUpdateProduct_whenNegativePrice_throwsException() {
        ProductRequest invalidRequest = new ProductRequest(
                "Test", "test", "VEGANO", new BigDecimal("-100"), null, null, null
        );

        assertThrows(InvalidProductDataException.class,
                () -> catalogoService.createOrUpdateProduct("cocina-sur", invalidRequest));
    }
}
