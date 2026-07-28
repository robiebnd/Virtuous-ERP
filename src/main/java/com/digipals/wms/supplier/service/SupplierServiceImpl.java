package com.digipals.wms.supplier.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.SupplierMapper;
import com.digipals.wms.supplier.dto.CreateSupplierRequest;
import com.digipals.wms.supplier.dto.SupplierResponse;
import com.digipals.wms.supplier.dto.UpdateSupplierRequest;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl
        implements SupplierService {

    private final SupplierRepository repository;

    private Supplier findSupplier(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found."));
    }

    @Override
    public SupplierResponse create(
            CreateSupplierRequest request) {

        if (repository.existsByCode(request.getCode())) {

            throw new DuplicateResourceException(
                    "Supplier code already exists.");
        }

        Supplier supplier =
                Supplier.builder()

                        .code(request.getCode())

                        .name(request.getName())

                        .contactPerson(request.getContactPerson())

                        .email(request.getEmail())

                        .phone(request.getPhone())

                        .address(request.getAddress())

                        .city(request.getCity())

                        .country(request.getCountry())

                        .active(request.getActive())

                        .build();

        supplier = repository.save(supplier);

        return SupplierMapper.toResponse(supplier);
    }

    @Override
    public SupplierResponse update(
            UUID id,
            UpdateSupplierRequest request) {

        Supplier supplier =
                findSupplier(id);

        supplier.setName(request.getName());

        supplier.setContactPerson(request.getContactPerson());

        supplier.setEmail(request.getEmail());

        supplier.setPhone(request.getPhone());

        supplier.setAddress(request.getAddress());

        supplier.setCity(request.getCity());

        supplier.setCountry(request.getCountry());

        supplier.setActive(request.getActive());

        supplier = repository.save(supplier);

        return SupplierMapper.toResponse(supplier);
    }

    @Override
    public SupplierResponse findById(
            UUID id) {

        return SupplierMapper.toResponse(
                findSupplier(id));
    }

    @Override
    public SupplierResponse findByCode(
            String code) {

        Supplier supplier =
                repository.findByCode(code)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Supplier not found."));

        return SupplierMapper.toResponse(supplier);
    }

    @Override
    public List<SupplierResponse> findAll() {

        return repository.findAll()

                .stream()

                .map(SupplierMapper::toResponse)

                .toList();
    }

    @Override
    public List<SupplierResponse> findActive() {

        return repository.findByActiveTrue()

                .stream()

                .map(SupplierMapper::toResponse)

                .toList();
    }

    @Override
    public void delete(
            UUID id) {

        Supplier supplier =
                findSupplier(id);

        repository.delete(supplier);
    }
}