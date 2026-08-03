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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionServiceImpl
        implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryBinRepository inventoryBinRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final BinRepository binRepository;
    private final CurrentUserService currentUserService;

    private InventoryBin getInventoryBin(
            UUID warehouseId,
            UUID binId,
            UUID productId) {

        return inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(
                        warehouseId,
                        binId,
                        productId)
                .orElseGet(() -> {

                    Warehouse warehouse =
                            warehouseRepository.findById(warehouseId)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("Warehouse not found."));

                    Bin bin =
                            binRepository.findById(binId)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("Bin not found."));

                    Product product =
                            productRepository.findById(productId)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("Product not found."));

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

    @Override
    public InventoryTransaction receiveStock(
            UUID warehouseId,
            UUID binId,
            UUID productId,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks) {

        InventoryBin inventory = getInventoryBin(
                warehouseId,
                binId,
                productId);

        inventory.setQuantityOnHand(
                inventory.getQuantityOnHand().add(quantity));

        inventory = inventoryBinRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        return transactionRepository.save(
                InventoryTransaction.builder()
                        .inventoryBin(inventory)
                        .transactionType(TransactionType.GOODS_RECEIPT)
                        .quantity(quantity)
                        .balanceAfter(inventory.getQuantityOnHand())
                        .referenceNumber(referenceNumber)
                        .referenceType(referenceType)
                        .performedBy(user)
                        .remarks(remarks)
                        .transactionDate(LocalDateTime.now())
                        .build());
    }

    @Override
    public InventoryTransaction issueStock(
            UUID warehouseId,
            UUID binId,
            UUID productId,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks) {

        InventoryBin inventory = getInventoryBin(
                warehouseId,
                binId,
                productId);

        if (inventory.getQuantityOnHand().compareTo(quantity) < 0) {
            throw new InsufficientStockException("Insufficient stock available.");
        }

        inventory.setQuantityOnHand(
                inventory.getQuantityOnHand().subtract(quantity));

        inventory = inventoryBinRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        return transactionRepository.save(
                InventoryTransaction.builder()
                        .inventoryBin(inventory)
                        .transactionType(TransactionType.TRANSFER_OUT)
                        .quantity(quantity.negate())
                        .balanceAfter(inventory.getQuantityOnHand())
                        .referenceNumber(referenceNumber)
                        .referenceType(referenceType)
                        .performedBy(user)
                        .remarks(remarks)
                        .transactionDate(LocalDateTime.now())
                        .build());
    }

    @Override
    public InventoryTransaction adjustStock(
            UUID warehouseId,
            UUID binId,
            UUID productId,
            BigDecimal quantity,
            TransactionType transactionType,
            String referenceNumber,
            String remarks) {

        InventoryBin inventory = getInventoryBin(
                warehouseId,
                binId,
                productId);

        BigDecimal newBalance =
                inventory.getQuantityOnHand().add(quantity);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientStockException(
                    "Adjustment results in negative stock.");
        }

        inventory.setQuantityOnHand(newBalance);

        inventory = inventoryBinRepository.save(inventory);

        User user = currentUserService.getCurrentUser();

        return transactionRepository.save(
                InventoryTransaction.builder()
                        .inventoryBin(inventory)
                        .transactionType(transactionType)
                        .quantity(quantity)
                        .balanceAfter(newBalance)
                        .referenceNumber(referenceNumber)
                        .referenceType(transactionType.name())
                        .performedBy(user)
                        .remarks(remarks)
                        .transactionDate(LocalDateTime.now())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByInventoryBin(
            UUID inventoryBinId) {

        return transactionRepository.findByInventoryBinId(inventoryBinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByBin(
            UUID binId) {

        return transactionRepository.findByFromBinId(binId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> findByReferenceNumber(
            String referenceNumber) {

        return transactionRepository.findByReferenceNumber(referenceNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryTransaction findById(UUID id) {

        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory Transaction not found."));
    }
}