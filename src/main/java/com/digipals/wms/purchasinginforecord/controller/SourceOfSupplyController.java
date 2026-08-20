package com.digipals.wms.purchasinginforecord.controller;

import com.digipals.wms.purchasinginforecord.service.SourceOfSupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/source-of-supply")
@RequiredArgsConstructor
public class SourceOfSupplyController {

    private final SourceOfSupplyService service;

    @GetMapping("/simulate")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public Map<String, Object> simulate(
            @RequestParam UUID productId,
            @RequestParam UUID warehouseId,
            @RequestParam(required = false) LocalDate deliveryDate) {
        return service.simulate(productId, warehouseId, deliveryDate);
    }
}
