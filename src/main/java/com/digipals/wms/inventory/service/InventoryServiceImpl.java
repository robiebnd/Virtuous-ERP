package com.digipals.wms.inventory.service;

import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl
        implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final WarehouseRepository warehouseRepository;

    private final ProductRepository productRepository;

    private final InventoryTransactionRepository
            inventoryTransactionRepository;

    @Override
    public Inventory create(
            Inventory inventory) {

        Warehouse warehouse =
                warehouseRepository.findById(
                        inventory.getWarehouse().getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Warehouse not found"));

        Product product =
                productRepository.findById(
                        inventory.getProduct().getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        inventoryRepository
                .findByWarehouseIdAndProductId(
                        warehouse.getId(),
                        product.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Inventory already exists for this warehouse and product.");
                });

        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);

        return inventoryRepository.save(
                inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAll() {

        return inventoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory findById(
            UUID id) {

        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByWarehouse(
            UUID warehouseId) {

        return inventoryRepository.findByWarehouseId(
                warehouseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByProduct(
            UUID productId) {

        return inventoryRepository.findByProductId(
                productId);
    }

    @Override
    public Inventory adjustStock(
            UUID inventoryId,
            BigDecimal quantity) {

        Inventory inventory =
                findById(inventoryId);

        BigDecimal balance =
                inventory.getQuantityOnHand()
                        .add(quantity);

        if (balance.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Insufficient stock.");
        }

        inventory.setQuantityOnHand(
                balance);

        return inventoryRepository.save(
                inventory);
    }

    @Override
    public Inventory receiveStock(
            Warehouse warehouse,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy) {

        Inventory inventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                warehouse.getId(),
                                product.getId())
                        .orElseGet(() ->

                                Inventory.builder()

                                        .warehouse(warehouse)

                                        .product(product)

                                        .quantityOnHand(BigDecimal.ZERO)

                                        .quantityReserved(BigDecimal.ZERO)

                                        .reorderLevel(BigDecimal.ZERO)

                                        .build());

        BigDecimal balance =
                inventory.getQuantityOnHand()
                        .add(quantity);

        inventory.setQuantityOnHand(
                balance);

        inventory =
                inventoryRepository.save(
                        inventory);

        InventoryTransaction transaction =
                InventoryTransaction.builder()

                        .inventory(
                                inventory)

                        .transactionType(
                                TransactionType.PURCHASE_RECEIPT)

                        .quantity(
                                quantity)

                        .balanceAfter(
                                balance)

                        .referenceNumber(
                                referenceNumber)

                        .referenceType(
                                referenceType)

                        .performedBy(
                                performedBy)

                        .remarks(
                                remarks)

                        .build();

        inventoryTransactionRepository.save(
                transaction);

        return inventory;
    }

    @Override
    public Inventory issueStock(
            Warehouse warehouse,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy) {

        Inventory inventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                warehouse.getId(),
                                product.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory record not found."));

        BigDecimal balance =
                inventory.getQuantityOnHand()
                        .subtract(quantity);

        if (balance.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Insufficient stock.");
        }

        inventory.setQuantityOnHand(
                balance);

        inventory =
                inventoryRepository.save(
                        inventory);

        InventoryTransaction transaction =
                InventoryTransaction.builder()

                        .inventory(
                                inventory)

                        .transactionType(
                                TransactionType.SALE)

                        .quantity(
                                quantity)

                        .balanceAfter(
                                balance)

                        .referenceNumber(
                                referenceNumber)

                        .referenceType(
                                referenceType)

                        .performedBy(
                                performedBy)

                        .remarks(
                                remarks)

                        .build();

        inventoryTransactionRepository.save(
                transaction);

        return inventory;
    }
}