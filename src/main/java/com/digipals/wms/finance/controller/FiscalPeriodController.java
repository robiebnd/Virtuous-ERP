package com.digipals.wms.finance.controller;

import com.digipals.wms.finance.dto.FiscalPeriodRequest;
import com.digipals.wms.finance.dto.FiscalPeriodResponse;
import com.digipals.wms.finance.dto.FiscalPeriodStatusRequest;
import com.digipals.wms.finance.service.FiscalPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/fiscal-periods")
@RequiredArgsConstructor
public class FiscalPeriodController {
    private final FiscalPeriodService service;

    @GetMapping
    public List<FiscalPeriodResponse> list(
        @RequestParam(defaultValue = "ZW01") String companyCode,
        @RequestParam int fiscalYear
    ) {
        return service.list(companyCode, fiscalYear).stream().map(FiscalPeriodResponse::from).toList();
    }

    @PostMapping("/years")
    public List<FiscalPeriodResponse> createYear(@Valid @RequestBody FiscalPeriodRequest request) {
        return service.createYear(request).stream().map(FiscalPeriodResponse::from).toList();
    }

    @PostMapping("/{year}/{period}/close")
    public FiscalPeriodResponse close(
        @PathVariable int year,
        @PathVariable int period,
        @RequestParam(defaultValue = "SYSTEM") String companyCode,
        @RequestBody(required = false) FiscalPeriodStatusRequest request
    ) {
        String closedBy = request == null ? "SYSTEM" : request.closedBy();
        boolean permanent = request != null && FiscalPeriodService.PERMANENTLY_CLOSED.equalsIgnoreCase(request.status());
        return FiscalPeriodResponse.from(service.closePeriod(companyCode, year, period, closedBy, permanent));
    }

    @PostMapping("/{year}/{period}/reopen")
    public FiscalPeriodResponse reopen(
        @PathVariable int year,
        @PathVariable int period,
        @RequestParam(defaultValue = "ZW01") String companyCode
    ) {
        return FiscalPeriodResponse.from(service.reopenPeriod(companyCode, year, period));
    }
}
