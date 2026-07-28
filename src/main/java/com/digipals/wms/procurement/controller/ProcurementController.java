package com.digipals.wms.procurement.controller;

import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.procurement.service.ProcurementService;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class ProcurementController {

    private final ProcurementService service;

    @PostMapping("/generate-purchase-order")
    public PurchaseOrderResponse generatePurchaseOrder(

            @Valid
            @RequestBody
            GeneratePurchaseOrderRequest request) {

        return service.generatePurchaseOrder(request);
    }
}