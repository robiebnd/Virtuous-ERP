package com.digipals.wms.payment.controller;

import com.digipals.wms.payment.dto.CreateIncomingPaymentRequest;
import com.digipals.wms.payment.dto.PaymentResponse;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.service.IncomingPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incoming-payments")
@RequiredArgsConstructor
public class IncomingPaymentController {

    private final IncomingPaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> receive(@Valid @RequestBody CreateIncomingPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(paymentService.receive(request)));
    }

    @PostMapping("/{id}/cancel")
    public PaymentResponse cancel(@PathVariable UUID id) {
        return toResponse(paymentService.cancel(id));
    }

    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PaymentResponse findById(@PathVariable UUID id) {
        return toResponse(paymentService.findById(id));
    }

    @GetMapping("/customer/{customerCode}")
    public List<PaymentResponse> findByCustomerCode(@PathVariable String customerCode) {
        return paymentService.findByCustomerCode(customerCode).stream().map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(IncomingPayment payment) {
        BigDecimal applied = payment.getStatus() == PaymentStatus.CANCELLED
                ? BigDecimal.ZERO
                : payment.getAllocations().stream()
                    .map(allocation -> allocation.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        UUID billingDocumentId = payment.getAllocations().stream()
                .findFirst()
                .map(allocation -> allocation.getBillingDocument().getId())
                .orElse(null);

        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentNumber(),
                payment.getCustomerCode(),
                payment.getAmount(),
                applied,
                payment.getAmount().subtract(applied),
                payment.getCurrency(),
                payment.getPaymentDate(),
                payment.getReference(),
                payment.getStatus(),
                billingDocumentId
        );
    }
}
