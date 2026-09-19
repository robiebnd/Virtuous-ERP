package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.InternalOrderRepository;
import com.digipals.wms.finance.service.ManagementAccountingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/finance/management-accounting")
@RequiredArgsConstructor
public class ManagementAccountingController {
    private final ManagementAccountingService service;
    private final InternalOrderRepository internalOrders;

    @GetMapping("/cost-centers/actuals")
    public List<CostCenterActualResponse> costCenterActuals(@RequestParam(defaultValue="2026") int fiscalYear) {
        return service.costCenterActuals(fiscalYear);
    }

    @PostMapping("/cost-center-budgets")
    public ResponseEntity<CostCenterBudget> saveBudget(@Valid @RequestBody CostCenterBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveBudget(request));
    }

    @GetMapping("/internal-orders")
    public List<InternalOrder> internalOrderMaster() {
        return internalOrders.findAll().stream().sorted(Comparator.comparing(InternalOrder::getOrderCode)).toList();
    }

    @GetMapping("/internal-orders/actuals")
    public List<InternalOrderActualResponse> internalOrderActuals() {
        return service.internalOrderActuals();
    }

    @PostMapping("/internal-orders")
    public ResponseEntity<InternalOrder> createInternalOrder(@Valid @RequestBody InternalOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInternalOrder(request));
    }

    @PostMapping("/allocations")
    public ResponseEntity<?> allocate(@Valid @RequestBody CostCenterAllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.allocate(request));
    }
}