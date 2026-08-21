package com.digipals.wms.inventory.controller;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.mapper.InventoryBinMapper;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;
    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final ProductRepository productRepository;

    @PostMapping
    public InventoryBinResponse create(@RequestBody InventoryBin inventoryBin) {
        return InventoryBinMapper.toResponse(service.create(inventoryBin));
    }

    @GetMapping
    public List<InventoryBinResponse> findAll() {
        return service.findAll().stream().map(InventoryBinMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public InventoryBinResponse findById(@PathVariable UUID id) {
        return InventoryBinMapper.toResponse(service.findById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryBinResponse> findByWarehouse(@PathVariable UUID warehouseId) {
        return service.findByWarehouse(warehouseId).stream().map(InventoryBinMapper::toResponse).toList();
    }

    @GetMapping("/product/{productId}")
    public List<InventoryBinResponse> findByProduct(@PathVariable UUID productId) {
        return service.findByProduct(productId).stream().map(InventoryBinMapper::toResponse).toList();
    }

    @PutMapping("/{id}/adjust")
    public InventoryBinResponse adjustStock(@PathVariable UUID id, @RequestParam BigDecimal quantity) {
        return InventoryBinMapper.toResponse(service.adjustStock(id, quantity));
    }

    @GetMapping("/warehouse/{warehouseId}/bin/{binId}/product/{productId}")
    public InventoryBinResponse getInventory(
            @PathVariable UUID warehouseId,
            @PathVariable UUID binId,
            @PathVariable UUID productId) {
        return InventoryBinMapper.toResponse(service.getInventory(warehouseId, binId, productId));
    }

    @GetMapping("/warehouse/{warehouseCode}/bin/{binCode}/product/{sku}")
    public InventoryBinResponse getInventoryByCodes(
            @PathVariable String warehouseCode,
            @PathVariable String binCode,
            @PathVariable String sku) {

        Warehouse warehouse = warehouseRepository.findByCode(warehouseCode.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Warehouse not found: " + warehouseCode));

        Bin bin = binRepository.findByWarehouseIdAndCode(warehouse.getId(), binCode.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bin not found in warehouse " + warehouseCode + ": " + binCode));

        Product product = productRepository.findBySkuIgnoreCase(sku.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product SKU not found: " + sku));

        return InventoryBinMapper.toResponse(
                service.getInventory(warehouse.getId(), bin.getId(), product.getId()));
    }

    @GetMapping("/{id}/available")
    public BigDecimal availableStock(@PathVariable UUID id) {
        return service.availableStock(id);
    }
}