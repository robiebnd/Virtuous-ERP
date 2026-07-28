package com.digipals.wms.supplier.service;

import com.digipals.wms.supplier.dto.CreateSupplierRequest;
import com.digipals.wms.supplier.dto.SupplierResponse;
import com.digipals.wms.supplier.dto.UpdateSupplierRequest;

import java.util.List;
import java.util.UUID;

public interface SupplierService {

    SupplierResponse create(
            CreateSupplierRequest request);

    SupplierResponse update(
            UUID id,
            UpdateSupplierRequest request);

    SupplierResponse findById(
            UUID id);

    SupplierResponse findByCode(
            String code);

    List<SupplierResponse> findAll();

    List<SupplierResponse> findActive();

    void delete(
            UUID id);
}