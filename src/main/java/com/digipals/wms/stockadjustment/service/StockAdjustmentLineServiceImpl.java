package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.common.mapper.StockAdjustmentLineMapper;
import com.digipals.wms.common.mapper.StockCountLineMapper;
import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.stockadjustment.dto.CreateStockAdjustmentLineRequest;
import com.digipals.wms.stockadjustment.dto.StockAdjustmentLineResponse;
import com.digipals.wms.stockadjustment.entity.AdjustmentStatus;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import com.digipals.wms.stockadjustment.entity.StockAdjustmentLine;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentLineRepository;
import com.digipals.wms.stockadjustment.repository.StockAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockAdjustmentLineServiceImpl
        implements StockAdjustmentLineService {

    private final StockAdjustmentLineRepository repository;

    private final StockAdjustmentRepository adjustmentRepository;

    private final ProductRepository productRepository;

    private final InventoryRepository inventoryRepository;


    @Override
    public StockAdjustmentLineResponse create(
            CreateStockAdjustmentLineRequest request) {

        StockAdjustment adjustment =
                adjustmentRepository.findById(
                        request.getStockAdjustmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock Adjustment not found"));

        if (adjustment.getStatus() !=
                AdjustmentStatus.DRAFT) {

            throw new RuntimeException(
                    "Only Draft adjustments can be modified.");
        }

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        if (repository.existsByStockAdjustmentIdAndProductId(
                adjustment.getId(),
                product.getId())) {

            throw new RuntimeException(
                    "Product already exists on this adjustment.");
        }

        Inventory inventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                adjustment.getWarehouse().getId(),
                                product.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found for "
                                                + product.getName()));

        BigDecimal systemQty =
                inventory.getQuantityOnHand();

        BigDecimal countedQty =
                request.getCountedQuantity();

        // Calculate variance / difference (e.g., 285 counted - 500 system = -215 difference)
        BigDecimal diff = countedQty.subtract(systemQty);

        StockAdjustmentLine line =
                StockAdjustmentLine.builder()
                        .stockAdjustment(adjustment)
                        .product(product)
                        .systemQuantity(systemQty)
                        .countedQuantity(countedQty)
                        .difference(diff)
                        // Populating DB audit columns to satisfy NOT NULL constraints
                        .adjustmentQuantity(diff)
                        .reason(request.getReason())
                        .build();

        line = repository.save(line);

        return StockAdjustmentLineMapper.toResponse(
                line);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentLineResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(StockAdjustmentLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockAdjustmentLineResponse findById(
            UUID id) {

        StockAdjustmentLine line =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adjustment Line not found"));

        return StockAdjustmentLineMapper.toResponse(
                line);
    }


    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentLineResponse> findByAdjustment(
            UUID adjustmentId) {

        return repository
                .findByStockAdjustmentId(
                        adjustmentId)
                .stream()
                .map(StockAdjustmentLineMapper::toResponse)
                .toList();
    }


    @Override
    public void delete(
            UUID id) {

        StockAdjustmentLine line =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adjustment Line not found"));

        if (line.getStockAdjustment().getStatus()
                != AdjustmentStatus.DRAFT) {

            throw new RuntimeException(
                    "Only Draft adjustments can be modified.");
        }

        repository.delete(line);
    }
}