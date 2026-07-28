package com.digipals.wms.inventorytransaction.service;

import com.digipals.wms.common.exception.InsufficientStockException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    private Inventory getInventory(UUID warehouseId, UUID productId) {
        return inventoryRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> Inventory.builder()
                        .warehouse(warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found.")))
                        .product(productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found.")))
                        .quantityOnHand(BigDecimal.ZERO) // Explicitly initialize to avoid NULLs
                        .build());
    }

    private BigDecimal getSafeQuantityOnHand(Inventory inventory) {
        return inventory.getQuantityOnHand() != null ? inventory.getQuantityOnHand() : BigDecimal.ZERO;
    }

    @Override
    public InventoryTransaction receiveStock(
            UUID warehouseId,
            UUID productId,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks) {

        Inventory inventory = getInventory(warehouseId, productId);
        BigDecimal currentQty = getSafeQuantityOnHand(inventory);

        inventory.setQuantityOnHand(currentQty.add(quantity));
        inventory = inventoryRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(TransactionType.PURCHASE_RECEIPT)
                .quantity(quantity)
                .balanceAfter(inventory.getQuantityOnHand())
                .referenceNumber(referenceNumber)
                .referenceType(referenceType)
                .performedBy(user)
                .remarks(remarks)
                .transactionDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public InventoryTransaction issueStock(
            UUID warehouseId,
            UUID productId,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks) {

        Inventory inventory = getInventory(warehouseId, productId);
        BigDecimal currentQty = getSafeQuantityOnHand(inventory);

        if (currentQty.compareTo(quantity) < 0) {
            throw new InsufficientStockException("Insufficient stock available.");
        }

        inventory.setQuantityOnHand(currentQty.subtract(quantity));
        inventory = inventoryRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(TransactionType.SALE)
                .quantity(quantity)
                .balanceAfter(inventory.getQuantityOnHand())
                .referenceNumber(referenceNumber)
                .referenceType(referenceType)
                .performedBy(user)
                .remarks(remarks)
                .transactionDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public InventoryTransaction adjustStock(
            UUID warehouseId,
            UUID productId,
            BigDecimal quantity,
            TransactionType transactionType,
            String referenceNumber,
            String remarks) {

        Inventory inventory = getInventory(warehouseId, productId);
        BigDecimal currentQty = getSafeQuantityOnHand(inventory);

        BigDecimal newQty;

        switch (transactionType) {
            case ADJUSTMENT -> {
                // Allows quantity to be positive or negative variance (e.g., -215.00)
                newQty = currentQty.add(quantity);
                if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientStockException("Adjustment results in negative stock balance.");
                }
            }
            case STOCK_COUNT, CUSTOMER_RETURN, PURCHASE_RECEIPT, TRANSFER_IN -> {
                newQty = currentQty.add(quantity);
            }
            case SALE, WRITE_OFF, SUPPLIER_RETURN, TRANSFER_OUT -> {
                if (currentQty.compareTo(quantity) < 0) {
                    throw new InsufficientStockException("Insufficient stock available.");
                }
                newQty = currentQty.subtract(quantity);
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type.");
        }

        inventory.setQuantityOnHand(newQty);
        inventory = inventoryRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(transactionType)
                .quantity(quantity)
                .balanceAfter(inventory.getQuantityOnHand())
                .referenceNumber(referenceNumber)
                .referenceType(transactionType.name())
                .performedBy(user)
                .remarks(remarks)
                .transactionDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByInventory(UUID inventoryId) {
        return transactionRepository.findByInventoryId(inventoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryTransaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory Transaction not found."));
    }
}