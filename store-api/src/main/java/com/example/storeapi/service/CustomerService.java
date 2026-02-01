package com.example.storeapi.service;

import com.example.storeapi.domain.Customer;
import com.example.storeapi.domain.OrderStatus;
import com.example.storeapi.dto.customer.CustomerCreateDTO;
import com.example.storeapi.dto.customer.CustomerOrderCountDTO;
import com.example.storeapi.dto.customer.CustomerPatchDTO;
import com.example.storeapi.dto.customer.CustomerResponseDTO;
import com.example.storeapi.dto.customer.CustomerUpdateDTO;
import com.example.storeapi.repository.CustomerRepository;
import com.example.storeapi.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CustomerResponseDTO create(CustomerCreateDTO dto) {
        customerRepository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(c -> {
            throw new IllegalArgumentException("Email already in use");
        });
        Customer c = new Customer();
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        return toResponse(customerRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> list(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO get(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
        return toResponse(c);
    }

    @Transactional(readOnly = true)
    public CustomerOrderCountDTO orderCounts(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new jakarta.persistence.EntityNotFoundException("Customer not found");
        }

        long total = orderRepository.countByCustomerId(customerId);
        long open = orderRepository.countOpenByCustomerId(customerId, java.util.List.of(OrderStatus.DELIVERED, OrderStatus.CANCELED));
        return new CustomerOrderCountDTO(total, open);
    }

    @Transactional
    public CustomerResponseDTO update(Long id, CustomerUpdateDTO dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
        customerRepository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Email already in use");
            }
        });
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        return toResponse(customerRepository.save(c));
    }

    @Transactional
    public CustomerResponseDTO patch(Long id, CustomerPatchDTO dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new IllegalArgumentException("Customer name must not be blank");
            }
            c.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            if (dto.getEmail().isBlank()) {
                throw new IllegalArgumentException("Customer email must not be blank");
            }
            customerRepository.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already in use");
                }
            });
            c.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            c.setPhone(dto.getPhone());
        }

        return toResponse(customerRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Customer not found");
        }

        if (orderRepository.existsByCustomerId(id)) {
            throw new IllegalStateException("Customer has orders and cannot be deleted");
        }

        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO toResponse(Customer c) {
        return new CustomerResponseDTO(c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getCreatedAt());
    }
}
