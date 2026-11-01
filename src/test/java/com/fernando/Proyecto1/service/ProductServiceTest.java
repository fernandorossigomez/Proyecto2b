package com.fernando.Proyecto1.service;

import com.fernando.proyecto1.repository.CategoryRepository;
import com.fernando.proyecto1.repository.ProductRepository;
import com.fernando.proyecto1.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fernando.proyecto1.dto.ProductResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import com.fernando.proyecto1.entity.ProductEntity;
import com.fernando.proyecto1.entity.CategoryEntity;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fernando.proyecto1.dto.ProductRequest;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import com.fernando.proyecto1.exceptions.CategoryNotFoundException;
import com.fernando.proyecto1.exceptions.ProductNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProductsShouldReturnEmptyList() {
        // Arrange: simulamos que no hay productos en el repositorio
        when(productRepository.findAll()).thenReturn(List.of());

        // Act: llamamos al servicio
        List<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, null, null
        );

        // Assert: comprobamos que la lista está vacía
        assertTrue(result.isEmpty());
    }
    @Test
    void getAllProductsShouldReturnProductResponse() {
        // Arrange
        CategoryEntity category = CategoryEntity.builder()
                .id(1L)
                .name("FOOD")
                .build();

        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .category(category)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product));

        // Act
        List<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, null, null
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals(new BigDecimal("8.50"), result.get(0).getPrice());
        assertTrue(result.get(0).getActive());
        assertEquals(1L, result.get(0).getCategoryId());
        assertEquals("FOOD", result.get(0).getCategoryName());
    }

    @Test
    void getAllProductsShouldFilterByPriceRange() {
        // Arrange
        ProductEntity cheapProduct = ProductEntity.builder()
                .id(1L)
                .name("Agua")
                .price(new BigDecimal("1.50"))
                .active(true)
                .build();

        ProductEntity mediumProduct = ProductEntity.builder()
                .id(2L)
                .name("Hamburguesa")
                .price(new BigDecimal("6.50"))
                .active(true)
                .build();

        ProductEntity expensiveProduct = ProductEntity.builder()
                .id(3L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        when(productRepository.findAll()).thenReturn(
                List.of(cheapProduct, mediumProduct, expensiveProduct)
        );

        // Act: buscamos productos entre 5 y 8 euros
        List<ProductResponse> result = productService.getAllProducts(
                new BigDecimal("5.00"),
                new BigDecimal("8.00"),
                null, null, null, null, null
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals("Hamburguesa", result.get(0).getName());
    }

    @Test
    void getAllProductsShouldFilterByNameIgnoringCase() {
        // Arrange
        ProductEntity pizza = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        ProductEntity burger = ProductEntity.builder()
                .id(2L)
                .name("Hamburguesa")
                .price(new BigDecimal("6.50"))
                .active(true)
                .build();

        when(productRepository.findAll()).thenReturn(
                List.of(pizza, burger)
        );

        // Act: buscamos "PIZ" en mayúsculas
        List<ProductResponse> result = productService.getAllProducts(
                null, null, "PIZ", null, null, null, null
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
    }

    @Test
    void getAllProductsShouldFilterByCategory() {
        // Arrange
        CategoryEntity food = CategoryEntity.builder()
                .id(1L)
                .name("FOOD")
                .build();

        CategoryEntity drinks = CategoryEntity.builder()
                .id(2L)
                .name("DRINK")
                .build();

        ProductEntity pizza = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .category(food)
                .build();

        ProductEntity water = ProductEntity.builder()
                .id(2L)
                .name("Agua")
                .price(new BigDecimal("1.50"))
                .active(true)
                .category(drinks)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(pizza, water));

        // Act: buscamos productos de la categoría DRINK
        List<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, 2L, null, null
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals("Agua", result.get(0).getName());
        assertEquals(2L, result.get(0).getCategoryId());
    }

    @Test
    void getAllProductsShouldFilterByActiveStatus() {
        // Arrange
        ProductEntity activeProduct = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        ProductEntity inactiveProduct = ProductEntity.builder()
                .id(2L)
                .name("Agua")
                .price(new BigDecimal("1.50"))
                .active(false)
                .build();

        when(productRepository.findAll()).thenReturn(
                List.of(activeProduct, inactiveProduct)
        );

        // Act
        List<ProductResponse> result = productService.getAllProducts(
                null, null, null, true, null, null, null
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertTrue(result.get(0).getActive());
    }

    @Test
    void getAllProductsShouldSortByPriceDescending() {
        // Arrange
        ProductEntity water = ProductEntity.builder()
                .id(1L)
                .name("Agua")
                .price(new BigDecimal("1.50"))
                .active(true)
                .build();

        ProductEntity pizza = ProductEntity.builder()
                .id(2L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        ProductEntity burger = ProductEntity.builder()
                .id(3L)
                .name("Hamburguesa")
                .price(new BigDecimal("6.50"))
                .active(true)
                .build();

        when(productRepository.findAll()).thenReturn(
                List.of(water, pizza, burger)
        );

        // Act
        List<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, "price", "desc"
        );

        // Assert
        assertEquals(3, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals("Hamburguesa", result.get(1).getName());
        assertEquals("Agua", result.get(2).getName());
    }
    @Test
    void createProductShouldSaveAndReturnProduct() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza");
        request.setPrice(new BigDecimal("8.50"));
        request.setCategoryId(1L);

        CategoryEntity category = CategoryEntity.builder()
                .id(1L)
                .name("FOOD")
                .build();

        when(productRepository.existsByNameIgnoreCase("Pizza"))
                .thenReturn(false);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(org.mockito.ArgumentMatchers.any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity saved = invocation.getArgument(0);
                    saved.setId(10L);
                    return saved;
                });

        // Act
        ProductResponse result = productService.createProduct(request);

        // Assert
        assertEquals(10L, result.getId());
        assertEquals("Pizza", result.getName());
        assertEquals(new BigDecimal("8.50"), result.getPrice());
        assertTrue(result.getActive());
        assertEquals(1L, result.getCategoryId());
        assertEquals("FOOD", result.getCategoryName());

        verify(productRepository).save(org.mockito.ArgumentMatchers.any(ProductEntity.class));
    }

    @Test
    void createProductShouldThrowExceptionWhenProductAlreadyExists() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza");
        request.setPrice(new BigDecimal("8.50"));
        request.setCategoryId(1L);

        when(productRepository.existsByNameIgnoreCase("Pizza"))
                .thenReturn(true);

        // Act + Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> productService.createProduct(request)
        );

        assertEquals("Ya existe un producto con ese nombre", exception.getMessage());

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }
    @Test
    void createProductShouldThrowExceptionWhenCategoryDoesNotExist() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza");
        request.setPrice(new BigDecimal("8.50"));
        request.setCategoryId(99L);

        when(productRepository.existsByNameIgnoreCase("Pizza"))
                .thenReturn(false);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }
    @Test
    void updateProductShouldUpdateAndReturnProduct() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza Especial");
        request.setPrice(new BigDecimal("10.50"));
        request.setCategoryId(2L);

        CategoryEntity oldCategory = CategoryEntity.builder()
                .id(1L)
                .name("FOOD")
                .build();

        CategoryEntity newCategory = CategoryEntity.builder()
                .id(2L)
                .name("DRINK")
                .build();

        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .category(oldCategory)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameIgnoreCase("Pizza Especial"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(productRepository.save(org.mockito.ArgumentMatchers.any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductResponse result = productService.updateProduct(1L, request);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Pizza Especial", result.getName());
        assertEquals(new BigDecimal("10.50"), result.getPrice());
        assertTrue(result.getActive());
        assertEquals(2L, result.getCategoryId());
        assertEquals("DRINK", result.getCategoryName());

        verify(productRepository).save(org.mockito.ArgumentMatchers.any(ProductEntity.class));
    }
    @Test
    void updateProductShouldThrowExceptionWhenProductDoesNotExist() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza Especial");
        request.setPrice(new BigDecimal("10.50"));
        request.setCategoryId(2L);

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(99L, request)
        );

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }

    @Test
    void updateProductShouldThrowExceptionWhenNameAlreadyExists() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Hamburguesa");
        request.setPrice(new BigDecimal("7.50"));
        request.setCategoryId(1L);

        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        ProductEntity existingProduct = ProductEntity.builder()
                .id(2L)
                .name("Hamburguesa")
                .price(new BigDecimal("6.50"))
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameIgnoreCase("Hamburguesa"))
                .thenReturn(Optional.of(existingProduct));

        // Act + Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> productService.updateProduct(1L, request)
        );

        assertEquals("Ya existe otro producto con ese nombre", exception.getMessage());

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }

    @Test
    void updateProductShouldThrowExceptionWhenCategoryDoesNotExist() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Pizza Actualizada");
        request.setPrice(new BigDecimal("9.50"));
        request.setCategoryId(99L);

        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByNameIgnoreCase("Pizza Actualizada"))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> productService.updateProduct(1L, request)
        );

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }

    @Test
    void deactivateProductShouldSetActiveFalseAndReturnProduct() {
        // Arrange
        ProductEntity product = ProductEntity.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("8.50"))
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        // Act
        ProductResponse response = productService.deactivateProduct(1L);

        // Assert
        assertEquals(false, product.getActive());
        assertEquals(false, response.getActive());
        assertEquals(1L, response.getId());
        assertEquals("Pizza", response.getName());

        verify(productRepository).save(product);
    }

    @Test
    void deactivateProductShouldThrowExceptionWhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                ProductNotFoundException.class,
                () -> productService.deactivateProduct(99L)
        );

        verify(productRepository, never()).save(
                org.mockito.ArgumentMatchers.any(ProductEntity.class)
        );
    }
}