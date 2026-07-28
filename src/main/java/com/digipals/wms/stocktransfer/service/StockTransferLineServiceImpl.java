package com.digipals.wms.stocktransfer.service;

import com.digipals.wms.common.mapper.StockTransferLineMapper;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.stocktransfer.dto.CreateStockTransferLineRequest;
import com.digipals.wms.stocktransfer.dto.StockTransferLineResponse;
import com.digipals.wms.stocktransfer.entity.StockTransfer;
import com.digipals.wms.stocktransfer.entity.StockTransferLine;
import com.digipals.wms.stocktransfer.entity.StockTransferStatus;
import com.digipals.wms.stocktransfer.repository.StockTransferLineRepository;
import com.digipals.wms.stocktransfer.repository.StockTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockTransferLineServiceImpl implements StockTransferLineService {

    private final StockTransferLineRepository repository;

    private final StockTransferRepository transferRepository;

    private final ProductRepository productRepository;

    @Override
    public StockTransferLineResponse create(
            CreateStockTransferLineRequest request) {

        StockTransfer transfer =
                transferRepository.findById(
                        request.getStockTransferId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Stock Transfer not found"));

        if (transfer.getStatus() != StockTransferStatus.DRAFT) {

            throw new RuntimeException(
                    "Lines can only be added while the transfer is in DRAFT status.");
        }

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        StockTransferLine line =
                StockTransferLine.builder()
                        .stockTransfer(transfer)
                        .product(product)
                        .quantity(request.getQuantity())
                        .build();

        line = repository.save(line);

        return StockTransferLineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransferLineResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(StockTransferLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransferLineResponse findById(
            UUID id) {

        StockTransferLine line =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transfer Line not found"));

        return StockTransferLineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransferLineResponse> findByStockTransferId(
            UUID stockTransferId) {

        return repository.findByStockTransferId(
                        stockTransferId)
                .stream()
                .map(StockTransferLineMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {

        StockTransferLine line = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Transfer Line not found"));

        if (line.getStockTransfer().getStatus() != StockTransferStatus.DRAFT) {
            throw new RuntimeException(
                    "Lines can only be removed while the transfer is in DRAFT status.");
        }

        repository.delete(line);
    }
}