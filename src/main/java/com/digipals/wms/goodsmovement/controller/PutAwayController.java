package com.digipals.wms.goodsmovement.controller;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementLineRequest;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementRequest;
import com.digipals.wms.goodsmovement.dto.CreatePutAwayRequest;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import com.digipals.wms.goodsmovement.service.GoodsMovementService;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/goods-movements")
@RequiredArgsConstructor
public class PutAwayController {

    private final GoodsMovementService service;
    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final ProductRepository productRepository;

    @PostMapping("/put-away")
    public ResponseEntity<GoodsMovementResponse> createPutAway(
            @Valid @RequestBody CreatePutAwayRequest request) {

        Warehouse warehouse = warehouseRepository
                .findByCode(request.getWarehouseCode().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found: " + request.getWarehouseCode()));

        List<CreateGoodsMovementLineRequest> lines = new ArrayList<>();

        for (CreatePutAwayRequest.PutAwayLineRequest line : request.getLines()) {
            Product product = productRepository
                    .findBySkuIgnoreCase(line.getSku().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found for SKU: " + line.getSku()));

            Bin fromBin = binRepository
                    .findByWarehouseIdAndCode(warehouse.getId(), line.getFromBinCode().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Source bin not found: " + line.getFromBinCode()));

            Bin toBin = binRepository
                    .findByWarehouseIdAndCode(warehouse.getId(), line.getToBinCode().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Destination bin not found: " + line.getToBinCode()));

            lines.add(CreateGoodsMovementLineRequest.builder()
                    .productId(product.getId())
                    .fromBinId(fromBin.getId())
                    .toBinId(toBin.getId())
                    .quantity(line.getQuantity())
                    .unitCost(line.getUnitCost())
                    .remarks(line.getRemarks())
                    .build());
        }

        CreateGoodsMovementRequest movementRequest = CreateGoodsMovementRequest.builder()
                .movementType(GoodsMovementType.PUT_AWAY)
                .warehouseId(warehouse.getId())
                .referenceNumber(request.getReferenceNumber().trim())
                .referenceType(request.getReferenceType().trim())
                .remarks(request.getRemarks())
                .lines(lines)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(movementRequest));
    }
}
