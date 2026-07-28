package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaseorders.dto.CreatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;

import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;

import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl
        implements PurchaseOrderService {

    private final PurchaseOrderRepository repository;

    private final SupplierRepository supplierRepository;

    private final WarehouseRepository warehouseRepository;

    private final PurchaseRequisitionRepository purchaseRequisitionRepository;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final PurchaseRequisitionLineRepository purchaseRequisitionLineRepository;

    private final DocumentNumberService documentNumberService;

    private final CurrentUserService currentUserService;

   /*  @Override
    public PurchaseOrder create(
            CreatePurchaseOrderRequest request) {

        Supplier supplier =
                supplierRepository.findById(request.getSupplierId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Supplier not found."));

        Warehouse warehouse =
                warehouseRepository.findById(request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse not found."));

        PurchaseRequisition requisition = null;

        if (request.getPurchaseRequisitionId() != null) {

            requisition =
                    purchaseRequisitionRepository.findById(
                                    request.getPurchaseRequisitionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Purchase Requisition not found."));
        }

        User currentUser =
                currentUserService.getCurrentUser();

        PurchaseOrder purchaseOrder =
                PurchaseOrder.builder()

                        .poNumber(
                                documentNumberService.next(
                                        DocumentType.PURCHASE_ORDER))

                        .supplier(supplier)

                        .warehouse(warehouse)

                        .purchaseRequisition(requisition)

                        .source(request.getSource())

                        .status(PurchaseOrderStatus.DRAFT)

                        .createdBy(currentUser)

                        .build();

        return repository.save(purchaseOrder);
    }*/

@Override
public PurchaseOrder create(
        CreatePurchaseOrderRequest request) {

    Supplier supplier =
            supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Supplier not found."));

    Warehouse warehouse =
            warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Warehouse not found."));

    PurchaseRequisition requisition = null;

    if (request.getPurchaseRequisitionId() != null) {

        requisition =
                purchaseRequisitionRepository.findById(
                                request.getPurchaseRequisitionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase Requisition not found."));
    }

    User currentUser =
            currentUserService.getCurrentUser();

    PurchaseOrder purchaseOrder =
            PurchaseOrder.builder()

                    .poNumber(
                            documentNumberService.next(
                                    DocumentType.PURCHASE_ORDER))

                    .supplier(supplier)

                    .warehouse(warehouse)

                    .purchaseRequisition(requisition)

                    .source(request.getSource())

                    .status(PurchaseOrderStatus.DRAFT)

                    .createdBy(currentUser)

                    .build();

    purchaseOrder =
            repository.save(purchaseOrder);

    /*
     * Automatically create Purchase Order Lines
     * from Purchase Requisition Lines
     */
    if (requisition != null) {

        List<PurchaseRequisitionLine> requisitionLines =
                purchaseRequisitionLineRepository
                        .findByPurchaseRequisitionId(
                                requisition.getId());

        for (PurchaseRequisitionLine reqLine : requisitionLines) {

            PurchaseOrderLine poLine =
                    PurchaseOrderLine.builder()

                            .purchaseOrder(purchaseOrder)

                            .product(reqLine.getProduct())

                            .quantity(reqLine.getQuantity())

                            // Supplier quotation not yet entered
                            .unitPrice(BigDecimal.ZERO)

                            .receivedQuantity(BigDecimal.ZERO)

                            .build();

            purchaseOrderLineRepository.save(poLine);
        }
    }

    return purchaseOrder;
}



    @Override
    public List<PurchaseOrder> findAll() {

        return repository.findAll();
    }

    @Override
    public PurchaseOrder findById(
            UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase Order not found."));
    }

    @Override
    public PurchaseOrder approve(
            UUID id) {

        PurchaseOrder purchaseOrder =
                findById(id);

        if (purchaseOrder.getStatus()
                == PurchaseOrderStatus.RECEIVED) {

            throw new InvalidWorkflowException(
                    "A received Purchase Order cannot be approved.");
        }

        purchaseOrder.setStatus(
                PurchaseOrderStatus.APPROVED);

        purchaseOrder.setApprovedBy(
                currentUserService.getCurrentUser());

        return repository.save(
                purchaseOrder);
    }

    @Override
    public PurchaseOrder receive(
            UUID id) {

        PurchaseOrder purchaseOrder =
                findById(id);

        if (purchaseOrder.getStatus()
                != PurchaseOrderStatus.APPROVED) {

            throw new InvalidWorkflowException(
                    "Only approved Purchase Orders can be received.");
        }

        purchaseOrder.setStatus(
                PurchaseOrderStatus.RECEIVED);

        return repository.save(
                purchaseOrder);
    }

    @Override
public PurchaseOrder update(
        UUID id,
        UpdatePurchaseOrderRequest request) {

    PurchaseOrder purchaseOrder = findById(id);

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Purchase Orders can be updated.");
    }

    Supplier supplier =
            supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Supplier not found."));

    Warehouse warehouse =
            warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Warehouse not found."));

    PurchaseRequisition requisition = null;

    if (request.getPurchaseRequisitionId() != null) {

        requisition =
                purchaseRequisitionRepository.findById(
                                request.getPurchaseRequisitionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Purchase Requisition not found."));
    }

    purchaseOrder.setSupplier(supplier);

    purchaseOrder.setWarehouse(warehouse);

    purchaseOrder.setPurchaseRequisition(requisition);

    purchaseOrder.setSource(request.getSource());

    return repository.save(purchaseOrder);
}
}