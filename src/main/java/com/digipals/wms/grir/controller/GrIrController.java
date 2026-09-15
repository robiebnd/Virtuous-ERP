package com.digipals.wms.grir.controller;

import com.digipals.wms.grir.dto.GrIrResponse;
import com.digipals.wms.grir.service.GrIrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/procurement/gr-ir")
@RequiredArgsConstructor
public class GrIrController {
    private final GrIrService grIrService;

    @GetMapping("/purchase-order/{purchaseOrderId}")
    public ResponseEntity<GrIrResponse> reconcile(@PathVariable UUID purchaseOrderId) {
        return ResponseEntity.ok(grIrService.reconcile(purchaseOrderId));
    }

    @PostMapping("/purchase-order/{purchaseOrderId}/close")
    public ResponseEntity<GrIrResponse> close(@PathVariable UUID purchaseOrderId) {
        return ResponseEntity.ok(grIrService.close(purchaseOrderId));
    }
}
