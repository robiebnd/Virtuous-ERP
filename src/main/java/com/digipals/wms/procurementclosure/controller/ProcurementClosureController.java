package com.digipals.wms.procurementclosure.controller;

import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.procurementclosure.dto.ProcurementClosureRequests.*;
import com.digipals.wms.procurementclosure.service.ProcurementClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/procurement/closure")
@RequiredArgsConstructor
public class ProcurementClosureController {
    private final ProcurementClosureService service;

    @PostMapping("/invoice-verification")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> verifyInvoice(@RequestBody SupplierInvoiceRequest request){ return service.verifyInvoice(request); }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> pay(@RequestBody SupplierPaymentRequest request){ return service.pay(request); }

    @PostMapping("/goods-issue")
    public GoodsMovementResponse goodsIssue(@RequestBody GoodsIssueRequest request){ return service.goodsIssue(request); }

    @GetMapping("/purchase-orders/{poNumber}/reconciliation")
    public Map<String,Object> reconcile(@PathVariable String poNumber){ return service.reconcile(poNumber); }

    @PostMapping("/purchase-orders/{poNumber}/close")
    public Map<String,Object> close(@PathVariable String poNumber){ return service.close(poNumber); }

    @PostMapping("/vendor-evaluations")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> evaluate(@RequestBody VendorEvaluationRequest request){ return service.evaluate(request); }
}
