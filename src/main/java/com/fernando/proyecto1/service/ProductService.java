package com.fernando.proyecto1.service;

import com.fernando.proyecto1.dto.ProductRequest;
import com.fernando.proyecto1.dto.ProductResponse;
import com.fernando.proyecto1.entity.CategoryEntity;
import com.fernando.proyecto1.entity.ProductEntity;
import com.fernando.proyecto1.repository.CategoryRepository;
import com.fernando.proyecto1.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.fernando.proyecto1.exceptions.CategoryNotFoundException;
import com.fernando.proyecto1.exceptions.ProductNotFoundException;
import java.math.BigDecimal;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Obtener productos aplicando filtros
    public List<ProductResponse> getAllProducts(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String name,
            Boolean active,
            Long categoryId,
            String sortBy,
            String sortDirection
    ) {

        var products = productRepository.findAll().stream().filter(p -> minPrice == null ||
                        p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null ||
                        p.getPrice().compareTo(maxPrice) <= 0)
                .filter(p -> name == null ||
                        p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> active == null || active.equals(p.getActive()))
                .filter(p -> categoryId == null ||
                        (p.getCategory() != null &&
                                p.getCategory().getId().equals(categoryId)))
                .toList();

        // Ordenación
        if (sortBy != null) {

            java.util.Comparator<ProductEntity> comparator = switch (sortBy.toLowerCase()) {

                case "name" -> java.util.Comparator.comparing(
                        ProductEntity::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

                case "price" -> java.util.Comparator.comparing(
                        ProductEntity::getPrice
                );

                case "category" -> java.util.Comparator.comparing(
                        (ProductEntity p) -> p.getCategory() != null
                                ? p.getCategory().getName()
                                : ""
                );

                default -> throw new IllegalArgumentException(
                        "Criterio de ordenación no válido. Usa name, price o category"
                );
            };

            if ("desc".equalsIgnoreCase(sortDirection)) {
                comparator = comparator.reversed();
            } else if (sortDirection != null &&
                    !"asc".equalsIgnoreCase(sortDirection)) {
                throw new IllegalArgumentException(
                        "Dirección de ordenación no válida. Usa ASC o DESC"
                );
            }

            products = products.stream()
                    .sorted(comparator)
                    .toList();
        }

        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Crear producto
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("No existe la categoría indicada"));

        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .price(request.getPrice())
                .active(request.getActive() != null ? request.getActive() : true)
                .category(category)
                .build();

        ProductEntity saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    // Actualizar producto
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        // Comprobar que el producto existe
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("No existe el producto indicado"));

        // Comprobar que el nombre no pertenece a otro producto
        productRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Ya existe otro producto con ese nombre");
                });

        // Comprobar que la categoría existe
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("No existe la categoría indicada"));

        // Actualizar los campos
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setActive(request.getActive() != null ? request.getActive() : true);
        product.setCategory(category);

        // Guardar cambios
        ProductEntity updated = productRepository.save(product);

        return mapToResponse(updated);
    }

    // Desactivar producto
    public ProductResponse deactivateProduct(Long id) {

        // Comprobar que el producto existe
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("No existe el producto indicado"));

        // Cambiar su estado a inactivo
        product.setActive(false);

        // Guardar cambios
        ProductEntity updated = productRepository.save(product);

        return mapToResponse(updated);
    }

    // Convertir entidad a DTO de respuesta
    private ProductResponse mapToResponse(ProductEntity product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getActive(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}