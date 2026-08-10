package com.digipals.wms.bin.service;

import com.digipals.wms.bin.dto.BinResponse;
import com.digipals.wms.bin.dto.CreateBinRequest;
import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.entity.BinType;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.mapper.BinMapper;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BinServiceImpl implements BinService {

    private final BinRepository repository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public BinResponse create(CreateBinRequest request) {

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found."));

        String code = request.getCode().trim().toUpperCase();

        if (repository.existsByWarehouseIdAndCode(
                warehouse.getId(),
                code)) {

            throw new RuntimeException(
                    "Bin code already exists in this warehouse.");
        }

        boolean receivingBin = request.getType() == BinType.RECEIVING;

        if (receivingBin
                && repository.findByWarehouseIdAndReceivingBinTrue(warehouse.getId()).isPresent()) {

            throw new RuntimeException(
                    "A Receiving Bin is already configured for this warehouse.");
        }

        Bin bin = Bin.builder()
                .warehouse(warehouse)
                .code(code)
                .name(request.getName().trim())
                .type(request.getType())
                .receivingBin(receivingBin)
                .capacity(request.getCapacity() == null
                        ? BigDecimal.ZERO
                        : request.getCapacity())
                .active(true)
                .build();

        return BinMapper.toResponse(
                repository.save(bin));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(BinMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BinResponse findById(UUID id) {

        Bin bin = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bin not found."));

        return BinMapper.toResponse(bin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinResponse> findByWarehouse(UUID warehouseId) {

        return repository.findByWarehouseId(warehouseId)
                .stream()
                .map(BinMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {

        Bin bin = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bin not found."));

        repository.delete(bin);
    }
}
