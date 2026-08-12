package com.digipals.wms.inventory.reconciliation.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.inventory.reconciliation.dto.InventoryReconciliationRequest;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryReconciliationServiceImpl implements InventoryReconciliationService {

    private static final String REFERENCE_TYPE = "INVENTORY_RECONCILIATION";

    private final InventoryBinRepository inventoryBinRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Override
    public InventoryBin reconcile(InventoryReconciliationRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));

        Bin bin = binRepository.findById(request.getBinId())
                .orElseThrow(() -> new ResourceNotFoundException("Bin not found."));

        if (bin.getWarehouse() == null || !warehouse.getId().equals(bin.getWarehouse().getId())) {
            throw new InvalidWorkflowException("Bin does not belong to the specified warehouse.");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        InventoryBin inventory = inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(
                        warehouse.getId(), bin.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory record not found for the specified warehouse, bin and product."));

        BigDecimal quantity = request.getQuantity();
        BigDecimal currentBalance = inventory.getQuantityOnHand();
        BigDecimal newBalance = currentBalance.add(quantity);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidWorkflowException("Reconciliation would make stock negative.");
        }

        User performedBy = currentUserService.getCurrentUser();

        inventory.setQuantityOnHand(newBalance);
        inventory = inventoryBinRepository.save(inventory);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryBin(inventory)
                .transactionType(quantity.compareTo(BigDecimal.ZERO) >= 0
                        ? TransactionType.ADJUSTMENT_IN
                        : TransactionType.ADJUSTMENT_OUT)
                .quantity(quantity)
                .balanceAfter(newBalance)
                .referenceNumber(request.getReferenceNumber())
                .referenceType(REFERENCE_TYPE)
                .performedBy(performedBy)
                .fromBin(quantity.compareTo(BigDecimal.ZERO) < 0 ? bin : null)
                .toBin(quantity.compareTo(BigDecimal.ZERO) > 0 ? bin : null)
                .remarks(request.getRemarks())
                .transactionDate(LocalDateTime.now())
                .build();

        inventoryTransactionRepository.save(transaction);

        return inventory;
    }
}
