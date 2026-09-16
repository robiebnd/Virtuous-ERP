package com.digipals.wms.vendorpayment.controller;

import com.digipals.wms.vendorpayment.dto.CreateVendorPaymentRequest;
import com.digipals.wms.vendorpayment.entity.VendorPayment;
import com.digipals.wms.vendorpayment.service.VendorPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/vendor-payments")
@RequiredArgsConstructor
public class VendorPaymentController {
    private final VendorPaymentService service;

    @PostMapping
    public ResponseEntity<VendorPayment> create(@Valid @RequestBody CreateVendorPaymentRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<VendorPayment>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<VendorPayment> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<VendorPayment> pay(@PathVariable UUID id) {
        return ResponseEntity.ok(service.pay(id));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<VendorPayment>> findByInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(service.findByInvoice(invoiceId));
    }
}
