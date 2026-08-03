package com.digipals.wms.bintransfer.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.entity.BinStatus;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.bintransfer.dto.BinTransferResponse;
import com.digipals.wms.bintransfer.dto.CreateBinTransferRequest;


import com.digipals.wms.inventorybin.entity.InventoryBin;


import com.digipals.wms.bintransfer.entity.BinTransfer;
import com.digipals.wms.inventorybin.entity.*;
import com.digipals.wms.bintransfer.entity.BinTransferLine;
import com.digipals.wms.bintransfer.entity.BinTransferStatus;
import com.digipals.wms.bintransfer.repository.BinTransferLineRepository;
import com.digipals.wms.bintransfer.repository.BinTransferRepository;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.mapper.BinTransferMapper;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.security.CurrentUserService;
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
public class BinTransferServiceImpl
        implements BinTransferService {

    private final BinTransferRepository repository;

    private final BinTransferLineRepository lineRepository;

    private final WarehouseRepository warehouseRepository;

    private final BinRepository binRepository;

    private final InventoryBinRepository inventoryBinRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final DocumentNumberService documentNumberService;

    private final CurrentUserService currentUserService;

        @Override
    public BinTransferResponse create(
            CreateBinTransferRequest request) {

        Warehouse warehouse =
                warehouseRepository.findById(
                        request.getWarehouseId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Warehouse not found."));

        Bin fromBin =
                binRepository.findById(
                        request.getFromBinId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Source bin not found."));

        Bin toBin =
                binRepository.findById(
                        request.getToBinId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Destination bin not found."));
            if (fromBin.getId().equals(toBin.getId())) {

            throw new RuntimeException(
                    "Source and destination bins cannot be the same.");
        }

        if (!fromBin.getWarehouse().getId().equals(
                warehouse.getId())) {

            throw new RuntimeException(
                    "Source bin does not belong to warehouse.");
        }

        if (!toBin.getWarehouse().getId().equals(
                warehouse.getId())) {

            throw new RuntimeException(
                    "Destination bin does not belong to warehouse.");
        }

        if (fromBin.getStatus() != BinStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Source bin is not available.");
        }

        if (toBin.getStatus() != BinStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Destination bin is not available.");
        }


            BinTransfer transfer =
                BinTransfer.builder()

                        .transferNumber(
                                documentNumberService.next(
                                        DocumentType.BIN_TRANSFER))

                        .warehouse(
                                warehouse)

                        .fromBin(
                                fromBin)

                        .toBin(
                                toBin)

                        .status(
                                BinTransferStatus.DRAFT)

                        .transferDate(
                                LocalDateTime.now())

                        .remarks(
                                request.getRemarks())

                        .build();

        transfer =
                repository.save(
                        transfer);

        return BinTransferMapper.toResponse(
                transfer);
    }

        @Override
    @Transactional(readOnly = true)
    public List<BinTransferResponse> findAll() {

        return repository.findAll()

                .stream()

                .map(BinTransferMapper::toResponse)

                .toList();
    }

        @Override
    @Transactional(readOnly = true)
    public BinTransferResponse findById(
            UUID id) {

        BinTransfer transfer =
                repository.findById(id)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bin Transfer not found."));

        return BinTransferMapper.toResponse(
                transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinTransferResponse> findByWarehouse(
            UUID warehouseId) {

        return repository.findByWarehouseId(
                        warehouseId)

                .stream()

                .map(BinTransferMapper::toResponse)

                .toList();
    }

    private BinTransfer getTransfer(
            UUID id) {

        return repository.findById(id)

                .orElseThrow(() ->
                        new RuntimeException(
                                "Bin Transfer not found."));
    }

        private void validateDraft(
            BinTransfer transfer) {

        if (transfer.getStatus() !=
                BinTransferStatus.DRAFT) {

            throw new RuntimeException(
                    "Only Draft transfers can be modified.");
        }
    }

        private void validateApproved(
            BinTransfer transfer) {

        if (transfer.getStatus() !=
                BinTransferStatus.APPROVED) {

            throw new RuntimeException(
                    "Transfer must be APPROVED.");
        }
    }

    @Override
public BinTransferResponse approve(UUID id) {

    BinTransfer transfer = getTransfer(id);

    validateDraft(transfer);

   /*  long lineCount = lineRepository.countByBinTransferId(
            transfer.getId());

    if (lineCount == 0) {

        throw new RuntimeException(
                "Bin Transfer has no lines.");
    }*/
  
    validateHasLines(transfer);

    transfer.setStatus(
            BinTransferStatus.APPROVED);

    transfer.setApprovedAt(
            LocalDateTime.now());

    transfer.setApprovedBy(
            currentUserService.getCurrentUser());


    

    repository.save(
            transfer);

    return BinTransferMapper.toResponse(
            transfer);
}

@Override
public BinTransferResponse cancel(UUID id) {

    BinTransfer transfer = getTransfer(id);

    if (transfer.getStatus() == BinTransferStatus.POSTED) {

        throw new RuntimeException(
                "Posted Bin Transfers cannot be cancelled.");
    }

    if (transfer.getStatus() == BinTransferStatus.CANCELLED) {

        throw new RuntimeException(
                "Bin Transfer is already cancelled.");
    }

    transfer.setStatus(
            BinTransferStatus.CANCELLED);

    repository.save(
            transfer);

    return BinTransferMapper.toResponse(
            transfer);
}

private void validateNotCancelled(
        BinTransfer transfer) {

    if (transfer.getStatus()
            == BinTransferStatus.CANCELLED) {

        throw new RuntimeException(
                "Cancelled Bin Transfers cannot be modified.");
    }
}

private void validateNotPosted(
        BinTransfer transfer) {

    if (transfer.getStatus()
            == BinTransferStatus.POSTED) {

        throw new RuntimeException(
                "Posted Bin Transfers cannot be modified.");
    }
}

private void validateHasLines(
        BinTransfer transfer) {

    if (lineRepository.countByBinTransferId(
            transfer.getId()) == 0) {

        throw new RuntimeException(
                "Bin Transfer has no lines.");
    }
}

@Override
@Transactional
public BinTransferResponse post(UUID id) {

    BinTransfer transfer = getTransfer(id);

    validateApproved(transfer);

    List<BinTransferLine> lines =
            lineRepository.findByBinTransferId(transfer.getId());

    if (lines.isEmpty()) {
        throw new RuntimeException("Bin Transfer has no lines.");
    }

    for (BinTransferLine line : lines) {

        moveInventory(line, transfer);

    }

    transfer.setStatus(BinTransferStatus.POSTED);
    transfer.setPostedAt(LocalDateTime.now());
    transfer.setPostedBy(currentUserService.getCurrentUser());

    repository.save(transfer);

    return BinTransferMapper.toResponse(transfer);
}

private void moveInventory(
        BinTransferLine line,
        BinTransfer transfer) {

    InventoryBin sourceInventory =
            inventoryBinRepository
                    .findByWarehouseIdAndBinIdAndProductId(
                            transfer.getWarehouse().getId(),
                            transfer.getFromBin().getId(),
                            line.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Product not found in source bin."));

    if (sourceInventory.getQuantityOnHand()
            .compareTo(line.getQuantity()) < 0) {

        throw new RuntimeException(
                "Insufficient stock for product: "
                        + line.getProduct().getName());
    }

    InventoryBin destinationInventory =
            inventoryBinRepository
                    .findByWarehouseIdAndBinIdAndProductId(
                            transfer.getWarehouse().getId(),
                            transfer.getToBin().getId(),
                            line.getProduct().getId())
                    .orElseGet(() -> {

                        InventoryBin inventory =
                                InventoryBin.builder()
                                        .warehouse(transfer.getWarehouse())
                                        .bin(transfer.getToBin())
                                        .product(line.getProduct())
                                        .quantityOnHand(BigDecimal.ZERO)
                                        .quantityReserved(BigDecimal.ZERO)
                                        .build();

                        return inventoryBinRepository.save(inventory);
                    });

    // Deduct source
    sourceInventory.setQuantityOnHand(
            sourceInventory.getQuantityOnHand()
                    .subtract(line.getQuantity()));

    // Add destination
    destinationInventory.setQuantityOnHand(
            destinationInventory.getQuantityOnHand()
                    .add(line.getQuantity()));

    inventoryBinRepository.save(sourceInventory);
    inventoryBinRepository.save(destinationInventory);

    // Update Bin Capacity
    transfer.getFromBin().setUsedCapacity(
            transfer.getFromBin().getUsedCapacity()
                    .subtract(line.getQuantity()));

    transfer.getToBin().setUsedCapacity(
            transfer.getToBin().getUsedCapacity()
                    .add(line.getQuantity()));

    binRepository.save(transfer.getFromBin());
    binRepository.save(transfer.getToBin());

    // ===== TRANSFER OUT =====
    InventoryTransaction issue =
            InventoryTransaction.builder()
                    .inventoryBin(sourceInventory)
                    .transactionType(TransactionType.TRANSFER_OUT)
                    .quantity(line.getQuantity().negate())
                    .balanceAfter(sourceInventory.getQuantityOnHand())
                    .referenceNumber(transfer.getTransferNumber())
                    .referenceType("BIN_TRANSFER")
                    .performedBy(currentUserService.getCurrentUser())
                    .remarks("Bin Transfer OUT")
                    .transactionDate(LocalDateTime.now())
                    .fromBin(transfer.getFromBin())
                    .toBin(transfer.getToBin())
                    .build();

    inventoryTransactionRepository.save(issue);

    // ===== TRANSFER IN =====
    InventoryTransaction receipt =
            InventoryTransaction.builder()
                    .inventoryBin(destinationInventory)
                    .transactionType(TransactionType.TRANSFER_IN)
                    .quantity(line.getQuantity())
                    .balanceAfter(destinationInventory.getQuantityOnHand())
                    .referenceNumber(transfer.getTransferNumber())
                    .referenceType("BIN_TRANSFER")
                    .performedBy(currentUserService.getCurrentUser())
                    .remarks("Bin Transfer IN")
                    .transactionDate(LocalDateTime.now())
                    .fromBin(transfer.getFromBin())
                    .toBin(transfer.getToBin())
                    .build();

    inventoryTransactionRepository.save(receipt);
}

@Override
@Transactional
public void delete(UUID id) {

    BinTransfer transfer = getTransfer(id);

    validateDraft(transfer);

    lineRepository.deleteByBinTransferId(
            transfer.getId());

    repository.delete(transfer);
}

private void validateSourceAndDestination(
        Warehouse warehouse,
        Bin fromBin,
        Bin toBin) {

    if (fromBin.getId().equals(toBin.getId())) {
        throw new RuntimeException(
                "Source and destination bins cannot be the same.");
    }

    if (!fromBin.getWarehouse().getId().equals(warehouse.getId())) {
        throw new RuntimeException(
                "Source bin does not belong to the selected warehouse.");
    }

    if (!toBin.getWarehouse().getId().equals(warehouse.getId())) {
        throw new RuntimeException(
                "Destination bin does not belong to the selected warehouse.");
    }

    if (fromBin.getStatus() != BinStatus.AVAILABLE) {
        throw new RuntimeException(
                "Source bin is not available.");
    }

    if (toBin.getStatus() != BinStatus.AVAILABLE) {
        throw new RuntimeException(
                "Destination bin is not available.");
    }
}

private void validateSufficientStock(
        InventoryBin inventory,
        BigDecimal quantity,
        String productName) {

    if (inventory.getQuantityOnHand().compareTo(quantity) < 0) {

        throw new RuntimeException(
                "Insufficient stock for product: "
                        + productName);
    }
}

private InventoryBin getOrCreateDestinationInventory(
        BinTransfer transfer,
        BinTransferLine line) {

    return inventoryBinRepository
            .findByWarehouseIdAndBinIdAndProductId(
                    transfer.getWarehouse().getId(),
                    transfer.getToBin().getId(),
                    line.getProduct().getId())
            .orElseGet(() -> {

                InventoryBin inventory = InventoryBin.builder()
                        .warehouse(transfer.getWarehouse())
                        .bin(transfer.getToBin())
                        .product(line.getProduct())
                        .quantityOnHand(BigDecimal.ZERO)
                        .quantityReserved(BigDecimal.ZERO)
                        .build();

                return inventoryBinRepository.save(inventory);
            });
}

private void updateBinCapacity(
        Bin fromBin,
        Bin toBin,
        BigDecimal quantity) {

    fromBin.setUsedCapacity(
            fromBin.getUsedCapacity().subtract(quantity));

    toBin.setUsedCapacity(
            toBin.getUsedCapacity().add(quantity));

    binRepository.save(fromBin);
    binRepository.save(toBin);
}

private void saveInventory(
        InventoryBin source,
        InventoryBin destination) {

    inventoryBinRepository.save(source);
    inventoryBinRepository.save(destination);
}
}