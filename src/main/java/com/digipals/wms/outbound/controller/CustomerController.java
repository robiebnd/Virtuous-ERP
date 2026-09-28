package com.digipals.wms.outbound.controller;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.outbound.entity.Customer;
import com.digipals.wms.outbound.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRepository repository;

    @GetMapping
    public List<Customer> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        if (customer.getCustomerNumber() == null || customer.getCustomerNumber().isBlank()) {
            customer.setCustomerNumber("CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (repository.existsByCustomerNumber(customer.getCustomerNumber())) {
            throw new IllegalArgumentException("Customer number already exists.");
        }
        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (customer.getCreditBlocked() == null) customer.setCreditBlocked(false);
        return repository.save(customer);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable UUID id, @RequestBody Customer request) {
        Customer customer = get(id);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setBillingAddress(request.getBillingAddress());
        customer.setShippingAddress(request.getShippingAddress());
        customer.setPaymentTerms(request.getPaymentTerms());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setCreditBlocked(request.getCreditBlocked() == null ? false : request.getCreditBlocked());
        customer.setActive(request.getActive() == null ? customer.getActive() : request.getActive());
        return repository.save(customer);
    }
}