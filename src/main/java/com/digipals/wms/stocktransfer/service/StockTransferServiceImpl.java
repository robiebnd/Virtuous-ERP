package com.digipals.wms.stocktransfer.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.mapper.StockTransferMapper;
import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.stocktransfer.dto.CreateStockTransferRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferResponse;
import com.digipals.wms.stocktransfer.entity.StockTransfer;
import com.digipals.wms.stocktransfer.entity.StockTransferLine;
import com.digipals.wms.stocktransfer.entity.StockTransferStatus;
import com.digipals.wms.stocktransfer.repository.StockTransferLineRepository;
import com.digipals.wms.stocktransfer.repository.StockTransferRepository;
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
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository repository;
    private final StockTransferLineRepository lineRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final DocumentNumberService documentNumberService;
    private final CurrentUserService currentUserService;

    private BigDecimal getSafeQuantityOnHand(Inventory inventory) {
        return (inventory != null && inventory.getQuantityOnHand() != null)
                ? inventory.getQuantityOnHand()
                : BigDecimal.ZERO;
    }

    @Override
    public StockTransferResponse create(CreateStockTransferRequest request) {

        Warehouse source = warehouseRepository.findById(request.getSourceWarehouseId())
                .orElseThrow(() -> new RuntimeException("Source warehouse not found"));

        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        if (source.getId().equals(destination.getId())) {
            throw new RuntimeException("Source and destination warehouses cannot be the same.");
        }

        if (!Boolean.TRUE.equals(source.getActive())) {
            throw new RuntimeException("Source warehouse is inactive.");
        }

        if (!Boolean.TRUE.equals(destination.getActive())) {
            throw new RuntimeException("Destination warehouse is inactive.");
        }

        StockTransfer transfer = StockTransfer.builder()
                .transferNumber(documentNumberService.next(DocumentType.STOCK_TRANSFER))
                .sourceWarehouse(source)
                .destinationWarehouse(destination)
                .status(StockTransferStatus.DRAFT)
                .transferDate(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        transfer = repository.save(transfer);

        return StockTransferMapper.toResponse(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransferResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(StockTransferMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransferResponse findById(UUID id) {
        StockTransfer transfer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        return StockTransferMapper.toResponse(transfer);
    }

   @Override
public StockTransferResponse approve(UUID id) {

    StockTransfer transfer = repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Transfer not found"));

    if (transfer.getStatus() != StockTransferStatus.DRAFT) {

        throw new RuntimeException(
                "Only Draft transfers can be approved.");
    }

    List<StockTransferLine> lines =
            lineRepository.findByStockTransferId(
                    transfer.getId());

    if (lines.isEmpty()) {

        throw new RuntimeException(
                "Cannot approve a transfer with no lines.");
    }

    transfer.setStatus(
            StockTransferStatus.APPROVED);

    transfer.setApprovedAt(
            LocalDateTime.now());

    transfer.setApprovedBy(
            currentUserService.getCurrentUser());

   
 System.out.println("ApprovedAt = " + transfer.getApprovedAt());

    transfer =
            repository.save(transfer);

    return StockTransferMapper.toResponse(
            transfer);
}

   @Override
public StockTransferResponse issue(UUID id) {

    StockTransfer transfer =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Transfer not found"));

    if (transfer.getStatus() !=
            StockTransferStatus.APPROVED) {

        throw new RuntimeException(
                "Transfer must be APPROVED before issuing.");
    }

    List<StockTransferLine> lines =
            lineRepository.findByStockTransferId(
                    transfer.getId());

    if (lines.isEmpty()) {

        throw new RuntimeException(
                "Transfer contains no lines.");
    }

    for (StockTransferLine line : lines) {

        Inventory inventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                transfer.getSourceWarehouse().getId(),
                                line.getProduct().getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for "
                                                + line.getProduct().getName()));

        BigDecimal currentQty =
                getSafeQuantityOnHand(inventory);

        if (currentQty.compareTo(
                line.getQuantity()) < 0) {

            throw new RuntimeException(
                    "Insufficient stock for "
                            + line.getProduct().getName());
        }

        BigDecimal newQty =
                currentQty.subtract(
                        line.getQuantity());

        inventory.setQuantityOnHand(
                newQty);

        inventory =
                inventoryRepository.save(
                        inventory);

        InventoryTransaction transaction =
                InventoryTransaction.builder()

                        .inventory(inventory)

                        .transactionType(
                                TransactionType.TRANSFER_OUT)

                        // negative movement
                        .quantity(
                                line.getQuantity().negate())

                        .balanceAfter(
                                newQty)

                        .referenceNumber(
                                transfer.getTransferNumber())

                        .referenceType(
                                "STOCK_TRANSFER")

                        .performedBy(
                                currentUserService.getCurrentUser())

                        .transactionDate(
                                LocalDateTime.now())

                        .remarks(
                                "Transfer to "
                                        + transfer.getDestinationWarehouse().getCode())

                        .build();

        inventoryTransactionRepository.save(
                transaction);
    }

    transfer.setStatus(
            StockTransferStatus.ISSUED);

    transfer.setIssuedAt(
            LocalDateTime.now());

    transfer.setIssuedBy(
            currentUserService.getCurrentUser());

    transfer =
            repository.save(
                    transfer);

    return StockTransferMapper.toResponse(
            transfer);
}

//merge

   @Override
public StockTransferResponse receive(UUID id) {

    StockTransfer transfer =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Transfer not found"));

    if (transfer.getStatus() !=
            StockTransferStatus.ISSUED) {

        throw new RuntimeException(
                "Transfer must be ISSUED before receiving.");
    }

    List<StockTransferLine> lines =
            lineRepository.findByStockTransferId(
                    transfer.getId());

    if (lines.isEmpty()) {

        throw new RuntimeException(
                "Transfer contains no lines.");
    }

    for (StockTransferLine line : lines) {

        Inventory inventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                transfer.getDestinationWarehouse().getId(),
                                line.getProduct().getId())
                        .orElse(null);

        if (inventory == null) {

            inventory =
                    Inventory.builder()

                            .warehouse(
                                    transfer.getDestinationWarehouse())

                            .product(
                                    line.getProduct())

                            .quantityOnHand(
                                    BigDecimal.ZERO)

                            .quantityReserved(
                                    BigDecimal.ZERO)

                            .reorderLevel(
                                    BigDecimal.ZERO)

                            .build();

            inventory =
                    inventoryRepository.save(
                            inventory);
        }

        BigDecimal currentQty =
                getSafeQuantityOnHand(
                        inventory);

        BigDecimal newQty =
                currentQty.add(
                        line.getQuantity());

        inventory.setQuantityOnHand(
                newQty);

        inventory =
                inventoryRepository.save(
                        inventory);

        InventoryTransaction transaction =
                InventoryTransaction.builder()

                        .inventory(inventory)

                        .transactionType(
                                TransactionType.TRANSFER_IN)

                        .quantity(
                                line.getQuantity())

                        .balanceAfter(
                                newQty)

                        .referenceNumber(
                                transfer.getTransferNumber())

                        .referenceType(
                                "STOCK_TRANSFER")

                        .performedBy(
                                currentUserService.getCurrentUser())

                        .transactionDate(
                                LocalDateTime.now())

                        .remarks(
                                "Received from "
                                        + transfer.getSourceWarehouse().getCode())

                        .build();

        inventoryTransactionRepository.save(
                transaction);
    }

    transfer.setStatus(
            StockTransferStatus.RECEIVED);

    transfer.setReceivedAt(
            LocalDateTime.now());

    transfer.setReceivedBy(
            currentUserService.getCurrentUser());


  
    transfer =
            repository.save(
                    transfer);

    return StockTransferMapper.toResponse(
            transfer);
}
}