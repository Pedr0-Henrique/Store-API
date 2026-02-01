package com.example.storeapi.service;

import com.example.storeapi.domain.Customer;
import com.example.storeapi.domain.Order;
import com.example.storeapi.domain.OrderItem;
import com.example.storeapi.domain.OrderStatus;
import com.example.storeapi.domain.Product;
import com.example.storeapi.dto.order.OrderCreateDTO;
import com.example.storeapi.dto.order.OrderItemCreateDTO;
import com.example.storeapi.dto.order.OrderItemResponseDTO;
import com.example.storeapi.dto.order.OrderResponseDTO;
import com.example.storeapi.dto.order.OrderUpdateDTO;
import com.example.storeapi.repository.CustomerRepository;
import com.example.storeapi.repository.OrderRepository;
import com.example.storeapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    private static final Logger LOG = Logger.getLogger(OrderService.class.getName());

    @Transactional
    public OrderResponseDTO create(OrderCreateDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setCustomer(customer);
        if (dto.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemCreateDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than 0");
            }
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(product.getPrice());
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setSubtotal(subtotal);
            items.add(item);
            total = total.add(subtotal);
        }
        order.setItems(items);
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> list(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO get(Long id) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return toResponse(o);
    }

    @Transactional
    public OrderResponseDTO updateStatus(Long id, OrderStatus status) {
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Validação adicional de transição de status (opcional, mas recomendado)
        validateStatusTransition(o.getStatus(), status);

        o.setStatus(status);

        Order saved = orderRepository.save(o);

        // Adicionar logs para debug
        LOG.info("Order " + id + " status updated to " + status);

        return toResponse(saved);
    }

    @Transactional
    public OrderResponseDTO update(Long id, OrderUpdateDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            order.setCustomer(customer);
        }

        if (dto.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        if (dto.getItems() != null) {
            if (dto.getItems().isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one item");
            }
            BigDecimal total = BigDecimal.ZERO;

            // IMPORTANT: Do not replace the collection instance when using orphanRemoval=true.
            // Replace-by-set breaks Hibernate tracking and can trigger:
            // "all-delete-orphan was no longer referenced by the owning entity instance".
            order.getItems().clear();
            for (OrderItemCreateDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Item quantity must be greater than 0");
                }
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(product.getPrice());
                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);
                item.setSubtotal(subtotal);
                order.getItems().add(item);
                total = total.add(subtotal);
            }
            order.setTotal(total);
        }

        Order saved = orderRepository.save(order);
        LOG.info("Order " + id + " updated");
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    // Método auxiliar para validar transições (exemplo)
    private void validateStatusTransition(OrderStatus current, OrderStatus newStatus) {
        // Implementar lógica de validação conforme suas regras de negócio
        // Exemplo: alguns status não podem retroceder
        if (current == OrderStatus.DELIVERED && newStatus == OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot revert from DELIVERED to PENDING");
        }
    }

    private OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> items = new ArrayList<>();
        for (OrderItem i : order.getItems()) {
            items.add(new OrderItemResponseDTO(
                    i.getProduct().getId(),
                    i.getProduct().getName(),
                    i.getQuantity(),
                    i.getUnitPrice(),
                    i.getSubtotal()
            ));
        }
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }
}
