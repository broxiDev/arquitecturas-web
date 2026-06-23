package com.farmacyfood.product.controller;

import com.farmacyfood.product.dto.ProductRequest;
import com.farmacyfood.product.dto.ProductResponse;
import com.farmacyfood.product.service.CatalogoService;
import com.farmacyfood.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para la gestión del catálogo de productos")
public class ProductController {

    private final ProductService productService;
    private final CatalogoService catalogoService;

    @Operation(
        summary = "Obtener todos los productos",
        description = "Retorna una lista de todos los productos, opcionalmente filtrados por categoría dietaria."
    )
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @Parameter(description = "Filtrar por categoría dietaria (ej: VEGANO, SIN_GLUTEN, VEGETARIANO, CLASICA)")
            @RequestParam(required = false) String category) {
        log.info("REST request to get all products. Category: {}", category);
        return ResponseEntity.ok(productService.getAllProducts(category));
    }

    @Operation(
        summary = "Obtener un producto por ID",
        description = "Retorna un producto específico por su ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id) {
        log.info("REST request to get product by ID: {}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
        summary = "Crear un nuevo producto",
        description = "Crea un nuevo producto sin vincular a un catálogo de cocina específico.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos", content = @Content)
        }
    )
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto a crear", required = true,
                content = @Content(schema = @Schema(implementation = ProductRequest.class)))
            @RequestBody ProductRequest productRequest) {
        log.info("REST request to create product: {}", productRequest.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }

    @Operation(
        summary = "Registrar producto en catálogo de cocina",
        description = "Registra un producto en el catálogo de una cocina fantasma. Si ya existe un producto con el mismo nombre en el catálogo de esa cocina, se actualiza (upsert).",
        responses = {
            @ApiResponse(responseCode = "201", description = "Producto creado en el catálogo"),
            @ApiResponse(responseCode = "200", description = "Producto actualizado en el catálogo"),
            @ApiResponse(responseCode = "400", description = "Datos de producto inválidos", content = @Content)
        }
    )
    @PostMapping("/cocina/{cocinaId}")
    public ResponseEntity<ProductResponse> createOrUpdateProductInCatalog(
            @Parameter(description = "ID de la cocina fantasma", required = true, example = "COCINA-DULCE")
            @PathVariable String cocinaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto a registrar", required = true,
                content = @Content(schema = @Schema(implementation = ProductRequest.class)))
            @RequestBody ProductRequest productRequest) {
        log.info("REST request to create/update product '{}' in kitchen catalog '{}'", productRequest.name(), cocinaId);
        ProductResponse response = catalogoService.createOrUpdateProduct(cocinaId, productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Obtener productos por cocina",
        description = "Retorna la lista de productos pertenecientes a una cocina mediante su ID."
    )
    @GetMapping("/cocina/{cocinaId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCocina(
            @Parameter(description = "ID de la cocina", required = true, example = "COCINA-DULCE")
            @PathVariable String cocinaId) {
        log.info("REST request to get products by cocina ID: {}", cocinaId);
        return ResponseEntity.ok(catalogoService.getProductsByCocina(cocinaId));
    }

    @Operation(
        summary = "Actualizar un producto existente",
        description = "Actualiza un producto existente por su ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del producto", required = true,
                content = @Content(schema = @Schema(implementation = ProductRequest.class)))
            @RequestBody ProductRequest productRequest) {
        log.info("REST request to update product with ID: {}", id);
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    @Operation(
        summary = "Eliminar un producto",
        description = "Elimina un producto por su ID.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obtener solo el nombre de un producto",
        description = "Endpoint utilizado internamente por otros microservicios.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Nombre del producto"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
        }
    )
    @GetMapping("/{id}/nombre")
    public ResponseEntity<String> getProductNameById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id) {
        log.info("REST request to get product name for ID: {}", id);
        return ResponseEntity.ok(productService.getProductNameById(id));
    }
}
