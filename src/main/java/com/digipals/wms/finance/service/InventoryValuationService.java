package com.digipals.wms.finance.service;

import com.digipals.wms.finance.dto.InventoryValuationResponse;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryValuationService {

    private final InventoryBinRepository inventoryBinRepository;

    public List<InventoryValuationResponse> valuation() {
        return aggregate(inventoryBinRepository.findAll());
    }

    public List<InventoryValuationResponse> valuationByWarehouse(UUID warehouseId) {
        return aggregate(inventoryBinRepository.findByWarehouseId(warehouseId));
    }

    private List<InventoryValuationResponse> aggregate(List<InventoryBin> records) {
        Map<String, Aggregate> grouped = new LinkedHashMap<>();

        for (InventoryBin inventory : records) {
            if (inventory.getProduct() == null || inventory.getWarehouse() == null) continue;
            BigDecimal quantity = nvl(inventory.getQuantityOnHand());
            String key = inventory.getWarehouse().getId() + ":" + inventory.getProduct().getId();
            Aggregate current = grouped.computeIfAbsent(key, ignored -> new Aggregate(inventory));
            current.quantity = current.quantity.add(quantity);
        }

        return grouped.values().stream().map(Aggregate::toResponse).toList();
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static final class Aggregate {
        private final InventoryBin first;
        private BigDecimal quantity = BigDecimal.ZERO;

        private Aggregate(InventoryBin first) {
            this.first = first;
        }

        private InventoryValuationResponse toResponse() {
            BigDecimal unitCost = first.getProduct().getCostPrice() == null
                    ? BigDecimal.ZERO
                    : first.getProduct().getCostPrice();
            unitCost = unitCost.setScale(2, RoundingMode.HALF_UP);
            BigDecimal value = quantity.multiply(unitCost).setScale(2, RoundingMode.HALF_UP);
            return new InventoryValuationResponse(
                    first.getWarehouse().getId(),
                    first.getWarehouse().getCode(),
                    first.getProduct().getId(),
                    first.getProduct().getSku(),
                    first.getProduct().getName(),
                    quantity.setScale(2, RoundingMode.HALF_UP),
                    unitCost,
                    value);
        }
    }
}
