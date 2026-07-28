package com.digipals.wms.supplier.controller;

import com.digipals.wms.supplier.dto.CreateSupplierRequest;
import com.digipals.wms.supplier.dto.SupplierResponse;
import com.digipals.wms.supplier.dto.UpdateSupplierRequest;
import com.digipals.wms.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService service;

    @PostMapping
    public SupplierResponse create(
            @Valid
            @RequestBody
            CreateSupplierRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public SupplierResponse update(
            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateSupplierRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    public List<SupplierResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/code/{code}")
    public SupplierResponse findByCode(
            @PathVariable String code) {

        return service.findByCode(code);
    }

    @GetMapping("/active")
    public List<SupplierResponse> findActive() {

        return service.findActive();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}