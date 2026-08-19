package com.digipals.wms.productsupplieridentifier.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.productsupplieridentifier.dto.CreateProductSupplierIdentifierRequest;
import com.digipals.wms.productsupplieridentifier.dto.ProductSupplierIdentifierResponse;
import com.digipals.wms.productsupplieridentifier.dto.UpdateProductSupplierIdentifierRequest;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.productsupplieridentifier.repository.ProductSupplierIdentifierRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductSupplierIdentifierServiceImpl implements ProductSupplierIdentifierService {

    private final ProductSupplierIdentifierRepository repository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    }

    private Supplier findSupplier(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));
    }

    private ProductSupplierIdentifier findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product identifier not found."));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void validateUnique(UUID supplierId, String code, UUID currentId) {
        String normalized = normalize(code);
        boolean exists = currentId == null
                ? repository.existsBySupplierIdAndSupplierItemCodeIgnoreCase(supplierId, normalized)
                : repository.existsBySupplierIdAndSupplierItemCodeIgnoreCaseAndIdNot(supplierId, normalized, currentId);
        if (exists) {
            throw new DuplicateResourceException(
                    "Supplier item code '" + normalized + "' already exists for this supplier.");
        }
    }

    private ProductSupplierIdentifierResponse toResponse(ProductSupplierIdentifier entity) {
        Product product = entity.getProduct();
        Supplier supplier = entity.getSupplier();
        return ProductSupplierIdentifierResponse.builder()
                .id(entity.getId())
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .supplierId(supplier.getId())
                .supplierCode(supplier.getCode())
                .supplierName(supplier.getName())
                .supplierItemCode(entity.getSupplierItemCode())
                .supplierItemName(entity.getSupplierItemName())
                .active(entity.getActive())
                .build();
    }

    @Override
    public ProductSupplierIdentifierResponse create(CreateProductSupplierIdentifierRequest request) {
        String code = normalize(request.getSupplierItemCode());
        validateUnique(request.getSupplierId(), code, null);

        ProductSupplierIdentifier entity = ProductSupplierIdentifier.builder()
                .product(findProduct(request.getProductId()))
                .supplier(findSupplier(request.getSupplierId()))
                .supplierItemCode(code)
                .supplierItemName(normalize(request.getSupplierItemName()))
                .active(true)
                .build();

        return toResponse(repository.save(entity));
    }

    @Override
    public ProductSupplierIdentifierResponse update(UUID id, UpdateProductSupplierIdentifierRequest request) {
        ProductSupplierIdentifier entity = findEntity(id);
        String code = normalize(request.getSupplierItemCode());
        validateUnique(request.getSupplierId(), code, id);

        entity.setProduct(findProduct(request.getProductId()));
        entity.setSupplier(findSupplier(request.getSupplierId()));
        entity.setSupplierItemCode(code);
        entity.setSupplierItemName(normalize(request.getSupplierItemName()));
        entity.setActive(request.getActive());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSupplierIdentifierResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSupplierIdentifierResponse findBySupplierAndCode(UUID supplierId, String supplierItemCode) {
        return repository.findBySupplierIdAndSupplierItemCodeIgnoreCase(supplierId, normalize(supplierItemCode))
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product identifier not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSupplierIdentifierResponse findBySupplierCodeAndItemCode(String supplierCode, String supplierItemCode) {
        Supplier supplier = supplierRepository.findByCode(normalize(supplierCode))
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierCode));
        return findBySupplierAndCode(supplier.getId(), supplierItemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSupplierIdentifierResponse> findByProduct(UUID productId) {
        findProduct(productId);
        return repository.findByProductId(productId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSupplierIdentifierResponse> findBySupplier(UUID supplierId) {
        findSupplier(supplierId);
        return repository.findBySupplierId(supplierId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSupplierIdentifierResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }
}
