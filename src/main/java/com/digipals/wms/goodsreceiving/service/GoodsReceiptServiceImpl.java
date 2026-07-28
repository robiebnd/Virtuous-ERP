package com.digipals.wms.goodsreceiving.service;


import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.GoodsReceiptMapper;
import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
import com.digipals.wms.inventory.repository.InventoryRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.inventorytransaction.repository.InventoryTransactionRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
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
public class GoodsReceiptServiceImpl
        implements GoodsReceiptService {

    private final GoodsReceiptRepository repository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final WarehouseRepository warehouseRepository;

    private final DocumentNumberService documentNumberService;

    private final CurrentUserService currentUserService;


    private final GoodsReceiptLineRepository goodsReceiptLineRepository;

    private final InventoryService inventoryService;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;



        private GoodsReceipt getGoodsReceipt(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Goods Receipt not found."));
    }

    private PurchaseOrder getPurchaseOrder(UUID id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase Order not found."));
    }

    private Warehouse getWarehouse(UUID id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Warehouse not found."));
    }

@Override
    public GoodsReceiptResponse create(
            CreateGoodsReceiptRequest request) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrder(
                        request.getPurchaseOrderId());

        if (purchaseOrder.getStatus()
                != PurchaseOrderStatus.APPROVED) {

            throw new InvalidWorkflowException(
                    "Only approved Purchase Orders can be received.");
        }

        Warehouse warehouse =
                getWarehouse(
                        request.getWarehouseId());

        User currentUser =
                currentUserService.getCurrentUser();

        GoodsReceipt goodsReceipt =
                GoodsReceipt.builder()

                        .grnNumber(
                                documentNumberService.next(
                                        DocumentType.GOODS_RECEIPT))

                        .purchaseOrder(
                                purchaseOrder)

                        .warehouse(
                                warehouse)

                        .receivedBy(
                                currentUser)

                        .status(
                                ReceiptStatus.DRAFT)

                        .supplierDeliveryNote(
                                request.getSupplierDeliveryNote())

                        .remarks(
                                request.getRemarks())

                        .receivedDate(
                                LocalDateTime.now())

                        .build();

        goodsReceipt =
                repository.save(
                        goodsReceipt);

        return GoodsReceiptMapper.toResponse(
                goodsReceipt);
    }

        @Override
    public GoodsReceiptResponse update(
            UUID id,
            UpdateGoodsReceiptRequest request) {

        GoodsReceipt goodsReceipt =
                getGoodsReceipt(id);

        if (goodsReceipt.getStatus()
                != ReceiptStatus.DRAFT) {

            throw new InvalidWorkflowException(
                    "Only draft Goods Receipts can be updated.");
        }

        Warehouse warehouse =
                getWarehouse(
                        request.getWarehouseId());

        goodsReceipt.setWarehouse(
                warehouse);

        goodsReceipt.setSupplierDeliveryNote(
                request.getSupplierDeliveryNote());

        goodsReceipt.setRemarks(
                request.getRemarks());

        goodsReceipt =
                repository.save(
                        goodsReceipt);

        return GoodsReceiptMapper.toResponse(
                goodsReceipt);
    }

 /*   @Override
public GoodsReceiptResponse approve(
        UUID id) {

    GoodsReceipt goodsReceipt =
            getGoodsReceipt(id);

    if (goodsReceipt.getStatus()
            != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Goods Receipts can be approved.");
    }

    goodsReceipt.setStatus(
            ReceiptStatus.APPROVED);

    goodsReceipt.setApprovedBy(
            currentUserService.getCurrentUser());

    goodsReceipt.setApprovedAt(
            LocalDateTime.now());

    goodsReceipt =
            repository.save(
                    goodsReceipt);

    return GoodsReceiptMapper.toResponse(
            goodsReceipt);
}*/
@Override
@Transactional(readOnly = true)
public List<GoodsReceiptResponse> findAll() {

    return repository.findAll()

            .stream()

            .map(GoodsReceiptMapper::toResponse)

            .toList();
}

@Override
public GoodsReceiptResponse approve(UUID id) {

    // Retrieve Goods Receipt
    GoodsReceipt goodsReceipt = getGoodsReceipt(id);

    // Validate status
    if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Goods Receipts can be approved.");
    }

    // Retrieve receipt lines
    List<GoodsReceiptLine> receiptLines =
            goodsReceiptLineRepository.findByGoodsReceiptId(
                    goodsReceipt.getId());

    if (receiptLines.isEmpty()) {

        throw new InvalidWorkflowException(
                "Cannot approve a Goods Receipt without any receipt lines.");
    }

    User currentUser =
            currentUserService.getCurrentUser();

    /*
     * Process every receipt line
     */
    for (GoodsReceiptLine receiptLine : receiptLines) {

        /*
         * 1. Update Inventory
         */
        inventoryService.receiveStock(

                goodsReceipt.getWarehouse(),

                receiptLine.getProduct(),

                receiptLine.getAcceptedQuantity(),

                goodsReceipt.getGrnNumber(),

                "GOODS_RECEIPT",

                receiptLine.getRemarks(),

                currentUser);

        /*
         * 2. Update Purchase Order Line
         */
        PurchaseOrderLine purchaseOrderLine =
                receiptLine.getPurchaseOrderLine();

        BigDecimal newReceivedQuantity =
                purchaseOrderLine.getReceivedQuantity()
                        .add(receiptLine.getAcceptedQuantity());

        purchaseOrderLine.setReceivedQuantity(
                newReceivedQuantity);

        BigDecimal outstanding =
                purchaseOrderLine.getQuantity()
                        .subtract(newReceivedQuantity);

        if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
            outstanding = BigDecimal.ZERO;
        }

        purchaseOrderLine.setOutstandingQuantity(
                outstanding);

        purchaseOrderLineRepository.save(
                purchaseOrderLine);
    }

    /*
     * 3. Refresh Purchase Order Status
     */
    updatePurchaseOrderReceiptStatus(
            goodsReceipt.getPurchaseOrder());

    /*
     * 4. Approve Goods Receipt
     */
    goodsReceipt.setStatus(
            ReceiptStatus.APPROVED);

    goodsReceipt.setApprovedBy(
            currentUser);

    goodsReceipt.setApprovedAt(
            LocalDateTime.now());

    goodsReceipt = repository.save(
            goodsReceipt);

    /*
     * 5. Return Response
     */
    return GoodsReceiptMapper.toResponse(
            goodsReceipt);
}


private void updatePurchaseOrderReceiptStatus(
        PurchaseOrder purchaseOrder) {

    List<PurchaseOrderLine> purchaseOrderLines =
            purchaseOrderLineRepository.findByPurchaseOrderId(
                    purchaseOrder.getId());

    boolean fullyReceived =
            purchaseOrderLines.stream()
                    .allMatch(line ->
                            line.getOutstandingQuantity()
                                    .compareTo(BigDecimal.ZERO) == 0);

    purchaseOrder.setStatus(
            fullyReceived
                    ? PurchaseOrderStatus.RECEIVED
                    : PurchaseOrderStatus.PARTIALLY_RECEIVED);

    purchaseOrderRepository.save(
            purchaseOrder);
}

@Override
@Transactional(readOnly = true)
public GoodsReceiptResponse findById(
        UUID id) {

    return GoodsReceiptMapper.toResponse(
            getGoodsReceipt(id));
}

@Override
@Transactional(readOnly = true)
public List<GoodsReceiptResponse> findByPurchaseOrder(
        UUID purchaseOrderId) {

    return repository.findByPurchaseOrderId(
                    purchaseOrderId)

            .stream()

            .map(GoodsReceiptMapper::toResponse)

            .toList();
}


@Override
public GoodsReceiptResponse loadPurchaseOrderLines(
        UUID goodsReceiptId) {

    GoodsReceipt goodsReceipt =
            getGoodsReceipt(goodsReceiptId);

    if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only Draft Goods Receipts can load Purchase Order Lines.");
    }

    PurchaseOrder purchaseOrder =
            goodsReceipt.getPurchaseOrder();

    List<PurchaseOrderLine> poLines =
            purchaseOrderLineRepository
                    .findByPurchaseOrderId(
                            purchaseOrder.getId());

    if (poLines.isEmpty()) {

        throw new ResourceNotFoundException(
                "Purchase Order contains no lines.");
    }

    /*
     * Prevent duplicate loading
     */
    if (!goodsReceiptLineRepository
            .findByGoodsReceiptId(goodsReceipt.getId())
            .isEmpty()) {

        throw new InvalidWorkflowException(
                "Goods Receipt Lines have already been loaded.");
    }

    for (PurchaseOrderLine poLine : poLines) {

        GoodsReceiptLine receiptLine =
                GoodsReceiptLine.builder()

                        .goodsReceipt(goodsReceipt)

                        .purchaseOrderLine(poLine)

                        .product(poLine.getProduct())

                        .orderedQuantity(
                                poLine.getQuantity())

                        .receivedQuantity(
                                poLine.getOutstandingQuantity())

                        .acceptedQuantity(
                                poLine.getOutstandingQuantity())

                        .rejectedQuantity(BigDecimal.ZERO)

                        .remarks("")

                        .build();

        goodsReceiptLineRepository.save(receiptLine);
    }

    return GoodsReceiptMapper.toResponse(goodsReceipt);
}
@Override
public void delete(
        UUID id) {

    GoodsReceipt goodsReceipt =
            getGoodsReceipt(id);

    if (goodsReceipt.getStatus()
            != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Goods Receipts can be deleted.");
    }

    repository.delete(
            goodsReceipt);

}


}