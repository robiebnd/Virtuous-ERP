package com.digipals.wms.finance.dto;
import java.math.BigDecimal; import java.util.UUID;
public record ProfitabilityReportLine(UUID segmentId,String segmentCode,String segmentName,BigDecimal revenue,BigDecimal cost,BigDecimal contributionMargin){}