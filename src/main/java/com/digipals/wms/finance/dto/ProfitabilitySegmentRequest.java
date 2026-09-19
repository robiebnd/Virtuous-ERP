package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record ProfitabilitySegmentRequest(@NotBlank String segmentCode,@NotBlank String segmentName,@NotBlank String companyCode,UUID customerId,String productCode,String salesChannel,String marketRegion,String customerGroup,String productGroup){}