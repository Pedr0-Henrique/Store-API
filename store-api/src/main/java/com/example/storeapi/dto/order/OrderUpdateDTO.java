package com.example.storeapi.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderUpdateDTO {
    @NotNull
    private Long customerId;

    @NotEmpty
    private List<OrderItemCreateDTO> items;

    // CREATED, PAID, CANCELED (opcional)
    private String status;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<OrderItemCreateDTO> getItems() { return items; }
    public void setItems(List<OrderItemCreateDTO> items) { this.items = items; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
