package com.digipals.wms.payment.controller;

import com.digipals.wms.payment.dto.CashApplicationRequest;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.service.CashApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cash-applications")
@RequiredArgsConstructor
public class CashApplicationController {

    private final CashApplicationService cashApplicationService;

    @PostMapping
    public ResponseEntity<IncomingPayment> apply(@Valid @RequestBody CashApplicationRequest request) {
        return ResponseEntity.ok(cashApplicationService.apply(request));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<IncomingPayment> findPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(cashApplicationService.findPayment(paymentId));
    }

    @GetMapping("/customers/{customerCode}/payments")
    public ResponseEntity<List<IncomingPayment>> findCustomerPayments(@PathVariable String customerCode) {
        return ResponseEntity.ok(cashApplicationService.findCustomerPayments(customerCode));
    }
}
