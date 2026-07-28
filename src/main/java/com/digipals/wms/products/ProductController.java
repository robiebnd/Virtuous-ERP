package com.digipals.wms.products;

import com.digipals.wms.products.dto.CreateProductRequest;
import com.digipals.wms.products.dto.ProductResponse;
import com.digipals.wms.products.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ProductResponse create(
            @Valid
            @RequestBody
            CreateProductRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateProductRequest request) {

        return service.update(
                id,
                request);
    }

    @GetMapping
    public List<ProductResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/sku/{sku}")
    public ProductResponse findBySku(
            @PathVariable String sku) {

        return service.findBySku(sku);
    }

    @GetMapping("/active")
    public List<ProductResponse> findActive() {

        return service.findActive();
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> findByCategory(
            @PathVariable UUID categoryId) {

        return service.findByCategory(categoryId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}