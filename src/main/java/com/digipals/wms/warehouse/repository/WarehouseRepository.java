package com.digipals.wms.warehouse.repository;

import com.digipals.wms.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository
        extends JpaRepository<Warehouse, UUID> {

    boolean existsByCode(String code);

    Optional<Warehouse> findByCode(String code);

    Optional<Warehouse> findByName(String name);

    List<Warehouse> findByActive(Boolean active);

    List<Warehouse> findByCity(String city);

    List<Warehouse> findByCountry(String country);

    List<Warehouse> findByNameContainingIgnoreCase(String name);

    List<Warehouse> findByCityContainingIgnoreCase(String city);

    List<Warehouse> findByCountryContainingIgnoreCase(String country);

    long countByActive(Boolean active);
}