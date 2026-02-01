package com.example.storeapi.service;

import com.example.storeapi.domain.Category;
import com.example.storeapi.domain.Product;
import com.example.storeapi.domain.ProductStatus;
import com.example.storeapi.dto.product.ProductCreateDTO;
import com.example.storeapi.dto.product.ProductPatchDTO;
import com.example.storeapi.dto.product.ProductResponseDTO;
import com.example.storeapi.dto.product.ProductUpdateDTO;
import com.example.storeapi.repository.CategoryRepository;
import com.example.storeapi.repository.OrderItemRepository;
import com.example.storeapi.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public ProductResponseDTO create(ProductCreateDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setCategory(category);
        if (dto.getStatus() != null) {
            p.setStatus(ProductStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        return toResponse(productRepository.save(p));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO get(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));
        return toResponse(p);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductUpdateDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setCategory(category);
        if (dto.getStatus() != null) {
            p.setStatus(ProductStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public ProductResponseDTO patch(Long id, ProductPatchDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new IllegalArgumentException("Product name must not be blank");
            }
            p.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            p.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }
            p.setPrice(dto.getPrice());
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Category not found"));
            p.setCategory(category);
        }

        if (dto.getStatus() != null) {
            if (dto.getStatus().isBlank()) {
                throw new IllegalArgumentException("Invalid product status: " + dto.getStatus());
            }
            try {
                p.setStatus(ProductStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid product status: " + dto.getStatus());
            }
        }

        return toResponse(productRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Product not found");
        }

        if (orderItemRepository.existsByProductId(id)) {
            throw new IllegalStateException("Product is used in orders and cannot be deleted");
        }

        productRepository.deleteById(id);
    }

    private ProductResponseDTO toResponse(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStatus().name(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getCreatedAt()
        );
    }
}
