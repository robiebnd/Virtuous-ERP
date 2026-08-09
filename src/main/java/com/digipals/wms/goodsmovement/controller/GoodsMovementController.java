package com.digipals.wms.goodsmovement.controller;

import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementRequest;
import com.digipals.wms.goodsmovement.dto.GoodsMovementLineResponse;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import com.digipals.wms.goodsmovement.service.GoodsMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goods-movements")
@RequiredArgsConstructor
public class GoodsMovementController {

    private final GoodsMovementService service;

    /*
     * ============================================================
     * CREATE GOODS MOVEMENT
     * ============================================================
     */

    @PostMapping
    public ResponseEntity<GoodsMovementResponse> create(
            @Valid @RequestBody CreateGoodsMovementRequest request) {

        GoodsMovementResponse response =
                service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * ============================================================
     * POST GOODS MOVEMENT
     * ============================================================
     *
     * Posting is what actually changes InventoryBin.
     */

    @PostMapping("/{id}/post")
    public ResponseEntity<GoodsMovementResponse> post(
            @PathVariable UUID id) {

        GoodsMovementResponse response =
                service.post(id);

        return ResponseEntity.ok(response);
    }

    /*
     * ============================================================
     * GET ALL
     * ============================================================
     */

    @GetMapping
    public ResponseEntity<List<GoodsMovementResponse>> findAll() {

        return ResponseEntity.ok(
                service.findAll());
    }

    /*
     * ============================================================
     * GET BY ID
     * ============================================================
     */

    @GetMapping("/{id}")
    public ResponseEntity<GoodsMovementResponse> findById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.findById(id));
    }

    /*
     * ============================================================
     * GET BY WAREHOUSE
     * ============================================================
     */

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<GoodsMovementResponse>>
    findByWarehouse(
            @PathVariable UUID warehouseId) {

        return ResponseEntity.ok(
                service.findByWarehouse(
                        warehouseId));
    }

    /*
     * ============================================================
     * GET BY MOVEMENT TYPE
     * ============================================================
     *
     * Example:
     *
     * /api/goods-movements/type/PUT_AWAY
     */

    @GetMapping("/type/{movementType}")
    public ResponseEntity<List<GoodsMovementResponse>>
    findByType(
            @PathVariable GoodsMovementType movementType) {

        return ResponseEntity.ok(
                service.findByType(
                        movementType));
    }

    /*
     * ============================================================
     * GET BY REFERENCE NUMBER
     * ============================================================
     *
     * Example:
     *
     * /api/goods-movements/reference/PA000001
     */

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<List<GoodsMovementResponse>>
    findByReferenceNumber(
            @PathVariable String referenceNumber) {

        return ResponseEntity.ok(
                service.findByReferenceNumber(
                        referenceNumber));
    }

    /*
     * ============================================================
     * GET MOVEMENT LINES
     * ============================================================
     */

    @GetMapping("/{id}/lines")
    public ResponseEntity<List<GoodsMovementLineResponse>>
    findLines(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.findLines(id));
    }

    /*
     * ============================================================
     * CANCEL
     * ============================================================
     */

    @PostMapping("/{id}/cancel")
    public ResponseEntity<GoodsMovementResponse> cancel(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.cancel(id));
    }
}
