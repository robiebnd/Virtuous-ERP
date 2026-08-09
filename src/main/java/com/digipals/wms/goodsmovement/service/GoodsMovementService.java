package com.digipals.wms.goodsmovement.service;

import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementRequest;
import com.digipals.wms.goodsmovement.dto.GoodsMovementLineResponse;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;

import java.util.List;
import java.util.UUID;

public interface GoodsMovementService {

    GoodsMovementResponse create(
            CreateGoodsMovementRequest request);

    GoodsMovementResponse post(
            UUID id);

    GoodsMovementResponse findById(
            UUID id);

    List<GoodsMovementResponse> findAll();

    List<GoodsMovementResponse> findByWarehouse(
            UUID warehouseId);

    List<GoodsMovementResponse> findByType(
            GoodsMovementType movementType);

    List<GoodsMovementResponse> findByReferenceNumber(
            String referenceNumber);

    List<GoodsMovementLineResponse> findLines(
            UUID movementId);

    GoodsMovementResponse cancel(
            UUID id);
}
