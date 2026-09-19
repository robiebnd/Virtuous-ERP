package com.digipals.wms.finance.dto;

import com.digipals.wms.finance.entity.FiscalPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FiscalPeriodResponse(
    UUID id, String companyCode, Integer fiscalYear, Integer periodNumber, String periodName,
    LocalDate startDate, LocalDate endDate, String status, LocalDateTime closedAt, String closedBy
) {
    public static FiscalPeriodResponse from(FiscalPeriod p) {
        return new FiscalPeriodResponse(p.getId(),p.getCompanyCode(),p.getFiscalYear(),p.getPeriodNumber(),
            p.getPeriodName(),p.getStartDate(),p.getEndDate(),p.getStatus(),p.getClosedAt(),p.getClosedBy());
    }
}