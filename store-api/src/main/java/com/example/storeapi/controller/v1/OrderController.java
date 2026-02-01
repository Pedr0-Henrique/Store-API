package com.example.storeapi.controller.v1;

import com.example.storeapi.domain.OrderStatus;
import com.example.storeapi.dto.order.OrderCreateDTO;
import com.example.storeapi.dto.order.OrderResponseDTO;
import com.example.storeapi.dto.order.OrderStatusUpdateDTO;
import com.example.storeapi.dto.order.OrderUpdateDTO;
import com.example.storeapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody @Valid OrderCreateDTO dto,
                                                   UriComponentsBuilder uriBuilder) {
        OrderResponseDTO created = orderService.create(dto);
        URI location = uriBuilder.path("/api/v1/orders/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> list(Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.list(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> get(@PathVariable("id") Long id) {
        OrderResponseDTO order = orderService.get(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid OrderStatusUpdateDTO dto) {

        try {
            OrderStatus status = parseStatus(dto.getStatus());
            OrderResponseDTO updatedOrder = orderService.updateStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido: " + dto.getStatus() + ". Status válidos: " + Arrays.toString(OrderStatus.values())
            );
        }
    }

    private OrderStatus parseStatus(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Status is required");
        }
        String s = raw.trim().toUpperCase(Locale.ROOT);
        if (s.equals("PAGO")) s = "PAID";
        if (s.equals("CANCELADO")) s = "CANCELED";
        if (s.equals("CRIADO")) s = "CREATED";
        if (s.equals("PENDENTE")) s = "PENDING";
        if (s.equals("ENTREGUE")) s = "DELIVERED";
        return OrderStatus.valueOf(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid OrderUpdateDTO dto) {
        OrderResponseDTO updatedOrder = orderService.update(id, dto);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {  // Adicionei ("id") aqui
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}