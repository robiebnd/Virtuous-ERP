package com.digipals.wms.warehouse.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.warehouse.dto.CreateWarehouseRequest;
import com.digipals.wms.warehouse.dto.UpdateWarehouseRequest;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository repository;

public Warehouse create(CreateWarehouseRequest request) {
    // 1. Normalize the code to UPPERCASE to prevent duplicates like "wh01" and "WH01"
    String normalizedCode = request.getCode().trim().toUpperCase();

    // 2. Validate uniqueness before attempting an insert
    if (repository.existsByCode(normalizedCode)) {
        throw new IllegalArgumentException("Warehouse code '" + normalizedCode + "' already exists");
    }

    // 3. Build the initial read-only assignment
    Warehouse warehouse = Warehouse.builder()
            .code(normalizedCode) // Assigned once here, locked forever by the entity configuration
            .name(request.getName())
            .address(request.getAddress())
            .city(request.getCity())
            .country(request.getCountry())
            .active(true)
            .build();

    return repository.save(warehouse);
}

    public List<Warehouse> getAll() {
        return repository.findAll();
    }

    public Warehouse getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));
    }

  public Warehouse update(UUID id, UpdateWarehouseRequest request) {
    Warehouse warehouse = getById(id);

    // Since request.getCode() is removed from the DTO, 
    // there is no incoming code to check. It is naturally unchangeable here!

    warehouse.setName(request.getName());
    warehouse.setAddress(request.getAddress());
    warehouse.setCity(request.getCity());
    warehouse.setCountry(request.getCountry());

    if (request.getActive() != null) {
        warehouse.setActive(request.getActive());
    }

    return repository.save(warehouse);
    }
}