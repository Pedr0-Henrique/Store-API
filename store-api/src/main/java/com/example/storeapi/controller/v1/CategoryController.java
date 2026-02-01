package com.example.storeapi.controller.v1;

import com.example.storeapi.dto.category.CategoryCreateDTO;
import com.example.storeapi.dto.category.CategoryPatchDTO;
import com.example.storeapi.dto.category.CategoryResponseDTO;
import com.example.storeapi.dto.category.CategoryUpdateDTO;
import com.example.storeapi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CategoryCreateDTO dto,
                                                      UriComponentsBuilder uriBuilder) {
        CategoryResponseDTO created = categoryService.create(dto);
        return ResponseEntity.created(
                uriBuilder.path("/api/v1/categories/{id}").build(created.getId())
        ).body(created);
    }

    @GetMapping
    public Page<CategoryResponseDTO> list(Pageable pageable) {
        return categoryService.list(pageable);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO get(@PathVariable("id") Long id) {
        return categoryService.get(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO update(@PathVariable("id") Long id, @RequestBody @Valid CategoryUpdateDTO dto) {
        return categoryService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public CategoryResponseDTO patch(@PathVariable("id") Long id, @RequestBody @Valid CategoryPatchDTO dto) {
        return categoryService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }
}
