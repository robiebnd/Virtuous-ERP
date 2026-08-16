package com.digipals.wms.purchaserequisition.service;

import java.util.UUID;

public interface PurchaseRequisitionPdfService {

    byte[] generateById(UUID id);

    byte[] generateByNumber(String requisitionNumber);
}
