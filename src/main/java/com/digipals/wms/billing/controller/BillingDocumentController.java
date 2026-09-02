package com.digipals.wms.billing.controller;

import com.digipals.wms.billing.dto.BillingDocumentItemResponse;
import com.digipals.wms.billing.dto.BillingDocumentResponse;
import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingDocumentItem;
import com.digipals.wms.billing.service.BillingDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing-documents")
@RequiredArgsConstructor
public class BillingDocumentController {

    private final BillingDocumentService billingDocumentService;

    @PostMapping
    public BillingDocumentResponse create(@Valid @RequestBody CreateBillingRequest request) {
        return toResponse(billingDocumentService.create(request));
    }

    @PostMapping("/{id}/post")
    public BillingDocumentResponse post(@PathVariable UUID id) {
        return toResponse(billingDocumentService.post(id));
    }

    @GetMapping
    public List<BillingDocumentResponse> findAll() {
        return billingDocumentService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BillingDocumentResponse findById(@PathVariable UUID id) {
        return toResponse(billingDocumentService.findById(id));
    }

    @GetMapping("/number/{billingNumber}")
    public BillingDocumentResponse findByBillingNumber(@PathVariable String billingNumber) {
        return toResponse(billingDocumentService.findByBillingNumber(billingNumber));
    }

    @GetMapping("/customer/{customerCode}")
    public List<BillingDocumentResponse> findByCustomer(@PathVariable String customerCode) {
        return billingDocumentService.findByCustomerCode(customerCode).stream()
                .map(this::toResponse)
                .toList();
    }

    private BillingDocumentResponse toResponse(BillingDocument billing) {
        List<BillingDocumentItemResponse> items = billing.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new BillingDocumentResponse(
                billing.getId(),
                billing.getBillingNumber(),
                billing.getOutboundDelivery().getId(),
                billing.getCustomerCode(),
                billing.getBillingType(),
                billing.getCurrency(),
                billing.getStatus(),
                billing.getBillingDate(),
                billing.getTotalAmount(),
                billing.getRemarks(),
                items);
    }

    private BillingDocumentItemResponse toItemResponse(BillingDocumentItem item) {
        return new BillingDocumentItemResponse(
                item.getId(),
                item.getItemNumber(),
                item.getMaterialCode(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getNetValue());
    }
}
