package com.digipals.wms.grir.service;

import com.digipals.wms.grir.dto.GrIrResponse;

import java.util.UUID;

public interface GrIrService {
    GrIrResponse reconcile(UUID purchaseOrderId);
    GrIrResponse close(UUID purchaseOrderId);
}
