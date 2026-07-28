package com.digipals.wms.uom.service;

import com.digipals.wms.uom.dto.CreateUnitOfMeasureRequest;
import com.digipals.wms.uom.dto.UnitOfMeasureResponse;
import com.digipals.wms.uom.dto.UpdateUnitOfMeasureRequest;

import java.util.List;
import java.util.UUID;

public interface UnitOfMeasureService {

    UnitOfMeasureResponse create(
            CreateUnitOfMeasureRequest request);

    UnitOfMeasureResponse update(
            UUID id,
            UpdateUnitOfMeasureRequest request);

    UnitOfMeasureResponse findById(
            UUID id);

    UnitOfMeasureResponse findByCode(
            String code);

    List<UnitOfMeasureResponse> findAll();

    List<UnitOfMeasureResponse> findActive();

    void delete(
            UUID id);
}