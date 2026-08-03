package com.digipals.wms.inventorybin.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.mapper.InventoryBinMapper;
import com.digipals.wms.inventorybin.dto.CreateInventoryBinRequest;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.dto.UpdateInventoryBinRequest;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryBinServiceImpl
        implements InventoryBinService {

    private final InventoryBinRepository repository;

    private final WarehouseRepository warehouseRepository;

    private final BinRepository binRepository;

    private final ProductRepository productRepository;

    @Override
    public InventoryBinResponse create(
            CreateInventoryBinRequest request) {

        Warehouse warehouse =
                warehouseRepository.findById(request.getWarehouseId())
                        .orElseThrow(() ->
                                new RuntimeException("Warehouse not found"));

        Bin bin =
                binRepository.findById(request.getBinId())
                        .orElseThrow(() ->
                                new RuntimeException("Bin not found"));

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException("Product not found"));

        if (repository.existsByWarehouseIdAndBinIdAndProductId(
                warehouse.getId(),
                bin.getId(),
                product.getId())) {

            throw new RuntimeException(
                    "Inventory already exists for this bin.");
        }

        InventoryBin inventory = InventoryBin.builder()
                .warehouse(warehouse)
                .bin(bin)
                .product(product)
                .quantityOnHand(
                        request.getQuantityOnHand() == null
                                ? BigDecimal.ZERO
                                : request.getQuantityOnHand())
                .quantityReserved(
                        request.getQuantityReserved() == null
                                ? BigDecimal.ZERO
                                : request.getQuantityReserved())
                .build();

        inventory = repository.save(inventory);

        return InventoryBinMapper.toResponse(inventory);
    }

    @Override
    public InventoryBinResponse update(
            UUID id,
            UpdateInventoryBinRequest request) {

        InventoryBin inventory =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Inventory Bin not found"));

        if (request.getQuantityOnHand() != null) {
            inventory.setQuantityOnHand(
                    request.getQuantityOnHand());
        }

        if (request.getQuantityReserved() != null) {
            inventory.setQuantityReserved(
                    request.getQuantityReserved());
        }

        inventory = repository.save(inventory);

        return InventoryBinMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryBinResponse findById(UUID id) {

        return InventoryBinMapper.toResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Inventory Bin not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBinResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(InventoryBinMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBinResponse> findByWarehouse(UUID warehouseId) {

        return repository.findByWarehouseId(warehouseId)
                .stream()
                .map(InventoryBinMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBinResponse> findByBin(UUID binId) {

        return repository.findByBinId(binId)
                .stream()
                .map(InventoryBinMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBinResponse> findByProduct(UUID productId) {

        return repository.findByProductId(productId)
                .stream()
                .map(InventoryBinMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {

        InventoryBin inventory =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Inventory Bin not found"));

        repository.delete(inventory);
    }
}
