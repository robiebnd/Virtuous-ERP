package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
public record GroupAccountMappingRequest(@NotNull java.util.UUID groupId,@NotBlank String companyCode,@NotBlank String localAccountCode,@NotBlank String groupAccountCode,@NotBlank String groupAccountName,@NotBlank String groupAccountType){}
