package com.fernando.proyecto1.service;

import com.fernando.proyecto1.dto.CategoryRequest;
import com.fernando.proyecto1.dto.CategoryResponse;
import com.fernando.proyecto1.entity.CategoryEntity;
import com.fernando.proyecto1.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Obtener todas las categorías
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Crear una categoría
    public CategoryResponse createCategory(CategoryRequest request) {

        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .build();

        CategoryEntity saved = categoryRepository.save(category);

        return mapToResponse(saved);
    }

    // Convertir entidad a DTO de respuesta
    private CategoryResponse mapToResponse(CategoryEntity category) {

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}