package com.digipals.wms.dunning.controller;

import com.digipals.wms.dunning.dto.CreateDunningRequest;
import com.digipals.wms.dunning.dto.DunningCaseResponse;
import com.digipals.wms.dunning.entity.DunningCase;
import com.digipals.wms.dunning.service.DunningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dunning")
@RequiredArgsConstructor
public class DunningController {

    private final DunningService dunningService;

    @PostMapping
    public ResponseEntity<DunningCaseResponse> create(@Valid @RequestBody CreateDunningRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(dunningService.create(request)));
    }

    @PostMapping("/{id}/send")
    public DunningCaseResponse send(@PathVariable UUID id) {
        return toResponse(dunningService.send(id));
    }

    @PostMapping("/{id}/resolve")
    public DunningCaseResponse resolve(@PathVariable UUID id) {
        return toResponse(dunningService.resolve(id));
    }

    @GetMapping
    public List<DunningCaseResponse> findAll() {
        return dunningService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public DunningCaseResponse findById(@PathVariable UUID id) {
        return toResponse(dunningService.findById(id));
    }

    @GetMapping("/customer/{customerCode}")
    public List<DunningCaseResponse> findByCustomer(@PathVariable String customerCode) {
        return dunningService.findByCustomerCode(customerCode).stream().map(this::toResponse).toList();
    }

    private DunningCaseResponse toResponse(DunningCase d) {
        return new DunningCaseResponse(
                d.getId(), d.getDunningNumber(), d.getBillingDocument().getId(), d.getCustomerCode(),
                d.getCurrency(), d.getOutstandingAmount(), d.getDueDate(), d.getDunningDate(),
                d.getDunningLevel(), d.getStatus(), d.getMessage());
    }
}
