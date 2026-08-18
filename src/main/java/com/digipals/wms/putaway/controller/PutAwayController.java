package com.digipals.wms.putaway.controller;

import com.digipals.wms.putaway.dto.AssignPutAwayRequest;
import com.digipals.wms.putaway.dto.CreatePutAwayRequest;
import com.digipals.wms.putaway.dto.PutAwayLineResponse;
import com.digipals.wms.putaway.dto.PutAwayResponse;
import com.digipals.wms.putaway.dto.UpdatePutAwayLineRequest;
import com.digipals.wms.putaway.dto.UpdatePutAwayRequest;
import com.digipals.wms.putaway.service.PutAwayAssignmentService;
import com.digipals.wms.putaway.service.PutAwayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/put-aways")
@RequiredArgsConstructor
public class PutAwayController {

    private final PutAwayService service;
    private final PutAwayAssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PutAwayResponse create(@Valid @RequestBody CreatePutAwayRequest request) {
        return service.create(request);
    }

    @PostMapping("/from-goods-receipt/number/{grnNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public PutAwayResponse createFromGoodsReceiptNumber(
            @PathVariable String grnNumber,
            @Valid @RequestBody CreatePutAwayRequest request) {
        return service.createFromGoodsReceiptNumber(grnNumber, request);
    }

    @GetMapping
    public List<PutAwayResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PutAwayResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/number/{putAwayNumber}")
    public PutAwayResponse findByNumber(@PathVariable String putAwayNumber) {
        return service.findByNumber(putAwayNumber);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<PutAwayResponse> findByWarehouse(@PathVariable UUID warehouseId) {
        return service.findByWarehouse(warehouseId);
    }

    @GetMapping("/goods-receipt/{goodsReceiptId}")
    public List<PutAwayResponse> findByGoodsReceipt(@PathVariable UUID goodsReceiptId) {
        return service.findByGoodsReceipt(goodsReceiptId);
    }

    @PutMapping("/{id}")
    public PutAwayResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePutAwayRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/assign")
    public PutAwayResponse assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPutAwayRequest request) {
        return assignmentService.assign(id, request);
    }

    @PostMapping("/lines/{lineId}/put-away")
    public PutAwayLineResponse putAwayLine(
            @PathVariable UUID lineId,
            @Valid @RequestBody UpdatePutAwayLineRequest request) {
        return service.putAwayLine(lineId, request);
    }

    @PostMapping("/number/{putAwayNumber}/lines/{lineId}/put-away")
    public PutAwayLineResponse putAwayLineByNumber(
            @PathVariable String putAwayNumber,
            @PathVariable UUID lineId,
            @Valid @RequestBody UpdatePutAwayLineRequest request) {
        PutAwayResponse putAway = service.findByNumber(putAwayNumber);
        boolean belongsToPutAway = putAway.getLines().stream()
                .anyMatch(line -> line.getId().equals(lineId));
        if (!belongsToPutAway) {
            throw new com.digipals.wms.common.exception.InvalidWorkflowException(
                    "Put-Away line does not belong to Put-Away " + putAwayNumber + ".");
        }
        return service.putAwayLine(lineId, request);
    }

    @GetMapping("/lines/{lineId}")
    public PutAwayLineResponse findLineById(@PathVariable UUID lineId) {
        return service.findLineById(lineId);
    }

    @GetMapping("/{putAwayId}/lines")
    public List<PutAwayLineResponse> findLinesByPutAway(@PathVariable UUID putAwayId) {
        return service.findLinesByPutAway(putAwayId);
    }

    @PostMapping("/{id}/cancel")
    public PutAwayResponse cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
