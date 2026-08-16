package com.digipals.wms.purchaseorders.controller;

import com.digipals.wms.common.mapper.PurchaseOrderMapper;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.service.PurchaseOrderNumberService;
import com.digipals.wms.purchaseorders.service.PurchaseOrderPdfService;
import com.digipals.wms.purchaseorders.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;
    private final PurchaseOrderNumberService numberService;
    private final PurchaseOrderPdfService pdfService;

    @PostMapping("/from-requisition/{requisitionId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public PurchaseOrderResponse createFromRequisition(@PathVariable UUID requisitionId) {
        return PurchaseOrderMapper.toResponse(service.createFromRequisition(requisitionId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchaseOrderResponse> getAll() {
        return service.findAll().stream().map(PurchaseOrderMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchaseOrderResponse getById(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.findById(id));
    }

    @GetMapping("/number/{poNumber}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchaseOrderResponse getByNumber(@PathVariable String poNumber) {
        return PurchaseOrderMapper.toResponse(numberService.findByNumber(poNumber));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public ResponseEntity<byte[]> getPdfById(@PathVariable UUID id) {
        byte[] pdf = pdfService.generateById(id);
        return pdfResponse(pdf, "purchase-order-" + id + ".pdf");
    }

    @GetMapping("/number/{poNumber}/pdf")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public ResponseEntity<byte[]> getPdfByNumber(@PathVariable String poNumber) {
        byte[] pdf = pdfService.generateByNumber(poNumber);
        return pdfResponse(pdf, "purchase-order-" + poNumber + ".pdf");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_APPROVE')")
    public PurchaseOrderResponse approve(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.approve(id));
    }

    @PutMapping("/number/{poNumber}/approve")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_APPROVE')")
    public PurchaseOrderResponse approveByNumber(@PathVariable String poNumber) {
        return PurchaseOrderMapper.toResponse(numberService.approveByNumber(poNumber));
    }

    @PutMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_RECEIVE')")
    public PurchaseOrderResponse receive(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.receive(id));
    }

    @PutMapping("/number/{poNumber}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_RECEIVE')")
    public PurchaseOrderResponse receiveByNumber(@PathVariable String poNumber) {
        return PurchaseOrderMapper.toResponse(numberService.receiveByNumber(poNumber));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_UPDATE')")
    public PurchaseOrderResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePurchaseOrderRequest request) {
        return PurchaseOrderMapper.toResponse(service.update(id, request));
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(filename).build());
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
