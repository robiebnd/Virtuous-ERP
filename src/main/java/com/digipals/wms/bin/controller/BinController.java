package com.digipals.wms.bin.controller;

import com.digipals.wms.bin.dto.BinResponse;
import com.digipals.wms.bin.dto.CreateBinRequest;
import com.digipals.wms.bin.service.BinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
public class BinController {

    private final BinService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BinResponse create(
            @Valid @RequestBody CreateBinRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<BinResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public BinResponse getById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<BinResponse> getByWarehouse(
            @PathVariable UUID warehouseId) {

        return service.findByWarehouse(warehouseId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}