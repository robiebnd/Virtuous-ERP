package com.digipals.wms.documentflow.controller;

import com.digipals.wms.documentflow.dto.DocumentFlowResponse;
import com.digipals.wms.documentflow.service.DocumentFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/document-flow")
@RequiredArgsConstructor
public class DocumentFlowController {

    private final DocumentFlowService documentFlowService;

    @GetMapping("/sales-orders/{salesOrderId}")
    public ResponseEntity<DocumentFlowResponse> bySalesOrder(@PathVariable UUID salesOrderId) {
        return ResponseEntity.ok(documentFlowService.getBySalesOrderId(salesOrderId));
    }

    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<DocumentFlowResponse> byDelivery(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(documentFlowService.getByDeliveryId(deliveryId));
    }

    @GetMapping("/billing-documents/{billingDocumentId}")
    public ResponseEntity<DocumentFlowResponse> byBillingDocument(@PathVariable UUID billingDocumentId) {
        return ResponseEntity.ok(documentFlowService.getByBillingDocumentId(billingDocumentId));
    }
}
