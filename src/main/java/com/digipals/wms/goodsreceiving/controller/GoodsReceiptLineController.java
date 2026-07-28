
package com.digipals.wms.goodsreceiving.controller;

import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptLineRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptLineResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptLineRequest;
import com.digipals.wms.goodsreceiving.service.GoodsReceiptLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goods-receipt-lines")
@RequiredArgsConstructor
public class GoodsReceiptLineController {

    private final GoodsReceiptLineService service;

    @PostMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_CREATE')")
    public GoodsReceiptLineResponse create(
            @Valid
            @RequestBody
            CreateGoodsReceiptLineRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_UPDATE')")
    public GoodsReceiptLineResponse update(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdateGoodsReceiptLineRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptLineResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public GoodsReceiptLineResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/goods-receipt/{goodsReceiptId}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptLineResponse> findByGoodsReceipt(
            @PathVariable UUID goodsReceiptId) {

        return service.findByGoodsReceipt(
                goodsReceiptId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_DELETE')")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
} 
    

