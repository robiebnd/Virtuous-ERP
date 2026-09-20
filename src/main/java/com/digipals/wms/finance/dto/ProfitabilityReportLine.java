package com.digipals.wms.finance.dto;
import java.math.BigDecimal; import java.util.UUID;
public record ProfitabilityReportLine(UUID segmentId,String segmentCode,String segmentName,String productCode,String salesChannel,String marketRegion,String customerGroup,String productGroup,BigDecimal revenue,BigDecimal cost,BigDecimal contributionMargin){}
