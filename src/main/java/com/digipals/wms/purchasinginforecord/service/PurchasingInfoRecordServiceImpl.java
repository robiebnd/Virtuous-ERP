package com.digipals.wms.purchasinginforecord.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.productsupplieridentifier.repository.ProductSupplierIdentifierRepository;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordRequest;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordResponse;
import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import com.digipals.wms.purchasinginforecord.repository.PurchasingInfoRecordRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchasingInfoRecordServiceImpl implements PurchasingInfoRecordService {

    private final PurchasingInfoRecordRepository repository;
    private final ProductSupplierIdentifierRepository supplierProductRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public PurchasingInfoRecordResponse create(PurchasingInfoRecordRequest request) {
        ProductSupplierIdentifier supplierProduct = findSupplierProduct(request.getSupplierProductIdentifierId());
        Warehouse warehouse = findWarehouse(request.getWarehouseId());
        validateUnique(supplierProduct.getId(), warehouse.getId(), null);

        PurchasingInfoRecord entity = PurchasingInfoRecord.builder()
                .supplierProduct(supplierProduct)
                .warehouse(warehouse)
                .currency(normalizeCurrency(request.getCurrency()))
                .lastPurchasePrice(request.getLastPurchasePrice())
                .standardOrderQuantity(request.getStandardOrderQuantity())
                .plannedDeliveryDays(request.getPlannedDeliveryDays())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .regularSupplier(Boolean.TRUE.equals(request.getRegularSupplier()))
                .automaticSourcing(Boolean.TRUE.equals(request.getAutomaticSourcing()))
                .active(true)
                .build();

        return toResponse(repository.save(entity));
    }

    @Override
    public PurchasingInfoRecordResponse update(UUID id, PurchasingInfoRecordRequest request) {
        PurchasingInfoRecord entity = findEntity(id);
        ProductSupplierIdentifier supplierProduct = findSupplierProduct(request.getSupplierProductIdentifierId());
        Warehouse warehouse = findWarehouse(request.getWarehouseId());
        validateUnique(supplierProduct.getId(), warehouse.getId(), id);

        entity.setSupplierProduct(supplierProduct);
        entity.setWarehouse(warehouse);
        entity.setCurrency(normalizeCurrency(request.getCurrency()));
        entity.setLastPurchasePrice(request.getLastPurchasePrice());
        entity.setStandardOrderQuantity(request.getStandardOrderQuantity());
        entity.setPlannedDeliveryDays(request.getPlannedDeliveryDays());
        entity.setValidFrom(request.getValidFrom());
        entity.setValidTo(request.getValidTo());
        entity.setRegularSupplier(Boolean.TRUE.equals(request.getRegularSupplier()));
        entity.setAutomaticSourcing(Boolean.TRUE.equals(request.getAutomaticSourcing()));
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchasingInfoRecordResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchasingInfoRecordResponse findBySupplierProductAndWarehouse(
            UUID supplierProductIdentifierId, UUID warehouseId) {
        return repository.findBySupplierProductIdAndWarehouseId(supplierProductIdentifierId, warehouseId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Purchasing info record not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasingInfoRecordResponse> findBySupplierProduct(UUID supplierProductIdentifierId) {
        findSupplierProduct(supplierProductIdentifierId);
        return repository.findBySupplierProductId(supplierProductIdentifierId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasingInfoRecordResponse> findByWarehouse(UUID warehouseId) {
        findWarehouse(warehouseId);
        return repository.findByWarehouseId(warehouseId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasingInfoRecordResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    private ProductSupplierIdentifier findSupplierProduct(UUID id) {
        return supplierProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product identifier not found."));
    }

    private Warehouse findWarehouse(UUID id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));
    }

    private PurchasingInfoRecord findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchasing info record not found."));
    }

    private void validateUnique(UUID supplierProductId, UUID warehouseId, UUID currentId) {
        repository.findBySupplierProductIdAndWarehouseId(supplierProductId, warehouseId)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "A purchasing info record already exists for this supplier product and warehouse.");
                });
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || !currency.trim().matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO currency code.");
        }
        return currency.trim().toUpperCase();
    }

    private PurchasingInfoRecordResponse toResponse(PurchasingInfoRecord entity) {
        ProductSupplierIdentifier supplierProduct = entity.getSupplierProduct();
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
