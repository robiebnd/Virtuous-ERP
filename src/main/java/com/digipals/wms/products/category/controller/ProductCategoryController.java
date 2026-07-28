package com.digipals.wms.products.category.controller;

import com.digipals.wms.products.category.dto.CreateProductCategoryRequest;
import com.digipals.wms.products.category.dto.ProductCategoryResponse;
import com.digipals.wms.products.category.dto.UpdateProductCategoryRequest;
import com.digipals.wms.products.category.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService service;

    @PostMapping
    public ProductCategoryResponse create(
            @Valid
            @RequestBody
            CreateProductCategoryRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public ProductCategoryResponse update(
            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateProductCategoryRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    public List<ProductCategoryResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductCategoryResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/code/{code}")
    public ProductCategoryResponse findByCode(
            @PathVariable String code) {

        return service.findByCode(code);
    }

    @GetMapping("/active")
    public List<ProductCategoryResponse> findActive() {

        return service.findActive();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}