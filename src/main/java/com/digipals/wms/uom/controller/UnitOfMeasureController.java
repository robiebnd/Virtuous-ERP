package com.digipals.wms.uom.controller;

import com.digipals.wms.uom.dto.CreateUnitOfMeasureRequest;
import com.digipals.wms.uom.dto.UnitOfMeasureResponse;
import com.digipals.wms.uom.dto.UpdateUnitOfMeasureRequest;
import com.digipals.wms.uom.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/uom")
@RequiredArgsConstructor
public class UnitOfMeasureController {

    private final UnitOfMeasureService service;

    @PostMapping
    public UnitOfMeasureResponse create(
            @Valid
            @RequestBody
            CreateUnitOfMeasureRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public UnitOfMeasureResponse update(
            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateUnitOfMeasureRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    public List<UnitOfMeasureResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public UnitOfMeasureResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/code/{code}")
    public UnitOfMeasureResponse findByCode(
            @PathVariable String code) {

        return service.findByCode(code);
    }

    @GetMapping("/active")
    public List<UnitOfMeasureResponse> findActive() {

        return service.findActive();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}