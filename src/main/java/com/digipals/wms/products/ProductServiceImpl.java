package com.digipals.wms.products;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.ProductMapper;
import com.digipals.wms.products.category.entity.ProductCategory;
import com.digipals.wms.products.category.repository.ProductCategoryRepository;
import com.digipals.wms.products.dto.CreateProductRequest;
import com.digipals.wms.products.dto.ProductResponse;
import com.digipals.wms.products.dto.UpdateProductRequest;
import com.digipals.wms.uom.entity.UnitOfMeasure;
import com.digipals.wms.uom.repository.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl
        implements ProductService {

    private final ProductRepository repository;

    private final ProductCategoryRepository categoryRepository;

    private final UnitOfMeasureRepository unitRepository;


    private Product findProduct(UUID id) {

    return repository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found."));
        }

        private ProductCategory findCategory(UUID id) {

         return categoryRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product category not found."));
}

private UnitOfMeasure findUnit(UUID id) {

    return unitRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Unit of Measure not found."));
}

@Override
public ProductResponse create(
        CreateProductRequest request) {

    if (repository.existsBySku(request.getSku())) {

        throw new DuplicateResourceException(
                "Product SKU already exists.");
    }

    ProductCategory category =
            findCategory(request.getCategoryId());

    UnitOfMeasure unit =
            findUnit(request.getUnitOfMeasureId());

    Product product =
            Product.builder()

                    .sku(request.getSku())

                    .name(request.getName())

                    .description(request.getDescription())

                    .costPrice(request.getCostPrice())

                    .sellingPrice(request.getSellingPrice())

                    .category(category)

                    .unitOfMeasure(unit)

                    .active(request.getActive())

                    .build();

    product = repository.save(product);

    return ProductMapper.toResponse(product);
}
@Override
public ProductResponse update(
        UUID id,
        UpdateProductRequest request) {

    Product product = findProduct(id);

    ProductCategory category =
            findCategory(request.getCategoryId());

    UnitOfMeasure unit =
            findUnit(request.getUnitOfMeasureId());

    product.setName(
            request.getName());

    product.setDescription(
            request.getDescription());

    product.setCostPrice(
            request.getCostPrice());

    product.setSellingPrice(
            request.getSellingPrice());

    product.setCategory(
            category);

    product.setUnitOfMeasure(
            unit);

    product.setActive(
            request.getActive());

    product = repository.save(product);

    return ProductMapper.toResponse(product);
}
@Override
public ProductResponse findById(
        UUID id) {

    return ProductMapper.toResponse(
            findProduct(id));
}
@Override
public ProductResponse findBySku(
        String sku) {

    Product product =
            repository.findBySku(sku)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found."));

    return ProductMapper.toResponse(
            product);
}
@Override
public List<ProductResponse> findAll() {

    return repository.findAll()

            .stream()

            .map(ProductMapper::toResponse)

            .toList();
}
@Override
public List<ProductResponse> findActive() {

    return repository.findByActiveTrue()

            .stream()

            .map(ProductMapper::toResponse)

            .toList();
}
@Override
public List<ProductResponse> findByCategory(
        UUID categoryId) {

    return repository.findByCategoryId(categoryId)

            .stream()

            .map(ProductMapper::toResponse)

            .toList();
}

@Override
public void delete(
        UUID id) {

    Product product =
            findProduct(id);

    repository.delete(product);
}


}