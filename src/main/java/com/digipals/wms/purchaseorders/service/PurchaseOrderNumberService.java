package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderNumberService {
    private final PurchaseOrderRepository repository;
    private final CurrentUserService currentUserService;
    private final SupplierRepository supplierRepository;
    private final PurchaseRequisitionRepository purchaseRequisitionRepository;
    private final PurchaseOrderService service;

    @Transactional(readOnly = true)
    public PurchaseOrder findByNumber(String poNumber) {
        return getByNumber(poNumber);
    }

    public PurchaseOrder createFromRequisitionByNumber(String requisitionNumber) {
        if (requisitionNumber == null || requisitionNumber.isBlank()) {
            throw new IllegalArgumentException("Purchase Requisition number is required.");
        }

        PurchaseRequisition requisition = purchaseRequisitionRepository.findByRequisitionNumber(requisitionNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase Requisition not found: " + requisitionNumber));

        return service.createFromRequisition(requisition.getId());
    }

    public PurchaseOrder approveByNumber(String poNumber) {
        PurchaseOrder purchaseOrder = getByNumber(poNumber);
        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Purchase Orders can be approved.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
        purchaseOrder.setApprovedBy(currentUserService.getCurrentUser());
        purchaseOrder.setApprovedAt(LocalDateTime.now());
        return repository.save(purchaseOrder);
    }

    public PurchaseOrder updateByNumber(String poNumber, UpdatePurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = getByNumber(poNumber);

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Purchase Orders can be updated.");
        }
        if (purchaseOrder.getPurchaseRequisition() != null) {
            throw new InvalidWorkflowException(
                    "Purchase Orders created from Purchase Requisitions cannot change supplier, warehouse, requisition or procurement source.");
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setSource(request.getSource());
        return repository.save(purchaseOrder);
    }

    private PurchaseOrder getByNumber(String poNumber) {
        if (poNumber == null || poNumber.isBlank()) {
            throw new IllegalArgumentException("PO number is required.");
        }
        return repository.findByPoNumber(poNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found: " + poNumber));
    }
}
