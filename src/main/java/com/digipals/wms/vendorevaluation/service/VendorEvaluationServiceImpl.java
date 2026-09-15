package com.digipals.wms.vendorevaluation.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.vendorevaluation.dto.CreateVendorEvaluationRequest;
import com.digipals.wms.vendorevaluation.entity.VendorEvaluation;
import com.digipals.wms.vendorevaluation.repository.VendorEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorEvaluationServiceImpl implements VendorEvaluationService {
    private final VendorEvaluationRepository evaluationRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public VendorEvaluation create(CreateVendorEvaluationRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));
        PurchaseOrder po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found."));
        if (!po.getSupplier().getId().equals(supplier.getId())) {
            throw new IllegalArgumentException("Supplier does not match the Purchase Order supplier.");
        }
        if (evaluationRepository.existsByPurchaseOrderId(po.getId())) {
            throw new DuplicateResourceException("A vendor evaluation already exists for this Purchase Order.");
        }
        return evaluationRepository.save(VendorEvaluation.builder()
                .supplier(supplier).purchaseOrderId(po.getId()).purchaseOrderNumber(po.getPoNumber())
                .priceScore(request.getPriceScore()).qualityScore(request.getQualityScore())
                .deliveryScore(request.getDeliveryScore()).serviceScore(request.getServiceScore())
                .remarks(request.getRemarks()).build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorEvaluation> findBySupplier(UUID supplierId) {
        return evaluationRepository.findAllBySupplierIdOrderByEvaluationDateDesc(supplierId);
    }
}
