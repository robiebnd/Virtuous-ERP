package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.entity.ProcurementSource;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository repository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRequisitionRepository purchaseRequisitionRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final PurchaseRequisitionLineRepository purchaseRequisitionLineRepository;
    private final DocumentNumberService documentNumberService;
    private final CurrentUserService currentUserService;
    private PurchaseOrder getPurchaseOrder(UUID id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found.")); }

    @Override
    public PurchaseOrder createFromRequisition(UUID purchaseRequisitionId) {
        PurchaseRequisition requisition = purchaseRequisitionRepository.findById(purchaseRequisitionId).orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        if (requisition.getStatus() != PurchaseRequisitionStatus.APPROVED) throw new InvalidWorkflowException("Only approved Purchase Requisitions can create Purchase Orders.");
        if (repository.existsByPurchaseRequisitionId(requisition.getId())) throw new InvalidWorkflowException("A Purchase Order already exists for this Purchase Requisition.");
        if (requisition.getSupplier() == null) throw new InvalidWorkflowException("Purchase Requisition has no supplier assigned and cannot create a Purchase Order.");
        String currency = normalizeCurrency(requisition.getCurrency());
        if (currency == null) throw new InvalidWorkflowException("Purchase Requisition currency is required before creating a Purchase Order.");
        List<PurchaseRequisitionLine> requisitionLines = purchaseRequisitionLineRepository.findByPurchaseRequisitionId(requisition.getId());
        if (requisitionLines.isEmpty()) throw new InvalidWorkflowException("Purchase Requisition has no lines and cannot create a Purchase Order.");
        for (PurchaseRequisitionLine line : requisitionLines) {
            if (line.getPurchasingInfoRecord() == null || line.getSourceSupplier() == null) throw new InvalidWorkflowException("Source of Supply is not assigned for product " + line.getProduct().getSku() + ". Determine and apply a Purchasing Info Record before creating the Purchase Order.");
            if (!requisition.getSupplier().getId().equals(line.getSourceSupplier().getId())) throw new InvalidWorkflowException("Source supplier for product " + line.getProduct().getSku() + " does not match the Purchase Requisition supplier.");
            if (line.getEstimatedUnitCost() == null || line.getEstimatedUnitCost().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("No valid source price is available for product " + line.getProduct().getSku() + ".");
        }
        PurchaseOrder purchaseOrder = PurchaseOrder.builder().poNumber(documentNumberService.next(DocumentType.PURCHASE_ORDER)).supplier(requisition.getSupplier()).warehouse(requisition.getWarehouse()).purchaseRequisition(requisition).source(ProcurementSource.REQUISITION).status(PurchaseOrderStatus.DRAFT).currency(currency).documentType(requisition.getDocumentType()).purchasingGroup(requisition.getPurchasingGroup()).orderDate(LocalDateTime.now()).createdBy(requisition.getRequestedBy()).outputStatus("NOT_SENT").build();
        purchaseOrder = repository.save(purchaseOrder);
        for (PurchaseRequisitionLine line : requisitionLines) {
            PurchaseOrderLine poLine = PurchaseOrderLine.builder().purchaseOrder(purchaseOrder).purchaseRequisitionLine(line).product(line.getProduct()).quantity(line.getQuantity()).unitPrice(line.getEstimatedUnitCost()).receivedQuantity(BigDecimal.ZERO).orderUnit(line.getUnitOfMeasure()).deliveryDate(line.getRequestedDeliveryDate()).storageLocation(line.getPurchaseRequisition().getStorageLocation()).itemCategory(line.getItemCategory()).build();
            purchaseOrderLineRepository.save(poLine); purchaseOrder.getLines().add(poLine);
        }
        requisition.setStatus(PurchaseRequisitionStatus.CONVERTED_TO_PO);
        purchaseRequisitionRepository.save(requisition);
        return purchaseOrder;
    }
    private String normalizeCurrency(String currency) { if (currency == null || currency.isBlank()) return null; String normalized = currency.trim().toUpperCase(); if (!normalized.matches("[A-Z]{3}")) throw new InvalidWorkflowException("Currency must be a 3-letter ISO currency code, e.g. USD."); return normalized; }
    @Override @Transactional(readOnly = true) public List<PurchaseOrder> findAll() { return repository.findAll(); }
    @Override @Transactional(readOnly = true) public PurchaseOrder findById(UUID id) { return getPurchaseOrder(id); }
    @Override public PurchaseOrder approve(UUID id) { PurchaseOrder po = getPurchaseOrder(id); if (po.getStatus() != PurchaseOrderStatus.DRAFT) throw new InvalidWorkflowException("Only draft Purchase Orders can be approved."); List<PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(po.getId()); if (lines.isEmpty()) throw new InvalidWorkflowException("Purchase Order cannot be approved without any lines."); if (po.getPurchaseRequisition() != null && po.getPurchaseRequisition().getStatus() != PurchaseRequisitionStatus.CONVERTED_TO_PO) throw new InvalidWorkflowException("Purchase Requisition must be converted to Purchase Order before approval."); for (PurchaseOrderLine line : lines) { if (line.getProduct() == null) throw new InvalidWorkflowException("Purchase Order contains a line without a product."); if (line.getQuantity() == null || line.getQuantity().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Purchase Order line quantity must be greater than zero."); if (line.getUnitPrice() == null || line.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Purchase Order line unit price must be greater than zero."); } po.setStatus(PurchaseOrderStatus.APPROVED); po.setApprovedBy(currentUserService.getCurrentUser()); po.setApprovedAt(LocalDateTime.now()); po.setOutputStatus("READY"); return repository.save(po); }
    @Override public PurchaseOrder receive(UUID id) { throw new InvalidWorkflowException("Purchase Order receiving is handled through Goods Receipt. Create and approve a Goods Receipt instead."); }
    @Override public PurchaseOrder update(UUID id, UpdatePurchaseOrderRequest request) { PurchaseOrder po = getPurchaseOrder(id); if (po.getStatus() != PurchaseOrderStatus.DRAFT) throw new InvalidWorkflowException("Only draft Purchase Orders can be updated."); if (po.getPurchaseRequisition() != null) throw new InvalidWorkflowException("Purchase Orders created from Purchase Requisitions cannot change supplier, warehouse, requisition or procurement source."); Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found.")); po.setSupplier(supplier); po.setSource(request.getSource()); return repository.save(po); }
}