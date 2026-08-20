package com.digipals.wms.purchasinginforecord.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordResponse;
import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import com.digipals.wms.purchasinginforecord.repository.PurchasingInfoRecordRepository;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SourceOfSupplyService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchasingInfoRecordRepository repository;

    public Map<String, Object> simulate(UUID productId, UUID warehouseId, LocalDate deliveryDate) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));

        LocalDate effectiveDate = deliveryDate == null ? LocalDate.now() : deliveryDate;
        List<PurchasingInfoRecord> candidates = repository.findByWarehouseId(warehouseId).stream()
                .filter(record -> Boolean.TRUE.equals(record.getActive()))
                .filter(record -> Boolean.TRUE.equals(record.getAutomaticSourcing()))
                .filter(record -> record.getSupplierProduct().getProduct().getId().equals(productId))
                .filter(record -> record.getValidFrom() == null || !effectiveDate.isBefore(record.getValidFrom()))
                .filter(record -> record.getValidTo() == null || !effectiveDate.isAfter(record.getValidTo()))
                .sorted(Comparator
                        .comparing((PurchasingInfoRecord r) -> Boolean.TRUE.equals(r.getRegularSupplier())).reversed()
                        .thenComparing(r -> r.getLastPurchasePrice() == null
                                ? java.math.BigDecimal.valueOf(Double.MAX_VALUE)
                                : r.getLastPurchasePrice()))
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("productId", productId);
        response.put("warehouseId", warehouseId);
        response.put("deliveryDate", effectiveDate);
        response.put("sourceDetermined", candidates.size() == 1);
        response.put("multipleSources", candidates.size() > 1);
        response.put("candidates", candidates.stream().map(this::toResponse).toList());
        response.put("selectedSource", candidates.isEmpty() ? null : toResponse(candidates.get(0)));
        return response;
    }

    private PurchasingInfoRecordResponse toResponse(PurchasingInfoRecord entity) {
        var supplierProduct = entity.getSupplierProduct();
        var product = supplierProduct.getProduct();
        var supplier = supplierProduct.getSupplier();
        var warehouse = entity.getWarehouse();

        return PurchasingInfoRecordResponse.builder()
                .id(entity.getId())
                .supplierProductIdentifierId(supplierProduct.getId())
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .supplierId(supplier.getId())
                .supplierCode(supplier.getCode())
                .supplierName(supplier.getName())
                .supplierItemCode(supplierProduct.getSupplierItemCode())
                .supplierItemName(supplierProduct.getSupplierItemName())
                .warehouseId(warehouse.getId())
                .warehouseCode(warehouse.getCode())
                .warehouseName(warehouse.getName())
                .currency(entity.getCurrency())
                .lastPurchasePrice(entity.getLastPurchasePrice())
                .standardOrderQuantity(entity.getStandardOrderQuantity())
                .plannedDeliveryDays(entity.getPlannedDeliveryDays())
                .validFrom(entity.getValidFrom())
                .validTo(entity.getValidTo())
                .regularSupplier(entity.getRegularSupplier())
                .automaticSourcing(entity.getAutomaticSourcing())
                .active(entity.getActive())
                .build();
    }
}
