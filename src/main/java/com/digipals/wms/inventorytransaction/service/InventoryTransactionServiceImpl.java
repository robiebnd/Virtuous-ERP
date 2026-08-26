package com.digipals.wms.inventorytransaction.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.InsufficientStockException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryBinRepository inventoryBinRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final BinRepository binRepository;
    private final CurrentUserService currentUserService;

    private InventoryBin getInventoryBin(UUID warehouseId, UUID binId, UUID productId) {
        return inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(warehouseId, binId, productId)
                .orElseGet(() -> {
                    Warehouse warehouse = warehouseRepository.findById(warehouseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));
                    Bin bin = binRepository.findById(binId)
                            .orElseThrow(() -> new ResourceNotFoundException("Bin not found."));
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

                    return inventoryBinRepository.save(
                            InventoryBin.builder()
                                    .warehouse(warehouse)
                                    .bin(bin)
                                    .product(product)
                                    .quantityOnHand(BigDecimal.ZERO)
                                    .quantityReserved(BigDecimal.ZERO)
                                    .build());
                });
    }

    private InventoryTransaction createTransaction(
            InventoryBin inventory,
            TransactionType transactionType,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            Bin fromBin,
            Bin toBin) {

        if (referenceNumber == null || referenceNumber.isBlank()) {
            throw new IllegalArgumentException("Reference number is required.");
        }
        if (referenceType == null || referenceType.isBlank()) {
            throw new IllegalArgumentException("Reference type is required.");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction quantity cannot be zero.");
        }

        boolean duplicate = transactionRepository
                .existsByReferenceNumberAndReferenceTypeAndInventoryBinIdAndTransactionType(
                        referenceNumber,
                        referenceType,
                        inventory.getId(),
                        transactionType);

        if (duplicate) {
            return transactionRepository
                    .findByReferenceNumberOrderByTransactionDateDesc(referenceNumber)
                    .stream()
                    .filter(t -> t.getInventoryBin().getId().equals(inventory.getId()))
                    .filter(t -> t.getTransactionType() == transactionType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Duplicate inventory transaction detected."));
        }

        User user = currentUserService.getCurrentUser();

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryBin(inventory)
                .transactionType(transactionType)
                .quantity(quantity)
                .balanceAfter(inventory.getQuantityOnHand())
                .referenceNumber(referenceNumber)
                .referenceType(referenceType)
                .performedBy(user)
                .remarks(remarks)
                .fromBin(fromBin)
                .toBin(toBin)
                .transactionDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    private void validatePositiveQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    private void validateStock(InventoryBin inventory, BigDecimal quantity) {
        if (inventory.getQuantityOnHand().compareTo(quantity) < 0) {
            throw new InsufficientStockException("Insufficient stock available.");
        }
    }

    private InventoryBin updateInventory(InventoryBin inventory, BigDecimal newBalance) {
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientStockException("Inventory cannot become negative.");
        }
        inventory.setQuantityOnHand(newBalance);
        return inventoryBinRepository.save(inventory);
    }

    @Override
    public InventoryTransaction receiveStock(UUID warehouseId, UUID binId, UUID productId,
                                             BigDecimal quantity, String referenceNumber,
                                             String referenceType, String remarks) {
        validatePositiveQuantity(quantity);

        InventoryBin inventory = getInventoryBin(warehouseId, binId, productId);
        BigDecimal newBalance = inventory.getQuantityOnHand().add(quantity);
        inventory = updateInventory(inventory, newBalance);

        return createTransaction(inventory, TransactionType.GOODS_RECEIPT, quantity,
                referenceNumber, referenceType, remarks, null, inventory.getBin());
    }

    @Override
    public InventoryTransaction issueStock(UUID warehouseId, UUID binId, UUID productId,
                                           BigDecimal quantity, String referenceNumber,
                                           String referenceType, String remarks) {
        validatePositiveQuantity(quantity);

        InventoryBin inventory = getInventoryBin(warehouseId, binId, productId);
        validateStock(inventory, quantity);
        inventory = updateInventory(inventory, inventory.getQuantityOnHand().subtract(quantity));

        return createTransaction(inventory, TransactionType.TRANSFER_OUT, quantity.negate(),
                referenceNumber, referenceType, remarks, inventory.getBin(), null);
    }

    @Override
    public InventoryTransaction adjustStock(UUID warehouseId, UUID binId, UUID productId,
                                            BigDecimal quantity, TransactionType transactionType,
                                            String referenceNumber, String remarks) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Adjustment quantity cannot be zero.");
        }

        InventoryBin inventory = getInventoryBin(warehouseId, binId, productId);
        BigDecimal newBalance;

        switch (transactionType) {
            case ADJUSTMENT_IN, TRANSFER_IN, GOODS_RECEIPT, PURCHASE_RECEIPT,
                    CUSTOMER_RETURN, RETURN_IN, REPLENISHMENT_IN ->
                    newBalance = inventory.getQuantityOnHand().add(quantity.abs());
            case ADJUSTMENT_OUT, TRANSFER_OUT, SALE, SUPPLIER_RETURN, WRITE_OFF,
                    PICK, RETURN_OUT, REPLENISHMENT_OUT -> {
                validateStock(inventory, quantity.abs());
                newBalance = inventory.getQuantityOnHand().subtract(quantity.abs());
            }
            case ADJUSTMENT -> {
                newBalance = inventory.getQuantityOnHand().add(quantity);
                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientStockException("Adjustment results in negative stock.");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type.");
        }

        inventory = updateInventory(inventory, newBalance);
        BigDecimal ledgerQuantity = switch (transactionType) {
            case ADJUSTMENT_OUT, TRANSFER_OUT, SALE, SUPPLIER_RETURN, WRITE_OFF,
                    PICK, RETURN_OUT, REPLENISHMENT_OUT -> quantity.abs().negate();
            default -> transactionType == TransactionType.ADJUSTMENT ? quantity : quantity.abs();
        };

        return createTransaction(inventory, transactionType, ledgerQuantity,
                referenceNumber, transactionType.name(), remarks, null, inventory.getBin());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByInventoryBin(UUID inventoryBinId) {
        return transactionRepository.findByInventoryBinIdOrderByTransactionDateDesc(inventoryBinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByBin(UUID binId) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        transactions.addAll(transactionRepository.findByFromBinIdOrderByTransactionDateDesc(binId));
        transactionRepository.findByToBinIdOrderByTransactionDateDesc(binId).stream()
                .filter(t -> !transactions.contains(t))
                .forEach(transactions::add);
        transactions.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumberOrderByTransactionDateDesc(referenceNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByReferenceType(String referenceType) {
        return transactionRepository.findByReferenceTypeOrderByTransactionDateDesc(referenceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByWarehouseCode(String warehouseCode) {
        return transactionRepository.findByInventoryBin_Warehouse_CodeOrderByTransactionDateDesc(warehouseCode);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryTransaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory Transaction not found."));
    }
}
