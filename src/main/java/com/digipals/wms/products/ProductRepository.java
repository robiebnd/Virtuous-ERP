package com.digipals.wms.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByUnitOfMeasureId(UUID unitOfMeasureId);

    Page<Product> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    Page<Product> findBySkuContainingIgnoreCase(
            String sku,
            Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String name,
            String sku,
            Pageable pageable);
}