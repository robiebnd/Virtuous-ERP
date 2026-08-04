package com.digipals.wms.inventory.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

        private final InventoryBinRepository inventoryBinRepository;

        private final WarehouseRepository warehouseRepository;

        private final ProductRepository productRepository;

        private final InventoryTransactionRepository inventoryTransactionRepository;

        @Override
        public InventoryBin create(InventoryBin inventoryBin) {

                Warehouse warehouse = warehouseRepository.findById(
                                inventoryBin.getWarehouse().getId())
                                .orElseThrow(() -> new RuntimeException("Warehouse not found."));

                Product product = productRepository.findById(
                                inventoryBin.getProduct().getId())
                                .orElseThrow(() -> new RuntimeException("Product not found."));

                if (inventoryBinRepository.existsByWarehouseIdAndBinIdAndProductId(
                                warehouse.getId(),
                                inventoryBin.getBin().getId(),
                                product.getId())) {

                        throw new RuntimeException(
                                        "Inventory already exists for this warehouse/bin/product.");
                }

                inventoryBin.setWarehouse(warehouse);
                inventoryBin.setProduct(product);

                if (inventoryBin.getQuantityOnHand() == null) {
                        inventoryBin.setQuantityOnHand(BigDecimal.ZERO);
                }

                if (inventoryBin.getQuantityReserved() == null) {
                        inventoryBin.setQuantityReserved(BigDecimal.ZERO);
                }

                return inventoryBinRepository.save(inventoryBin);
        }

        @Override
        @Transactional(readOnly = true)
        public List<InventoryBin> findAll() {
                return inventoryBinRepository.findAll();
        }

        @Override
        @Transactional(readOnly = true)
        public InventoryBin findById(UUID id) {

                return inventoryBinRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Inventory record not found."));
        }


        @Override
        @Transactional(readOnly = true)
        public List<InventoryBin> findByWarehouse(UUID warehouseId) {

                return inventoryBinRepository.findByWarehouseId(warehouseId);
        }

        @Override
        @Transactional(readOnly = true)
        public List<InventoryBin> findByProduct(UUID productId) {

                return inventoryBinRepository.findByProductId(productId);
        }

        @Override
        public InventoryBin adjustStock(
                        UUID inventoryBinId,
                        BigDecimal quantity) {

                InventoryBin inventory = findById(inventoryBinId);

                BigDecimal balance = inventory.getQuantityOnHand().add(quantity);

                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("Insufficient stock.");
                }

                inventory.setQuantityOnHand(balance);

                inventory = inventoryBinRepository.save(inventory);

                inventoryTransactionRepository.save(

                                InventoryTransaction.builder()

                                                .inventoryBin(inventory)

                                                .transactionType(
                                                                quantity.compareTo(BigDecimal.ZERO) >= 0
                                                                                ? TransactionType.ADJUSTMENT_IN
                                                                                : TransactionType.ADJUSTMENT_OUT)

                                                .quantity(quantity)

                                                .balanceAfter(balance)

                                                .referenceNumber("SYSTEM")

                                                .referenceType("ADJUSTMENT")

                                                .performedBy(null)

                                                .remarks("Manual stock adjustment")

                                                .transactionDate(LocalDateTime.now())

                                                .build());

                return inventory;
        }

        @Override
        public InventoryBin receiveStock(
                        Warehouse warehouse,
                        Bin bin,
                        Product product,
                        BigDecimal quantity,
                        String referenceNumber,
                        String referenceType,
                        String remarks,
                        User performedBy) {

                InventoryBin inventory = inventoryBinRepository
                                .findByWarehouseIdAndBinIdAndProductId(
                                                warehouse.getId(),
                                                bin.getId(),
                                                product.getId())
                                .orElseGet(() -> InventoryBin.builder()
                                                .warehouse(warehouse)
                                                .bin(bin)
                                                .product(product)
                                                .quantityOnHand(BigDecimal.ZERO)
                                                .quantityReserved(BigDecimal.ZERO)
                                                .build());

                inventory.setQuantityOnHand(
                                inventory.getQuantityOnHand().add(quantity));

                inventory = inventoryBinRepository.save(inventory);

                recordTransaction(
                                inventory,
                                TransactionType.GOODS_RECEIPT,
                                quantity,
                                referenceNumber,
                                referenceType,
                                null,
                                bin,
                                remarks,
                                performedBy);

                return inventory;
        }

        @Override
        public InventoryBin issueStock(
                        Warehouse warehouse,
                        Bin bin,
                        Product product,
                        BigDecimal quantity,
                        String referenceNumber,
                        String referenceType,
                        String remarks,
                        User performedBy) {

                InventoryBin inventory = inventoryBinRepository
                                .findByWarehouseIdAndBinIdAndProductId(
                                                warehouse.getId(),
                                                bin.getId(),
                                                product.getId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Inventory does not exist."));

                if (inventory.getQuantityOnHand().compareTo(quantity) < 0) {
                        throw new RuntimeException(
                                        "Insufficient stock available.");
                }

                inventory.setQuantityOnHand(
                                inventory.getQuantityOnHand().subtract(quantity));

                inventory = inventoryBinRepository.save(inventory);

                recordTransaction(
                                inventory,
                                TransactionType.SALE,
                                quantity.negate(),
                                referenceNumber,
                                referenceType,
                                bin,
                                null,
                                remarks,
                                performedBy);

                return inventory;
        }

        private void recordTransaction(
                        InventoryBin inventory,
                        TransactionType transactionType,
                        BigDecimal quantity,
                        String referenceNumber,
                        String referenceType,
                        Bin fromBin,
                        Bin toBin,
                        String remarks,
                        User performedBy) {

                InventoryTransaction transaction = InventoryTransaction.builder()

                                .inventoryBin(inventory)

                                .transactionType(transactionType)

                                .quantity(quantity)

                                .balanceAfter(
                                                inventory.getQuantityOnHand())

                                .referenceNumber(referenceNumber)

                                .referenceType(referenceType)

                                .performedBy(performedBy)

                                .fromBin(fromBin)

                                .toBin(toBin)

                                .remarks(remarks)

                                .transactionDate(LocalDateTime.now())

                                .build();

                inventoryTransactionRepository.save(transaction);
        }

        @Override
        public void moveStock(
                        Warehouse warehouse,
                        Bin fromBin,
                        Bin toBin,
                        Product product,
                        BigDecimal quantity,
                        String referenceNumber,
                        String referenceType,
                        String remarks,
                        User performedBy) {

                if (fromBin.getId().equals(toBin.getId())) {
                        throw new RuntimeException("Source and destination bins cannot be the same.");
                }

                InventoryBin sourceInventory = inventoryBinRepository
                                .findByWarehouseIdAndBinIdAndProductId(
                                                warehouse.getId(),
                                                fromBin.getId(),
                                                product.getId())
                                .orElseThrow(() -> new RuntimeException("Source inventory not found."));

                if (sourceInventory.getQuantityOnHand().compareTo(quantity) < 0) {
                        throw new RuntimeException("Insufficient stock in source bin.");
                }

                sourceInventory.setQuantityOnHand(
                                sourceInventory.getQuantityOnHand().subtract(quantity));

                inventoryBinRepository.save(sourceInventory);

                InventoryBin destinationInventory = inventoryBinRepository
                                .findByWarehouseIdAndBinIdAndProductId(
                                                warehouse.getId(),
                                                toBin.getId(),
                                                product.getId())
                                .orElseGet(() -> InventoryBin.builder()
                                                .warehouse(warehouse)
                                                .bin(toBin)
                                                .product(product)
                                                .quantityOnHand(BigDecimal.ZERO)
                                                .quantityReserved(BigDecimal.ZERO)
                                                .build());

                destinationInventory.setQuantityOnHand(
                                destinationInventory.getQuantityOnHand().add(quantity));

                destinationInventory = inventoryBinRepository.save(destinationInventory);

                recordTransaction(
                                sourceInventory,
                                TransactionType.TRANSFER_OUT,
                                quantity.negate(),
                                referenceNumber,
                                referenceType,
                                fromBin,
                                toBin,
                                remarks,
                                performedBy);

                recordTransaction(
                                destinationInventory,
                                TransactionType.TRANSFER_IN,
                                quantity,
                                referenceNumber,
                                referenceType,
                                fromBin,
                                toBin,
                                remarks,
                                performedBy);
        }

        @Override
        public InventoryBin reserveStock(
                        UUID inventoryBinId,
                        BigDecimal quantity) {

                InventoryBin inventory = findById(inventoryBinId);

                BigDecimal available = inventory.getQuantityOnHand()
                                .subtract(inventory.getQuantityReserved());

                if (available.compareTo(quantity) < 0) {
                        throw new RuntimeException("Insufficient available stock.");
                }

                inventory.setQuantityReserved(
                                inventory.getQuantityReserved().add(quantity));

                return inventoryBinRepository.save(inventory);
        }

        @Override
        public InventoryBin releaseReservation(
                        UUID inventoryBinId,
                        BigDecimal quantity) {

                InventoryBin inventory = findById(inventoryBinId);

                if (inventory.getQuantityReserved().compareTo(quantity) < 0) {
                        throw new RuntimeException("Reserved quantity cannot become negative.");
                }

                inventory.setQuantityReserved(
                                inventory.getQuantityReserved().subtract(quantity));

                return inventoryBinRepository.save(inventory);
        }

        @Override
        @Transactional(readOnly = true)
        public BigDecimal availableStock(UUID inventoryBinId) {

                InventoryBin inventory = findById(inventoryBinId);

                return inventory.getQuantityOnHand()
                                .subtract(inventory.getQuantityReserved());
        }

        @Override
        @Transactional(readOnly = true)
        public boolean inventoryExists(
                        UUID warehouseId,
                        UUID binId,
                        UUID productId) {

                return inventoryBinRepository
                                .existsByWarehouseIdAndBinIdAndProductId(
                                                warehouseId,
                                                binId,
                                                productId);
        }

        @Override
        @Transactional(readOnly = true)
        public InventoryBin getInventory(
                        UUID warehouseId,
                        UUID binId,
                        UUID productId) {

                return inventoryBinRepository
                                .findByWarehouseIdAndBinIdAndProductId(
                                                warehouseId,
                                                binId,
                                                productId)
                                .orElse(null);
        }

}