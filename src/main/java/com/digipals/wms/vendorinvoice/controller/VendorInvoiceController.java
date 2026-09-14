package com.digipals.wms.vendorinvoice.controller;

import com.digipals.wms.vendorinvoice.dto.CreateVendorInvoiceRequest;
import com.digipals.wms.vendorinvoice.dto.VendorInvoiceResponse;
import com.digipals.wms.vendorinvoice.service.VendorInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-invoices")
@RequiredArgsConstructor
public class VendorInvoiceController {
    private final VendorInvoiceService service;

    @PostMapping
    public VendorInvoiceResponse create(@Valid @RequestBody CreateVendorInvoiceRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/match")
    public VendorInvoiceResponse match(@PathVariable UUID id) {
        return service.match(id);
    }

    @GetMapping
    public List<VendorInvoiceResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public VendorInvoiceResponse findById(@PathVariable UUID id) { return service.findById(id); }

    @GetMapping("/number/{invoiceNumber}")
    public VendorInvoiceResponse findByNumber(@PathVariable String invoiceNumber) { return service.findByNumber(invoiceNumber); }
}
