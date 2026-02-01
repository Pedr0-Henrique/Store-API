package com.example.storeapi.service;

import com.example.storeapi.domain.Category;
import com.example.storeapi.dto.category.CategoryCreateDTO;
import com.example.storeapi.dto.category.CategoryPatchDTO;
import com.example.storeapi.dto.category.CategoryResponseDTO;
import com.example.storeapi.dto.category.CategoryUpdateDTO;
import com.example.storeapi.repository.CategoryRepository;
import com.example.storeapi.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CategoryResponseDTO create(CategoryCreateDTO dto) {
        categoryRepository.findByNameIgnoreCase(dto.getName()).ifPresent(c -> {
            throw new IllegalArgumentException("Category name already exists");
        });
        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        Category saved = categoryRepository.save(c);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> list(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO get(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));
        return toResponse(c);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryUpdateDTO dto) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));
        categoryRepository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Category name already exists");
            }
        });
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return toResponse(categoryRepository.save(c));
    }

    @Transactional
    public CategoryResponseDTO patch(Long id, CategoryPatchDTO dto) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new IllegalArgumentException("Category name must not be blank");
            }
            categoryRepository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Category name already exists");
                }
            });
            c.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            c.setDescription(dto.getDescription());
        }

        return toResponse(categoryRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Category not found");
        }

        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Category has products and cannot be deleted");
        }

        categoryRepository.deleteById(id);
    }

    private CategoryResponseDTO toResponse(Category c) {
        return new CategoryResponseDTO(c.getId(), c.getName(), c.getDescription(), c.getCreatedAt());
    }
}
