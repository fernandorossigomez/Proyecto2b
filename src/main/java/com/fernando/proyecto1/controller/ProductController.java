package com.fernando.proyecto1.controller;

import com.fernando.proyecto1.dto.ProductRequest;
import com.fernando.proyecto1.dto.ProductResponse;
import com.fernando.proyecto1.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        return ResponseEntity.ok(
                productService.getAllProducts(
                        minPrice,
                        maxPrice,
                        name,
                        active,
                        categoryId,
                        sortBy,
                        sortDirection
                )
        );
    }

    // POST
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    // Desactivar producto
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivate(
            @PathVariable Long id
    ) {
        ProductResponse response = productService.deactivateProduct(id);
        return ResponseEntity.ok(response);
    }
}