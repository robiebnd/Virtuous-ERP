package com.digipals.wms.integration.manufacturing;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.UUID;
public record ProductionConfirmedEvent(UUID sourceDocumentId,String sourceDocumentNumber,String productSku,BigDecimal quantity,BigDecimal scrapQuantity,String currency,LocalDateTime postingDate) {}