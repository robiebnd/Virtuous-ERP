package com.digipals.wms.inventorymovement.service;

import com.digipals.wms.inventorymovement.dto.InventoryMovementResponse;
import com.digipals.wms.inventorymovement.entity.InventoryMovement;
import com.digipals.wms.inventorymovement.repository.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementRepository repository;

    @Override
    @Transactional
    public InventoryMovementResponse create(InventoryMovement movement) {
        if (movement.getQuantity() == null || movement.getQuantity().signum() <= 0) {
            throw new IllegalArgumentException("Movement quantity must be greater than zero.");
        }
        if (movement.getMovementType() == null) {
            throw new IllegalArgumentException("Movement type is required.");
        }
        if (movement.getReferenceType() == null || movement.getReferenceType().isBlank()) {
            throw new IllegalArgumentException("Reference type is required.");
        }
        if (movement.getReferenceId() == null) {
            throw new IllegalArgumentException("Reference ID is required.");
        }
        return toResponse(repository.save(movement));
    }

    @Override
    public List<InventoryMovementResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<InventoryMovementResponse> findBySku(String sku) {
        return repository.findBySkuOrderByMovementDateDesc(sku).stream().map(this::toResponse).toList();
    }

    @Override
    public List<InventoryMovementResponse> findByWarehouse(UUID warehouseId) {
        return repository.findByWarehouseIdOrderByMovementDateDesc(warehouseId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<InventoryMovementResponse> findByReference(String referenceType, UUID referenceId) {
        return repository.findByReferenceTypeAndReferenceId(referenceType, referenceId)
                .stream().map(this::toResponse).toList();
    }

    private InventoryMovementResponse toResponse(InventoryMovement m) {
        return new InventoryMovementResponse(
                m.getId(), m.getMovementDate(), m.getMovementType(), m.getReferenceType(),
                m.getReferenceId(), m.getReferenceNumber(), m.getWarehouseId(), m.getFromBinId(),
                m.getToBinId(), m.getProductId(), m.getSku(), m.getQuantity(), m.getPerformedById(),
                m.getRemarks(), m.getCreatedAt());
    }
}
