package com.example.storeapi.dto.category;

import java.time.OffsetDateTime;

public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;

    public CategoryResponseDTO() {}
    public CategoryResponseDTO(Long id, String name, String description, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
