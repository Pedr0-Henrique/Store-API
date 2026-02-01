package com.example.storeapi.controller.v1;

import com.example.storeapi.dto.customer.CustomerCreateDTO;
import com.example.storeapi.dto.customer.CustomerOrderCountDTO;
import com.example.storeapi.dto.customer.CustomerPatchDTO;
import com.example.storeapi.dto.customer.CustomerResponseDTO;
import com.example.storeapi.dto.customer.CustomerUpdateDTO;
import com.example.storeapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerCreateDTO dto,
                                                      UriComponentsBuilder uriBuilder) {
        CustomerResponseDTO created = customerService.create(dto);
        return ResponseEntity.created(
                uriBuilder.path("/api/v1/customers/{id}").build(created.getId())
        ).body(created);
    }

    @GetMapping
    public Page<CustomerResponseDTO> list(Pageable pageable) {
        return customerService.list(pageable);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO get(@PathVariable("id") Long id) {
        return customerService.get(id);
    }

    @GetMapping("/{id}/orders/count")
    public ResponseEntity<CustomerOrderCountDTO> orderCounts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.orderCounts(id));
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO update(@PathVariable("id") Long id, @RequestBody @Valid CustomerUpdateDTO dto) {
        return customerService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public CustomerResponseDTO patch(@PathVariable("id") Long id, @RequestBody @Valid CustomerPatchDTO dto) {
        return customerService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        customerService.delete(id);
    }
}
