package com.digipals.wms.goodsreceiving.controller;

import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.service.GoodsReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goods-receipts")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService service;

    @PostMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_CREATE')")
    public GoodsReceiptResponse create(
            @Valid
            @RequestBody
            CreateGoodsReceiptRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_UPDATE')")
    public GoodsReceiptResponse update(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdateGoodsReceiptRequest request) {

        return service.update(id, request);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_APPROVE')")
    public GoodsReceiptResponse approve(
            @PathVariable UUID id) {

        return service.approve(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public GoodsReceiptResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptResponse> findByPurchaseOrder(
            @PathVariable UUID purchaseOrderId) {

        return service.findByPurchaseOrder(
                purchaseOrderId);
    }

    @PostMapping("/{id}/load-po-lines")
public GoodsReceiptResponse loadPurchaseOrderLines(
        @PathVariable UUID id) {

    return service.loadPurchaseOrderLines(id);
}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_DELETE')")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}