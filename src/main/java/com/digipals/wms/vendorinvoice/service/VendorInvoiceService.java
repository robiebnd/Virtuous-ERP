package com.digipals.wms.vendorinvoice.service;

import com.digipals.wms.vendorinvoice.dto.CreateVendorInvoiceRequest;
import com.digipals.wms.vendorinvoice.dto.VendorInvoiceResponse;
import java.util.List;
import java.util.UUID;

public interface VendorInvoiceService {
    VendorInvoiceResponse create(CreateVendorInvoiceRequest request);
    VendorInvoiceResponse match(UUID id);
    VendorInvoiceResponse findById(UUID id);
    VendorInvoiceResponse findByNumber(String invoiceNumber);
    List<VendorInvoiceResponse> findAll();
}
