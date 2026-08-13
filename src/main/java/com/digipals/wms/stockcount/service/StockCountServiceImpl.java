package com.digipals.wms.stockcount.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.mapper.StockCountMapper;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentRepository;
import com.digipals.wms.stockadjustment.service.StockAdjustmentService;
import com.digipals.wms.stockcount.dto.CreateStockCountRequest;
import com.digipals.wms.stockcount.dto.StockCountResponse;
import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.stockcount.entity.StockCountLine;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import com.digipals.wms.stockcount.repository.StockCountLineRepository;
import com.digipals.wms.stockcount.repository.StockCountRepository;
import com.digipals.wms.stockcount.validation.StockCountValidator;
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
public class StockCountServiceImpl implements StockCountService {

    private final StockCountRepository repository;
    private final StockCountLineRepository lineRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryBinRepository inventoryBinRepository;
    private final DocumentNumberService documentNumberService;
    private final StockAdjustmentService stockAdjustmentService;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockCountValidator stockCountValidator;

    @Override
    public StockCountResponse create(CreateStockCountRequest request) {
        if (request.getWarehouseId() == null) {
            throw new RuntimeException("Warehouse is required.");
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        if (!Boolean.TRUE.equals(warehouse.getActive())) {
            throw new RuntimeException("Warehouse is inactive.");
        }

        StockCount stockCount = StockCount.builder()
                .countNumber(documentNumberService.next(DocumentType.STOCK_COUNT))
                .warehouse(warehouse)
                .remarks(request.getRemarks())
                .countDate(request.getCountDate() != null ? request.getCountDate() : LocalDateTime.now())
                .status(StockCountStatus.DRAFT)
                .build();

        stockCount = repository.save(stockCount);
        return StockCountMapper.toResponse(stockCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountResponse> findAll() {
        return repository.findAll().stream().map(StockCountMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockCountResponse findById(UUID id) {
        StockCount count = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));
        List<StockCountLine> lines = lineRepository.findByStockCountId(count.getId());
        return StockCountMapper.toResponse(count, lines);
    }

    @Override
    public StockCountResponse loadInventory(UUID id) {
        StockCount stockCount = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));

        if (stockCount.getStatus() != StockCountStatus.DRAFT) {
            throw new RuntimeException("Inventory can only be loaded for Draft Stock Counts.");
        }

        List<InventoryBin> inventories = inventoryBinRepository.findByWarehouseId(stockCount.getWarehouse().getId());
        if (inventories.isEmpty()) {
            throw new RuntimeException("No inventory found for warehouse.");
        }

        List<StockCountLine> linesToSave = new ArrayList<>();
        for (InventoryBin inventory : inventories) {
            boolean exists = lineRepository.existsByStockCountIdAndProductIdAndBinId(
                    stockCount.getId(), inventory.getProduct().getId(), inventory.getBin().getId());

            if (exists) {
                continue;
            }

            BigDecimal systemQuantity = inventory.getQuantityOnHand() == null
                    ? BigDecimal.ZERO : inventory.getQuantityOnHand();

            StockCountLine line = StockCountLine.builder()
                    .stockCount(stockCount)
                    .product(inventory.getProduct())
                    .bin(inventory.getBin())
                    .systemQuantity(systemQuantity)
                    // Null means the physical count has not yet been entered.
                    // Zero remains a valid physical count when explicitly entered.
                    .countedQuantity(null)
                    .variance(null)
                    .reason(null)
                    .build();

            linesToSave.add(line);
        }

        if (!linesToSave.isEmpty()) {
            lineRepository.saveAll(linesToSave);
        }

        stockCount.setStatus(StockCountStatus.COUNTING);
        stockCount = repository.save(stockCount);

        List<StockCountLine> allLines = lineRepository.findByStockCountId(stockCount.getId());
        return StockCountMapper.toResponse(stockCount, allLines);
    }

    @Override
    public StockCountResponse complete(UUID id) {
        StockCount stockCount = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));

        if (stockCount.getStatus() == StockCountStatus.COUNT_COMPLETED) {
            throw new RuntimeException("Stock Count has already been completed.");
        }
        if (stockCount.getStatus() == StockCountStatus.ADJUSTMENT_CREATED) {
            throw new RuntimeException("Stock Count has already generated a Stock Adjustment.");
        }
        if (stockCount.getStatus() != StockCountStatus.COUNTING) {
            throw new RuntimeException("Only Stock Counts in COUNTING status can be completed.");
        }

        List<StockCountLine> lines = lineRepository.findByStockCountId(stockCount.getId());
        if (lines.isEmpty()) {
            throw new RuntimeException("No Stock Count Lines found.");
        }

        for (StockCountLine line : lines) {
            if (line.getCountedQuantity() == null) {
                throw new RuntimeException("Counted Quantity is missing for product: " + line.getProduct().getName()
                        + " in bin: " + line.getBin().getCode());
            }
            if (line.getCountedQuantity().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Counted Quantity cannot be negative for product: "
                        + line.getProduct().getName() + " in bin: " + line.getBin().getCode());
            }
            line.setVariance(line.getCountedQuantity().subtract(line.getSystemQuantity()));
        }

        List<StockCountLine> updatedLines = lineRepository.saveAll(lines);
        stockCountValidator.validateCanComplete(stockCount);
        stockCount.setStatus(StockCountStatus.COUNT_COMPLETED);
        stockCount.setCompletedAt(LocalDateTime.now());
        stockCount = repository.save(stockCount);

        return StockCountMapper.toResponse(stockCount, updatedLines);
    }

    @Override
    @Transactional
    public StockCountResponse generateAdjustment(UUID id) {
        StockCount stockCount = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));

        stockCountValidator.validateCanGenerateAdjustment(stockCount);

        StockAdjustmentResponse adjustmentResponse = stockAdjustmentService.createFromStockCount(stockCount);
        StockAdjustment adjustment = stockAdjustmentRepository.findById(adjustmentResponse.getId())
                .orElseThrow(() -> new RuntimeException("Generated Stock Adjustment not found."));

        stockCount.setStockAdjustment(adjustment);
        stockCount.setStatus(StockCountStatus.ADJUSTMENT_CREATED);
        stockCount = repository.save(stockCount);

        List<StockCountLine> lines = lineRepository.findByStockCountId(stockCount.getId());
        return StockCountMapper.toResponse(stockCount, lines);
    }

    @Override
    public void delete(UUID id) {
        StockCount stockCount = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));

        if (stockCount.getStatus() != StockCountStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT Stock Counts can be deleted.");
        }

        repository.delete(stockCount);
    }
}
