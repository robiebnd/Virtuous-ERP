package com.digipals.wms.stockcount.service;

import com.digipals.wms.common.mapper.StockCountLineMapper;
import com.digipals.wms.stockcount.dto.CreateStockCountLineRequest;
import com.digipals.wms.stockcount.dto.StockCountLineResponse;
import com.digipals.wms.stockcount.entity.StockCountLine;
import com.digipals.wms.stockcount.entity.StockCountStatus;
import com.digipals.wms.stockcount.repository.StockCountLineRepository;
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

    @Override
    @Transactional
    public StockCountLineResponse updateCount(
            UUID lineId,
            CreateStockCountLineRequest request) {

        StockCountLine line = repository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Stock Count Line not found"));

        if (line.getStockCount().getStatus() != StockCountStatus.COUNTING) {
            throw new RuntimeException("Only COUNTING Stock Counts can be updated.");
        }

        line.setCountedQuantity(request.getCountedQuantity());
        line.setReason(request.getReason());

        // Calculate variance = countedQuantity - systemQuantity
        if (request.getCountedQuantity() != null && line.getSystemQuantity() != null) {
            line.setVariance(request.getCountedQuantity().subtract(line.getSystemQuantity()));
        } else {
            line.setVariance(null);
        }

        repository.save(line);

        return StockCountLineMapper.toResponse(line);
    }

    @Override
    public StockCountLineResponse create(CreateStockCountLineRequest request) {
        StockCountLine line = new StockCountLine();
        line.setCountedQuantity(request.getCountedQuantity());
        line.setReason(request.getReason());

        // Calculate variance if system quantity is present
        if (request.getCountedQuantity() != null && line.getSystemQuantity() != null) {
            line.setVariance(request.getCountedQuantity().subtract(line.getSystemQuantity()));
        }

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
        repository.deleteById(lineId);
    }
}