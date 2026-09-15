package com.digipals.wms.vendorevaluation.controller;

import com.digipals.wms.vendorevaluation.dto.CreateVendorEvaluationRequest;
import com.digipals.wms.vendorevaluation.entity.VendorEvaluation;
import com.digipals.wms.vendorevaluation.service.VendorEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/vendor-evaluations")
@RequiredArgsConstructor
public class VendorEvaluationController {
    private final VendorEvaluationService service;

    @PostMapping
    public ResponseEntity<VendorEvaluation> create(@Valid @RequestBody CreateVendorEvaluationRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<VendorEvaluation>> findBySupplier(@PathVariable UUID supplierId) {
        return ResponseEntity.ok(service.findBySupplier(supplierId));
    }
}
