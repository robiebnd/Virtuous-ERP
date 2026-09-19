package com.digipals.wms.warehouse.controller;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.outbounddelivery.service.OutboundDeliveryService;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/warehouse-execution")
@RequiredArgsConstructor
public class WarehouseExecutionController {
    private final InventoryService inventoryService;
    private final InventoryBinRepository inventoryBins;
    private final WarehouseRepository warehouses;
    private final BinRepository bins;
    private final ProductRepository products;
    private final OutboundDeliveryService outboundDeliveries;

    @GetMapping("/inventory")
    public List<InventoryBin> inventory() {
        return inventoryService.findAll();
    }

    @GetMapping("/warehouses")
    public List<Warehouse> warehouses() {
        return warehouses.findByActive(true);
    }

    @GetMapping("/warehouses/{warehouseId}/bins")
    public List<Bin> bins(@PathVariable UUID warehouseId) {
        return bins.findByWarehouseId(warehouseId);
    }

    @GetMapping("/products")
    public List<Product> products() {
        return products.findByActiveTrue();
    }

    @PostMapping("/inventory/{id}/adjust")
    public InventoryBin adjust(@PathVariable UUID id, @RequestParam BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("Adjustment quantity cannot be zero.");
        return inventoryService.adjustStock(id, quantity);
    }

    @PostMapping("/inventory/{id}/reserve")
    public InventoryBin reserve(@PathVariable UUID id, @RequestParam BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Reservation quantity must be positive.");
        return inventoryService.reserveStock(id, quantity);
    }

    @PostMapping("/inventory/{id}/release")
    public InventoryBin release(@PathVariable UUID id, @RequestParam BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Release quantity must be positive.");
        return inventoryService.releaseReservation(id, quantity);
    }

    @PostMapping("/inventory/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        if (request.quantity() == null || request.quantity().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Transfer quantity must be positive.");
        Warehouse warehouse = warehouses.findById(request.warehouseId()).orElseThrow(() -> new IllegalArgumentException("Warehouse not found."));
        Bin from = bins.findById(request.fromBinId()).orElseThrow(() -> new IllegalArgumentException("Source bin not found."));
        Bin to = bins.findById(request.toBinId()).orElseThrow(() -> new IllegalArgumentException("Destination bin not found."));
        Product product = products.findById(request.productId()).orElseThrow(() -> new IllegalArgumentException("Product not found."));
        inventoryService.moveStock(warehouse, from, to, product, request.quantity(), "WM-" + System.currentTimeMillis(), "WAREHOUSE_TRANSFER", request.remarks(), null);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/{id}/start-picking")
    public Object startPicking(@PathVariable UUID id) { return outboundDeliveries.startPicking(id); }

    @PostMapping("/outbound/{id}/confirm-picking")
    public Object confirmPicking(@PathVariable UUID id) { return outboundDeliveries.confirmPicking(id); }

    @PostMapping("/outbound/{id}/confirm-packing")
    public Object confirmPacking(@PathVariable UUID id) { return outboundDeliveries.confirmPacking(id); }

    @PostMapping("/outbound/{id}/post-goods-issue")
    public Object postGoodsIssue(@PathVariable UUID id) { return outboundDeliveries.postGoodsIssue(id); }

    public record TransferRequest(UUID warehouseId, UUID fromBinId, UUID toBinId, UUID productId, BigDecimal quantity, String remarks) {}
}