package com.example.storeapi.dto.order;

import com.example.storeapi.domain.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateDTO {

    @NotBlank(message = "Status é obrigatório")
    @Pattern(regexp = "PENDING|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Status deve ser: PENDING, PROCESSING, SHIPPED, DELIVERED ou CANCELLED")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderStatus getStatusAsEnum() {
        return OrderStatus.valueOf(status.toUpperCase());
    }
}