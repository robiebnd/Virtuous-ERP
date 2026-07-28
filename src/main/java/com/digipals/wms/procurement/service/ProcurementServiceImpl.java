package com.digipals.wms.procurement.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PurchaseOrderMapper;
import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.procurement.validation.ProcurementValidator;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.*;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaserequisition.entity.*;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcurementServiceImpl
        implements ProcurementService {

    private final PurchaseRequisitionRepository requisitionRepository;

    private final PurchaseRequisitionLineRepository requisitionLineRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final SupplierRepository supplierRepository;

  

    private final DocumentNumberService documentNumberService;

    private final ProcurementValidator validator;
    
@Override
public PurchaseOrderResponse generatePurchaseOrder(
        GeneratePurchaseOrderRequest request) {

    PurchaseRequisition requisition =
            requisitionRepository
                    .findById(request.getPurchaseRequisitionId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Purchase Requisition not found."));

    validator.validateApproved(requisition);
    validator.validateNotConverted(requisition);

    Supplier supplier =
            supplierRepository
                    .findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Supplier not found."));

    List<PurchaseRequisitionLine> requisitionLines =
            requisitionLineRepository.findByPurchaseRequisitionId(
                    requisition.getId());

    if (requisitionLines.isEmpty()) {

        throw new ResourceNotFoundException(
                "Purchase Requisition contains no lines.");
    }

    PurchaseOrder purchaseOrder =
            PurchaseOrder.builder()

                    .poNumber(
                            documentNumberService.next(
                                    DocumentType.PURCHASE_ORDER))

                    .supplier(supplier)

                    .warehouse(
                            requisition.getWarehouse())

                    .purchaseRequisition(
                            requisition)

                    .source(
                            ProcurementSource.REQUISITION)

                    .status(
                            PurchaseOrderStatus.DRAFT)

                    .build();

    purchaseOrder =
            purchaseOrderRepository.save(
                    purchaseOrder);


    for (PurchaseRequisitionLine requisitionLine : requisitionLines) {

        PurchaseOrderLine orderLine =
                PurchaseOrderLine.builder()

                        .purchaseOrder(
                                purchaseOrder)

                        .product(
                                requisitionLine.getProduct())

                        .quantity(
                                requisitionLine.getQuantity())

                        .unitPrice(
                                BigDecimal.ZERO)

                        .build();

        purchaseOrderLineRepository.save(
                orderLine);
            }
    requisition.setStatus(
            PurchaseRequisitionStatus.CONVERTED_TO_PO);

    requisitionRepository.save(
            requisition);

    return PurchaseOrderMapper.toResponse(
            purchaseOrder);
        }


}
        