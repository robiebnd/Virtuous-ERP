package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.common.mapper.StockAdjustmentMapper;
import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockadjustment.entity.AdjustmentStatus;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import com.digipals.wms.stockadjustment.entity.StockAdjustmentLine;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentLineRepository;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentRepository;
import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.stockcount.entity.StockCountLine;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import com.digipals.wms.stockcount.repository.StockCountLineRepository;
import com.digipals.wms.stockcount.repository.StockCountRepository;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class StockAdjustmentServiceImpl
                implements StockAdjustmentService {

        private final StockAdjustmentRepository repository;

        private final StockAdjustmentLineRepository lineRepository;

        private final WarehouseRepository warehouseRepository;

        private final InventoryRepository inventoryRepository;

        private final InventoryTransactionRepository inventoryTransactionRepository;

        private final DocumentNumberService documentNumberService;

        private final StockCountLineRepository stockCountLineRepository;

        private final StockCountRepository stockCountRepository;

        private final CurrentUserService currentUserService;

        @Override
        public StockAdjustmentResponse create(
                        CreateStockAdjustmentRequest request) {

                Warehouse warehouse = warehouseRepository.findById(
                                request.getWarehouseId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Warehouse not found"));

                if (!Boolean.TRUE.equals(
                                warehouse.getActive())) {

                        throw new RuntimeException(
                                        "Warehouse is inactive.");
                }

                StockAdjustment adjustment = StockAdjustment.builder()
                                .adjustmentNumber(
                                                documentNumberService.next(
                                                                DocumentType.STOCK_ADJUSTMENT))
                                .warehouse(warehouse)
                                .reason(request.getReason())
                                .remarks(request.getRemarks())
                                .status(
                                                AdjustmentStatus.DRAFT)
                                .build();

                adjustment = repository.save(
                                adjustment);

                return StockAdjustmentMapper.toResponse(
                                adjustment);
        }

        @Override
        @Transactional(readOnly = true)
        public List<StockAdjustmentResponse> findAll() {

                return repository.findAll()
                                .stream()
                                .map(StockAdjustmentMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public StockAdjustmentResponse findById(
                        UUID id) {

                StockAdjustment adjustment = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Stock Adjustment not found"));

                return StockAdjustmentMapper.toResponse(
                                adjustment);
        }

        @Override
        public StockAdjustmentResponse approve(
                        UUID id) {

                StockAdjustment adjustment = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Adjustment not found"));

                if (adjustment.getStatus() != AdjustmentStatus.DRAFT) {

                        throw new RuntimeException(
                                        "Only Draft adjustments can be approved.");
                }

                List<StockAdjustmentLine> lines = lineRepository.findByStockAdjustmentId(
                                adjustment.getId());

                if (lines.isEmpty()) {

                        throw new RuntimeException(
                                        "Adjustment has no lines.");
                }

                adjustment.setStatus(
                                AdjustmentStatus.APPROVED);

                adjustment = repository.save(
                                adjustment);

                return StockAdjustmentMapper.toResponse(
                                adjustment);
        }

      @Override
@Transactional
public StockAdjustmentResponse post(UUID id) {

    StockAdjustment adjustment = repository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                    "Stock Adjustment not found"));

    if (adjustment.getStatus() != AdjustmentStatus.APPROVED) {

        throw new RuntimeException(
                "Only APPROVED adjustments can be posted.");
    }

    List<StockAdjustmentLine> lines =
            lineRepository.findByStockAdjustmentId(
                    adjustment.getId());

    if (lines.isEmpty()) {

        throw new RuntimeException(
                "Adjustment contains no lines.");
    }

    for (StockAdjustmentLine line : lines) {

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(
                        adjustment.getWarehouse().getId(),
                        line.getProduct().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for "
                                + line.getProduct().getName()));

        // Current stock before adjustment
        BigDecimal oldQty = inventory.getQuantityOnHand();

        // Counted quantity
        BigDecimal newQty = line.getCountedQuantity();

        // Calculate variance
        BigDecimal difference = newQty.subtract(oldQty);

        // Update inventory
        inventory.setQuantityOnHand(newQty);

        inventory = inventoryRepository.save(inventory);

        // Record inventory transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(TransactionType.ADJUSTMENT)
                .quantity(difference)
                .balanceAfter(newQty)
                .referenceNumber(adjustment.getAdjustmentNumber())
                .referenceType("STOCK_ADJUSTMENT")
                .transactionDate(LocalDateTime.now())
                .performedBy(currentUserService.getCurrentUser())
                .remarks("Stock Adjustment")
                .build();

        inventoryTransactionRepository.save(transaction);
    }

    // Mark adjustment as posted
    adjustment.setPostedAt(LocalDateTime.now());
    adjustment.setStatus(AdjustmentStatus.POSTED);

    adjustment = repository.save(adjustment);

    // ---------------------------------------------------
    // COMPLETE THE RELATED STOCK COUNT
    // ---------------------------------------------------

    Optional<StockCount> stockCountOptional =
            stockCountRepository.findByStockAdjustmentId(adjustment.getId());

    if (stockCountOptional.isPresent()) {

        StockCount stockCount = stockCountOptional.get();

        stockCount.setStatus(StockCountStatus.RECONCILED);

        stockCount.setCompletedAt(LocalDateTime.now());

        stockCountRepository.save(stockCount);
    }

    return StockAdjustmentMapper.toResponse(adjustment);
}
        /*
         * @Override
         * public StockAdjustmentResponse createFromStockCount(
         * StockCount stockCount) {
         * 
         * if (stockCount.getStatus() !=
         * StockCountStatus.COMPLETED) {
         * 
         * throw new RuntimeException(
         * "Only COMPLETED Stock Counts can generate Stock Adjustments.");
         * }
         * 
         * List<StockCountLine> countLines =
         * stockCountLineRepository.findByStockCountId(
         * stockCount.getId());
         * 
         * if (countLines.isEmpty()) {
         * 
         * throw new RuntimeException(
         * "Stock Count contains no lines.");
         * }
         * 
         * StockAdjustment adjustment =
         * StockAdjustment.builder()
         * .adjustmentNumber(
         * documentNumberService.next(
         * DocumentType.STOCK_ADJUSTMENT))
         * .warehouse(
         * stockCount.getWarehouse())
         * .reason(
         * "Generated from Stock Count "
         * + stockCount.getCountNumber())
         * .remarks(
         * "Automatically generated from Stock Count")
         * .status(
         * AdjustmentStatus.DRAFT)
         * .build();
         * 
         * adjustment =
         * repository.save(adjustment);
         * 
         * int linesCreated = 0;
         * 
         * for (StockCountLine countLine : countLines) {
         * 
         * if (countLine.getVariance() == null) {
         * continue;
         * }
         * 
         * if (countLine.getVariance()
         * .compareTo(BigDecimal.ZERO) == 0) {
         * continue;
         * }
         * 
         * StockAdjustmentLine line =
         * StockAdjustmentLine.builder()
         * .stockAdjustment(adjustment)
         * .product(countLine.getProduct())
         * .systemQuantity(countLine.getSystemQuantity())
         * .countedQuantity(countLine.getCountedQuantity())
         * .difference(countLine.getVariance())
         * .build();
         * 
         * lineRepository.save(line);
         * 
         * linesCreated++;
         * }
         * 
         * if (linesCreated == 0) {
         * 
         * repository.delete(adjustment);
         * 
         * throw new RuntimeException(
         * "No Stock Adjustment Lines were created from the Stock Count.");
         * }
         * 
         * return StockAdjustmentMapper.toResponse(
         * adjustment);
         * }
         */

        @Override
        public StockAdjustmentResponse createFromStockCount(StockCount stockCount) {

                if (stockCount.getStatus() != StockCountStatus.COUNT_COMPLETED) {
                        throw new RuntimeException("Only COMPLETED Stock Counts can generate Stock Adjustments.");
                }

                List<StockCountLine> countLines = stockCountLineRepository.findByStockCountId(stockCount.getId());

                if (countLines.isEmpty()) {
                        throw new RuntimeException("Stock Count contains no lines.");
                }

                StockAdjustment adjustment = StockAdjustment.builder()
                                .adjustmentNumber(documentNumberService.next(DocumentType.STOCK_ADJUSTMENT))
                                .warehouse(stockCount.getWarehouse())
                                .reason("Generated from Stock Count " + stockCount.getCountNumber())
                                .remarks("Automatically generated from Stock Count")
                                .status(AdjustmentStatus.DRAFT)
                                .build();

                adjustment = repository.save(adjustment);

                List<StockAdjustmentLine> linesToSave = new ArrayList<>();

                for (StockCountLine countLine : countLines) {
                        if (countLine.getVariance() == null
                                        || countLine.getVariance().compareTo(BigDecimal.ZERO) == 0) {
                                continue;
                        }

                        StockAdjustmentLine line = StockAdjustmentLine.builder()
                                        .stockAdjustment(adjustment)
                                        .product(countLine.getProduct())
                                        .systemQuantity(countLine.getSystemQuantity())
                                        .countedQuantity(countLine.getCountedQuantity())
                                        .difference(countLine.getVariance())
                                        .adjustmentQuantity(countLine.getVariance()) // Fixes the NOT-NULL database
                                                                                     // constraint error
                                        .reason(countLine.getReason())
                                        .build();

                        linesToSave.add(line);
                }

                if (linesToSave.isEmpty()) {
                        repository.delete(adjustment);
                        throw new RuntimeException(
                                        "No variances found to adjust; no Stock Adjustment Lines were created.");
                }

                lineRepository.saveAll(linesToSave);

                return StockAdjustmentMapper.toResponse(adjustment);
        }

}