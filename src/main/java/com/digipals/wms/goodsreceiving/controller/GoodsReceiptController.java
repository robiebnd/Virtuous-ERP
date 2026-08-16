package com.digipals.wms.goodsreceiving.controller;

import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.service.GoodsReceiptPdfService;
import com.digipals.wms.goodsreceiving.service.GoodsReceiptService;
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
@RequestMapping("/api/goods-receipts")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService service;
    private final GoodsReceiptPdfService pdfService;

    @PostMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_CREATE')")
    public GoodsReceiptResponse create(@Valid @RequestBody CreateGoodsReceiptRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_UPDATE')")
    public GoodsReceiptResponse update(@PathVariable UUID id,
                                       @Valid @RequestBody UpdateGoodsReceiptRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_APPROVE')")
    public GoodsReceiptResponse approve(@PathVariable UUID id) {
        return service.approve(id);
    }

    @PutMapping("/number/{grnNumber}/approve")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_APPROVE')")
    public GoodsReceiptResponse approveByNumber(@PathVariable String grnNumber) {
        return service.approve(service.findByNumber(grnNumber).getId());
    }

    @PostMapping("/{id}/load-po-lines")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_UPDATE')")
    public GoodsReceiptResponse loadPurchaseOrderLines(@PathVariable UUID id) {
        return service.loadPurchaseOrderLines(id);
    }

    @PostMapping("/number/{grnNumber}/load-po-lines")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_UPDATE')")
    public GoodsReceiptResponse loadPurchaseOrderLinesByNumber(@PathVariable String grnNumber) {
        return service.loadPurchaseOrderLines(service.findByNumber(grnNumber).getId());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public GoodsReceiptResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/number/{grnNumber}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public GoodsReceiptResponse findByNumber(@PathVariable String grnNumber) {
        return service.findByNumber(grnNumber);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public ResponseEntity<byte[]> pdf(@PathVariable UUID id) {
        return pdfResponse(pdfService.generateById(id), "goods-receipt.pdf");
    }

    @GetMapping("/number/{grnNumber}/pdf")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public ResponseEntity<byte[]> pdfByNumber(@PathVariable String grnNumber) {
        return pdfResponse(pdfService.generateByNumber(grnNumber), grnNumber + ".pdf");
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_VIEW')")
    public List<GoodsReceiptResponse> findByPurchaseOrder(@PathVariable UUID purchaseOrderId) {
        return service.findByPurchaseOrder(purchaseOrderId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GOODS_RECEIPT_DELETE')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] bytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(filename).build());
        headers.setContentLength(bytes.length);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
