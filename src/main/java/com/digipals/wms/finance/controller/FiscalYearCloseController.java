package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.FiscalYearCloseResponse;
import com.digipals.wms.finance.service.FiscalYearCloseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/fiscal-years")
@RequiredArgsConstructor
public class FiscalYearCloseController {
    private final FiscalYearCloseService service;

    @PostMapping("/{fiscalYear}/close")
    public FiscalYearCloseResponse close(@PathVariable int fiscalYear) {
        return service.closeAndCarryForward(fiscalYear);
    }
}
