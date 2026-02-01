package com.example.storeapi.dto.customer;

import java.time.OffsetDateTime;

public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private OffsetDateTime createdAt;

    public CustomerResponseDTO() {}
    public CustomerResponseDTO(Long id, String name, String email, String phone, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
