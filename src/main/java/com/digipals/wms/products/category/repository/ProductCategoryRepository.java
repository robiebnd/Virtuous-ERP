package com.digipals.wms.products.category.repository;

import com.digipals.wms.products.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, UUID> {

    boolean existsByCode(String code);

    Optional<ProductCategory> findByCode(String code);

    List<ProductCategory> findByActiveTrue();
}