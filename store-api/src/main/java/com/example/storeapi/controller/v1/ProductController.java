package com.example.storeapi.controller.v1;

import com.example.storeapi.dto.product.ProductCreateDTO;
import com.example.storeapi.dto.product.ProductPatchDTO;
import com.example.storeapi.dto.product.ProductResponseDTO;
import com.example.storeapi.dto.product.ProductUpdateDTO;
import com.example.storeapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductCreateDTO dto,
                                                     UriComponentsBuilder uriBuilder) {
        ProductResponseDTO created = productService.create(dto);
        return ResponseEntity.created(
                uriBuilder.path("/api/v1/products/{id}").build(created.getId())
        ).body(created);
    }

    @GetMapping
    public Page<ProductResponseDTO> list(Pageable pageable) {
        return productService.list(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable("id") Long id) {
        return productService.get(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable("id") Long id, @RequestBody @Valid ProductUpdateDTO dto) {
        return productService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO patch(@PathVariable("id") Long id, @RequestBody @Valid ProductPatchDTO dto) {
        return productService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }
}
