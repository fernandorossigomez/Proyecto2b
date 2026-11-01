
package com.fernando.Proyecto1.service;

import com.fernando.proyecto1.dto.CategoryRequest;
import com.fernando.proyecto1.dto.CategoryResponse;
import com.fernando.proyecto1.entity.CategoryEntity;
import com.fernando.proyecto1.repository.CategoryRepository;
import com.fernando.proyecto1.service.CategoryService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // --------------------------------------------------
    // GET ALL CATEGORIES
    // --------------------------------------------------

    @Test
    void getAllCategoriesShouldReturnAllCategories() {
        CategoryEntity food = CategoryEntity.builder()
                .id(1L)
                .name("Comida")
                .build();

        CategoryEntity drinks = CategoryEntity.builder()
                .id(2L)
                .name("Bebidas")
                .build();

        when(categoryRepository.findAll())
                .thenReturn(List.of(food, drinks));

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals("Comida", response.get(0).getName());

        assertEquals(2L, response.get(1).getId());
        assertEquals("Bebidas", response.get(1).getName());

        verify(categoryRepository).findAll();
    }

    @Test
    void getAllCategoriesShouldReturnEmptyListWhenNoCategoriesExist() {
        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(categoryRepository).findAll();
    }

    @Test
    void getAllCategoriesShouldMapAllCategoryFields() {
        CategoryEntity category = CategoryEntity.builder()
                .id(10L)
                .name("Postres")
                .build();

        when(categoryRepository.findAll())
                .thenReturn(List.of(category));

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getId());
        assertEquals("Postres", response.get(0).getName());
    }

    // --------------------------------------------------
    // CREATE CATEGORY
    // --------------------------------------------------

    @Test
    void createCategoryShouldSaveAndReturnCategory() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Bebidas");

        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> {
                    CategoryEntity category = invocation.getArgument(0);
                    category.setId(1L);
                    return category;
                });

        CategoryResponse response =
                categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Bebidas", response.getName());

        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void createCategoryShouldSaveCategoryWithRequestedName() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Comida");

        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.createCategory(request);

        verify(categoryRepository).save(argThat(category ->
                category.getName().equals("Comida")
        ));
    }
}
