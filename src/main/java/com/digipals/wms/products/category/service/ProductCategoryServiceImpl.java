package com.digipals.wms.products.category.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.ProductCategoryMapper;
import com.digipals.wms.products.category.dto.CreateProductCategoryRequest;
import com.digipals.wms.products.category.dto.ProductCategoryResponse;
import com.digipals.wms.products.category.dto.UpdateProductCategoryRequest;
import com.digipals.wms.products.category.entity.ProductCategory;
import com.digipals.wms.products.category.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl
        implements ProductCategoryService {

    private final ProductCategoryRepository repository;

    private ProductCategory findCategory(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product category not found."));
    }

    @Override
    public ProductCategoryResponse create(
            CreateProductCategoryRequest request) {

        if (repository.existsByCode(request.getCode())) {

            throw new DuplicateResourceException(
                    "Category code already exists.");
        }

        ProductCategory category =
                ProductCategory.builder()

                        .code(request.getCode())

                        .name(request.getName())

                        .description(request.getDescription())

                        .active(request.getActive())

                        .build();

        category = repository.save(category);

        return ProductCategoryMapper.toResponse(category);
    }

    @Override
    public ProductCategoryResponse update(
            UUID id,
            UpdateProductCategoryRequest request) {

        ProductCategory category =
                findCategory(id);

        category.setName(request.getName());

        category.setDescription(request.getDescription());

        category.setActive(request.getActive());

        category = repository.save(category);

        return ProductCategoryMapper.toResponse(category);
    }

    @Override
    public ProductCategoryResponse findById(
            UUID id) {

        return ProductCategoryMapper.toResponse(
                findCategory(id));
    }

    @Override
    public ProductCategoryResponse findByCode(
            String code) {

        ProductCategory category =
                repository.findByCode(code)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product category not found."));

        return ProductCategoryMapper.toResponse(category);
    }

    @Override
    public List<ProductCategoryResponse> findAll() {

        return repository.findAll()

                .stream()

                .map(ProductCategoryMapper::toResponse)

                .toList();
    }

    @Override
    public List<ProductCategoryResponse> findActive() {

        return repository.findByActiveTrue()

                .stream()

                .map(ProductCategoryMapper::toResponse)

                .toList();
    }

    @Override
    public void delete(
            UUID id) {

        ProductCategory category =
                findCategory(id);

        repository.delete(category);
    }
}