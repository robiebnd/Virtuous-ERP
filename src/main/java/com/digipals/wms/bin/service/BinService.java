package com.digipals.wms.bin.service;

import com.digipals.wms.bin.dto.CreateBinRequest;
import com.digipals.wms.bin.dto.BinResponse;

import java.util.List;
import java.util.UUID;

public interface BinService {

    BinResponse create(CreateBinRequest request);

    List<BinResponse> findAll();

    BinResponse findById(UUID id);

    List<BinResponse> findByWarehouse(UUID warehouseId);

    void delete(UUID id);
}