package com.digipals.wms.stockadjustment.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.mapper.StockAdjustmentLineMapper;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
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
public class StockAdjustmentLineServiceImpl implements StockAdjustmentLineService {

    private final StockAdjustmentLineRepository repository;
    private final StockAdjustmentRepository adjustmentRepository;
    private final ProductRepository productRepository;
    private final BinRepository binRepository;
    private final InventoryBinRepository inventoryBinRepository;

    @Override
    public StockAdjustmentLineResponse create(CreateStockAdjustmentLineRequest request) {
        StockAdjustment adjustment = adjustmentRepository.findById(request.getStockAdjustmentId())
                .orElseThrow(() -> new RuntimeException("Stock Adjustment not found."));

        if (adjustment.getStatus() != AdjustmentStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT adjustments can be modified.");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found."));

        Bin bin = binRepository.findById(request.getBinId())
                .orElseThrow(() -> new RuntimeException("Bin not found."));

        if (bin.getWarehouse() == null || !adjustment.getWarehouse().getId().equals(bin.getWarehouse().getId())) {
            throw new RuntimeException("Bin does not belong to the adjustment warehouse.");
        }

        if (repository.existsByStockAdjustmentIdAndProductIdAndBinId(
                adjustment.getId(), product.getId(), bin.getId())) {
            throw new RuntimeException("Product already exists on this adjustment for the selected bin.");
        }

        InventoryBin inventory = inventoryBinRepository
                .findByWarehouseIdAndBinIdAndProductId(
                        adjustment.getWarehouse().getId(), bin.getId(), product.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Inventory not found for the selected warehouse, bin and product."));

        BigDecimal systemQty = inventory.getQuantityOnHand() == null
                ? BigDecimal.ZERO
                : inventory.getQuantityOnHand();
        BigDecimal countedQty = request.getCountedQuantity();
        BigDecimal difference = countedQty.subtract(systemQty);

        StockAdjustmentLine line = StockAdjustmentLine.builder()
                .stockAdjustment(adjustment)
                .product(product)
                .bin(bin)
                .systemQuantity(systemQty)
                .countedQuantity(countedQty)
                .difference(difference)
                .adjustmentQuantity(difference)
                .reason(request.getReason())
                .build();

        return StockAdjustmentLineMapper.toResponse(repository.save(line));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentLineResponse> findAll() {
        return repository.findAll().stream()
                .map(StockAdjustmentLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockAdjustmentLineResponse findById(UUID id) {
        StockAdjustmentLine line = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment Line not found."));
        return StockAdjustmentLineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentLineResponse> findByAdjustment(UUID adjustmentId) {
        return repository.findByStockAdjustmentId(adjustmentId).stream()
                .map(StockAdjustmentLineMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        StockAdjustmentLine line = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment Line not found."));

        if (line.getStockAdjustment().getStatus() != AdjustmentStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT adjustments can be modified.");
        }

        repository.delete(line);
    }
}
