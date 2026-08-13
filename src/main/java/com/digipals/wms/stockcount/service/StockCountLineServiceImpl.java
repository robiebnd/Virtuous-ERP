package com.digipals.wms.stockcount.service;

import com.digipals.wms.common.mapper.StockCountLineMapper;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.stockcount.dto.CreateStockCountLineRequest;
import com.digipals.wms.stockcount.dto.StockCountLineResponse;
import com.digipals.wms.stockcount.dto.UpdateStockCountLineRequest;
import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.stockcount.entity.StockCountLine;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import com.digipals.wms.stockcount.repository.StockCountLineRepository;
import com.digipals.wms.stockcount.repository.StockCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCountLineServiceImpl implements StockCountLineService {

    private final StockCountLineRepository repository;
    private final StockCountRepository stockCountRepository;
    private final InventoryBinRepository inventoryBinRepository;

    @Override
    @Transactional
    public StockCountLineResponse updateCount(UUID lineId, UpdateStockCountLineRequest request) {
        StockCountLine line = repository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Stock Count Line not found"));

        if (line.getStockCount().getStatus() != StockCountStatus.COUNTING) {
            throw new RuntimeException("Only COUNTING Stock Counts can be updated.");
        }

        if (request.getCountedQuantity() == null) {
            throw new RuntimeException("Counted quantity is required.");
        }
        if (request.getCountedQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Counted quantity cannot be negative.");
        }

        line.setCountedQuantity(request.getCountedQuantity());
        line.setReason(request.getReason());
        line.setVariance(request.getCountedQuantity().subtract(line.getSystemQuantity()));

        repository.save(line);
        return StockCountLineMapper.toResponse(line);
    }

    @Override
    public StockCountLineResponse create(CreateStockCountLineRequest request) {
        if (request.getStockCountId() == null || request.getProductId() == null || request.getBinId() == null) {
            throw new RuntimeException("Stock Count, Product and Bin are required.");
        }
        if (request.getCountedQuantity() == null) {
            throw new RuntimeException("Counted quantity is required.");
        }
        if (request.getCountedQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Counted quantity cannot be negative.");
        }

        StockCount stockCount = stockCountRepository.findById(request.getStockCountId())
                .orElseThrow(() -> new RuntimeException("Stock Count not found"));

        if (stockCount.getStatus() != StockCountStatus.COUNTING) {
            throw new RuntimeException("Only COUNTING Stock Counts can have lines created.");
        }

        boolean exists = repository.existsByStockCountIdAndProductIdAndBinId(
                stockCount.getId(), request.getProductId(), request.getBinId());
        if (exists) {
            throw new RuntimeException("A Stock Count Line already exists for the selected product and bin.");
        }

        InventoryBin inventory = inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(
                        stockCount.getWarehouse().getId(), request.getBinId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for the selected warehouse, bin and product."));

        BigDecimal systemQuantity = inventory.getQuantityOnHand() == null
                ? BigDecimal.ZERO : inventory.getQuantityOnHand();

        StockCountLine line = StockCountLine.builder()
                .stockCount(stockCount)
                .product(inventory.getProduct())
                .bin(inventory.getBin())
                .systemQuantity(systemQuantity)
                .countedQuantity(request.getCountedQuantity())
                .variance(request.getCountedQuantity().subtract(systemQuantity))
                .reason(request.getReason())
                .build();

        line = repository.save(line);
        return StockCountLineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public StockCountLineResponse findById(UUID lineId) {
        return repository.findById(lineId)
                .map(StockCountLineMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Stock Count Line not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountLineResponse> findByStockCount(UUID stockCountId) {
        return repository.findByStockCountId(stockCountId)
                .stream()
                .map(StockCountLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountLineResponse> findAll() {
        return repository.findAll().stream()
                .map(StockCountLineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID lineId) {
        StockCountLine line = repository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Stock Count Line not found"));

        if (line.getStockCount().getStatus() != StockCountStatus.COUNTING) {
            throw new RuntimeException("Only COUNTING Stock Count Lines can be deleted.");
        }

        repository.delete(line);
    }
}
