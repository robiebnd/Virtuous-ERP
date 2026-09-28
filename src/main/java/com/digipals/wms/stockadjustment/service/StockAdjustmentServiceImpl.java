package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.mapper.StockAdjustmentMapper;
import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.integration.IntegrationEventTypes;
import com.digipals.wms.integration.inventory.StockAdjustmentPostedEvent;
import com.digipals.wms.integration.outbox.IntegrationOutboxService;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockadjustment.entity.AdjustmentStatus;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import com.digipals.wms.stockadjustment.entity.StockAdjustmentLine;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentLineRepository;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentRepository;
import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.stockcount.entity.StockCountLine;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import com.digipals.wms.stockcount.repository.StockCountLineRepository;
import com.digipals.wms.stockcount.repository.StockCountRepository;
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
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

    private final StockAdjustmentRepository repository;
    private final StockAdjustmentLineRepository lineRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryBinRepository inventoryBinRepository;
    private final BinRepository binRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final DocumentNumberService documentNumberService;
    private final StockCountLineRepository stockCountLineRepository;
    private final StockCountRepository stockCountRepository;
    private final CurrentUserService currentUserService;
    private final IntegrationOutboxService outboxService;

    private InventoryBin getInventoryBin(UUID warehouseId, UUID binId, UUID productId) {
        return inventoryBinRepository.findByWarehouseIdAndBinIdAndProductId(
                        warehouseId, binId, productId)
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for the selected warehouse, bin and product."));
    }

    private BigDecimal getQuantityOnHand(InventoryBin inventoryBin) {
        return inventoryBin.getQuantityOnHand() == null
                ? BigDecimal.ZERO
                : inventoryBin.getQuantityOnHand();
    }

    private InventoryTransaction buildTransaction(
            InventoryBin inventoryBin,
            TransactionType transactionType,
            BigDecimal quantity,
            BigDecimal balanceAfter,
            String referenceNumber,
            String referenceType,
            String remarks) {

        Bin bin = inventoryBin.getBin();

        return InventoryTransaction.builder()
                .inventoryBin(inventoryBin)
                .transactionType(transactionType)
                .quantity(quantity)
                .balanceAfter(balanceAfter)
                .referenceNumber(referenceNumber)
                .referenceType(referenceType)
                .performedBy(currentUserService.getCurrentUser())
                .fromBin(quantity.compareTo(BigDecimal.ZERO) < 0 ? bin : null)
                .toBin(quantity.compareTo(BigDecimal.ZERO) > 0 ? bin : null)
                .remarks(remarks)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Override
    public StockAdjustmentResponse create(CreateStockAdjustmentRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found."));

        if (!Boolean.TRUE.equals(warehouse.getActive())) {
            throw new RuntimeException("Warehouse is inactive.");
        }

        StockAdjustment adjustment = StockAdjustment.builder()
                .adjustmentNumber(documentNumberService.next(DocumentType.STOCK_ADJUSTMENT))
                .warehouse(warehouse)
                .reason(request.getReason())
                .remarks(request.getRemarks())
                .status(AdjustmentStatus.DRAFT)
                .build();

        return StockAdjustmentMapper.toResponse(repository.save(adjustment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> findAll() {
        return repository.findAll().stream()
                .map(StockAdjustmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockAdjustmentResponse findById(UUID id) {
        StockAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Adjustment not found."));
        return StockAdjustmentMapper.toResponse(adjustment);
    }

    @Override
    public StockAdjustmentResponse approve(UUID id) {
        StockAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Adjustment not found."));

        if (adjustment.getStatus() != AdjustmentStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT adjustments can be approved.");
        }

        List<StockAdjustmentLine> lines = lineRepository.findByStockAdjustmentId(adjustment.getId());
        if (lines.isEmpty()) {
            throw new RuntimeException("Adjustment has no lines.");
        }

        adjustment.setStatus(AdjustmentStatus.APPROVED);
        return StockAdjustmentMapper.toResponse(repository.save(adjustment));
    }

    @Override
    @Transactional
    public StockAdjustmentResponse post(UUID id) {
        StockAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Adjustment not found."));

        if (adjustment.getStatus() != AdjustmentStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED adjustments can be posted.");
        }

        List<StockAdjustmentLine> lines = lineRepository.findByStockAdjustmentId(adjustment.getId());
        if (lines.isEmpty()) {
            throw new RuntimeException("Adjustment contains no lines.");
        }

        BigDecimal inventoryIncreaseValue = BigDecimal.ZERO;
        BigDecimal inventoryDecreaseValue = BigDecimal.ZERO;

        for (StockAdjustmentLine line : lines) {
            InventoryBin inventory = getInventoryBin(
                    adjustment.getWarehouse().getId(),
                    line.getBin().getId(),
                    line.getProduct().getId());

            BigDecimal oldQty = getQuantityOnHand(inventory);
            BigDecimal countedQty = line.getCountedQuantity();
            BigDecimal difference = countedQty.subtract(oldQty);

            if (countedQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Counted quantity cannot be negative.");
            }

            BigDecimal unitCost = line.getProduct().getCostPrice();
            if (difference.compareTo(BigDecimal.ZERO) != 0 && (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new InvalidWorkflowException("A positive product cost price is required for stock adjustment: " + line.getProduct().getSku());
            }

            BigDecimal valuation = difference.abs().multiply(unitCost == null ? BigDecimal.ZERO : unitCost).setScale(2, java.math.RoundingMode.HALF_UP);
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                inventoryIncreaseValue = inventoryIncreaseValue.add(valuation);
            } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
                inventoryDecreaseValue = inventoryDecreaseValue.add(valuation);
            }

            inventory.setQuantityOnHand(countedQty);
            inventory = inventoryBinRepository.save(inventory);

            TransactionType transactionType = difference.compareTo(BigDecimal.ZERO) >= 0
                    ? TransactionType.ADJUSTMENT_IN
                    : TransactionType.ADJUSTMENT_OUT;

            InventoryTransaction transaction = buildTransaction(
                    inventory,
                    transactionType,
                    difference,
                    countedQty,
                    adjustment.getAdjustmentNumber(),
                    "STOCK_ADJUSTMENT",
                    line.getReason() != null ? line.getReason() : adjustment.getReason());

            inventoryTransactionRepository.save(transaction);
        }

        adjustment.setPostedAt(LocalDateTime.now());
        adjustment.setStatus(AdjustmentStatus.POSTED);
        adjustment = repository.save(adjustment);

        if (inventoryIncreaseValue.compareTo(BigDecimal.ZERO) > 0 || inventoryDecreaseValue.compareTo(BigDecimal.ZERO) > 0) {
            outboxService.enqueue(
                    IntegrationEventTypes.INVENTORY_STOCK_ADJUSTMENT_POSTED,
                    "STOCK_ADJUSTMENT",
                    adjustment.getId(),
                    new StockAdjustmentPostedEvent(
                            adjustment.getId(),
                            adjustment.getAdjustmentNumber(),
                            "USD",
                            inventoryIncreaseValue,
                            inventoryDecreaseValue,
                            adjustment.getPostedAt()
                    ),
                    adjustment.getPostedAt()
            );
        }

        stockCountRepository.findByStockAdjustmentId(adjustment.getId()).ifPresent(count -> {
            count.setStatus(StockCountStatus.RECONCILED);
            count.setCompletedAt(LocalDateTime.now());
            stockCountRepository.save(count);
        });

        return StockAdjustmentMapper.toResponse(adjustment);
    }

    @Override
    public StockAdjustmentResponse postByAdjustmentNumber(String adjustmentNumber) {
        if (adjustmentNumber == null || adjustmentNumber.isBlank()) {
            throw new RuntimeException("Adjustment number is required.");
        }

        StockAdjustment adjustment = repository.findByAdjustmentNumber(adjustmentNumber.trim())
                .orElseThrow(() -> new RuntimeException(
                        "Stock Adjustment not found for number: " + adjustmentNumber));

        return post(adjustment.getId());
    }

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
                    .bin(countLine.getBin())
                    .systemQuantity(countLine.getSystemQuantity())
                    .countedQuantity(countLine.getCountedQuantity())
                    .difference(countLine.getVariance())
                    .adjustmentQuantity(countLine.getVariance())
                    .reason(countLine.getReason())
                    .build();
            linesToSave.add(line);
        }

        if (linesToSave.isEmpty()) {
            repository.delete(adjustment);
            throw new RuntimeException("No variances found to adjust; no Stock Adjustment Lines were created.");
        }

        lineRepository.saveAll(linesToSave);
        return StockAdjustmentMapper.toResponse(adjustment);
    }
}
