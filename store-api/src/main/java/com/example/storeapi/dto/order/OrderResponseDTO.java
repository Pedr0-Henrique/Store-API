package com.example.storeapi.dto.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal total;
    private String status;
    private OffsetDateTime createdAt;
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Long id, Long customerId, String customerName, BigDecimal total, String status, OffsetDateTime createdAt, List<OrderItemResponseDTO> items) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemResponseDTO> getItems() { return items; }
    public void setItems(List<OrderItemResponseDTO> items) { this.items = items; }
}
