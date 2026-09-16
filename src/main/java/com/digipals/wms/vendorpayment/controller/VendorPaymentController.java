package com.digipals.wms.vendorpayment.controller;

import com.digipals.wms.vendorpayment.dto.CreateVendorPaymentRequest;
import com.digipals.wms.vendorpayment.dto.VendorPaymentResponse;
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
    public ResponseEntity<VendorPaymentResponse> create(@Valid @RequestBody CreateVendorPaymentRequest request) {
        return ResponseEntity.ok(toResponse(service.create(request)));
    }

    @GetMapping
    public ResponseEntity<List<VendorPaymentResponse>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<VendorPaymentResponse> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(service.approve(id)));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<VendorPaymentResponse> pay(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(service.pay(id)));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<VendorPaymentResponse>> findByInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(service.findByInvoice(invoiceId).stream().map(this::toResponse).toList());
    }

    private VendorPaymentResponse toResponse(VendorPayment payment) {
        return VendorPaymentResponse.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .vendorInvoiceId(payment.getVendorInvoice().getId())
                .invoiceNumber(payment.getVendorInvoice().getInvoiceNumber())
                .supplierId(payment.getSupplier().getId())
                .supplierCode(payment.getSupplier().getCode())
                .supplierName(payment.getSupplier().getName())
                .paymentDate(payment.getPaymentDate())
                .currency(payment.getCurrency())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .referenceNumber(payment.getReferenceNumber())
                .status(payment.getStatus())
                .remarks(payment.getRemarks())
                .build();
    }
}
