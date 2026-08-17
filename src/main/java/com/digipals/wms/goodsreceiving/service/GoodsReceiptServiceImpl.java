package com.digipals.wms.goodsreceiving.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
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
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.users.entity.User;
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
public class GoodsReceiptServiceImpl implements GoodsReceiptService {

    private final GoodsReceiptRepository repository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DocumentNumberService documentNumberService;
    private final CurrentUserService currentUserService;
    private final BinRepository binRepository;
    private final GoodsReceiptLineRepository goodsReceiptLineRepository;
    private final InventoryService inventoryService;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private GoodsReceipt getGoodsReceipt(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found."));
    }

    private GoodsReceipt getGoodsReceiptWithLines(UUID id) {
        return repository.findWithLinesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found."));
    }

    private GoodsReceipt getGoodsReceiptByNumber(String grnNumber) {
        return repository.findByGrnNumber(grnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found: " + grnNumber));
    }

    private GoodsReceipt getGoodsReceiptByNumberWithLines(String grnNumber) {
        return repository.findWithLinesByGrnNumber(grnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found: " + grnNumber));
    }

    private PurchaseOrder resolvePurchaseOrder(CreateGoodsReceiptRequest request) {
        PurchaseOrder byId = null;
        PurchaseOrder byNumber = null;

        if (request.getPurchaseOrderId() != null) {
            byId = getPurchaseOrder(request.getPurchaseOrderId());
        }

        if (request.getPurchaseOrderNumber() != null && !request.getPurchaseOrderNumber().isBlank()) {
            byNumber = purchaseOrderRepository.findByPoNumber(request.getPurchaseOrderNumber().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Purchase Order not found: " + request.getPurchaseOrderNumber()));
        }

        if (byId == null && byNumber == null) {
            throw new InvalidWorkflowException("Purchase Order ID or Purchase Order number is required.");
        }

        if (byId != null && byNumber != null && !byId.getId().equals(byNumber.getId())) {
            throw new InvalidWorkflowException("Purchase Order ID and Purchase Order number refer to different Purchase Orders.");
        }

        return byId != null ? byId : byNumber;
    }

    private PurchaseOrder getPurchaseOrder(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found."));
    }

    private Bin getReceivingBin(UUID warehouseId) {
        return binRepository.findByWarehouseIdAndReceivingBinTrue(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiving Bin not configured for the Purchase Order warehouse."));
    }

    @Override
    public GoodsReceiptResponse create(CreateGoodsReceiptRequest request) {
        PurchaseOrder purchaseOrder = resolvePurchaseOrder(request);

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.APPROVED
                && purchaseOrder.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new InvalidWorkflowException(
                    "Only approved or partially received Purchase Orders can be received.");
        }

        if (purchaseOrder.getWarehouse() == null) {
            throw new InvalidWorkflowException("Purchase Order has no warehouse assigned.");
        }

        List<PurchaseOrderLine> outstandingLines = purchaseOrderLineRepository
                .findByPurchaseOrderId(purchaseOrder.getId())
                .stream()
                .filter(line -> line.getOutstandingQuantity() != null
                        && line.getOutstandingQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (outstandingLines.isEmpty()) {
            throw new InvalidWorkflowException("Purchase Order has no outstanding quantities to receive.");
        }

        User currentUser = currentUserService.getCurrentUser();

        GoodsReceipt goodsReceipt = GoodsReceipt.builder()
                .grnNumber(documentNumberService.next(DocumentType.GOODS_RECEIPT))
                .purchaseOrder(purchaseOrder)
                .warehouse(purchaseOrder.getWarehouse())
                .receivedBy(currentUser)
                .status(ReceiptStatus.DRAFT)
                .supplierDeliveryNote(request.getSupplierDeliveryNote())
                .remarks(request.getRemarks())
                .receivedDate(LocalDateTime.now())
                .build();

        return GoodsReceiptMapper.toResponse(repository.save(goodsReceipt));
    }

    @Override
    public GoodsReceiptResponse update(UUID id, UpdateGoodsReceiptRequest request) {
        GoodsReceipt goodsReceipt = getGoodsReceipt(id);

        if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Goods Receipts can be updated.");
        }

        goodsReceipt.setSupplierDeliveryNote(request.getSupplierDeliveryNote());
        goodsReceipt.setRemarks(request.getRemarks());

        return GoodsReceiptMapper.toResponse(repository.save(goodsReceipt));
    }

    @Override
    public GoodsReceiptResponse approve(UUID id) {
        GoodsReceipt goodsReceipt = getGoodsReceipt(id);

        if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Goods Receipts can be approved.");
        }

        List<GoodsReceiptLine> receiptLines = goodsReceiptLineRepository
                .findByGoodsReceiptId(goodsReceipt.getId());

        if (receiptLines.isEmpty()) {
            throw new InvalidWorkflowException("Cannot approve a Goods Receipt without any receipt lines.");
        }

        User currentUser = currentUserService.getCurrentUser();
        Bin receivingBin = getReceivingBin(goodsReceipt.getWarehouse().getId());

        for (GoodsReceiptLine receiptLine : receiptLines) {
            validateReceiptLine(receiptLine);

            BigDecimal acceptedQuantity = nullSafe(receiptLine.getAcceptedQuantity());
            PurchaseOrderLine purchaseOrderLine = receiptLine.getPurchaseOrderLine();
            BigDecimal currentReceived = nullSafe(purchaseOrderLine.getReceivedQuantity());
            BigDecimal newReceivedQuantity = currentReceived.add(acceptedQuantity);

            if (newReceivedQuantity.compareTo(purchaseOrderLine.getQuantity()) > 0) {
                throw new InvalidWorkflowException(
                        "Accepted quantity would exceed the outstanding Purchase Order quantity for product "
                                + receiptLine.getProduct().getSku() + ".");
            }

            if (acceptedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                inventoryService.receiveStock(
                        goodsReceipt.getWarehouse(),
                        receivingBin,
                        receiptLine.getProduct(),
                        acceptedQuantity,
                        goodsReceipt.getGrnNumber(),
                        "GOODS_RECEIPT",
                        receiptLine.getRemarks(),
                        currentUser);
            }

            purchaseOrderLine.setReceivedQuantity(newReceivedQuantity);
            purchaseOrderLine.setOutstandingQuantity(
                    purchaseOrderLine.getQuantity().subtract(newReceivedQuantity));
            purchaseOrderLineRepository.save(purchaseOrderLine);
        }

        updatePurchaseOrderReceiptStatus(goodsReceipt.getPurchaseOrder());

        goodsReceipt.setStatus(ReceiptStatus.APPROVED);
        goodsReceipt.setApprovedBy(currentUser);
        goodsReceipt.setApprovedAt(LocalDateTime.now());

        return GoodsReceiptMapper.toResponse(repository.save(goodsReceipt));
    }

    private void validateReceiptLine(GoodsReceiptLine receiptLine) {
        if (receiptLine.getPurchaseOrderLine() == null) {
            throw new InvalidWorkflowException("Goods Receipt Line is not linked to a Purchase Order Line.");
        }

        BigDecimal received = nullSafe(receiptLine.getReceivedQuantity());
        BigDecimal accepted = nullSafe(receiptLine.getAcceptedQuantity());
        BigDecimal rejected = nullSafe(receiptLine.getRejectedQuantity());
        BigDecimal ordered = nullSafe(receiptLine.getOrderedQuantity());

        if (accepted.add(rejected).compareTo(received) != 0) {
            throw new InvalidWorkflowException(
                    "Accepted quantity + rejected quantity must equal received quantity.");
        }

        if (received.compareTo(ordered) > 0) {
            throw new InvalidWorkflowException(
                    "Received quantity cannot exceed the outstanding Purchase Order quantity.");
        }

        if (accepted.compareTo(received) > 0) {
            throw new InvalidWorkflowException(
                    "Accepted quantity cannot exceed received quantity.");
        }
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void updatePurchaseOrderReceiptStatus(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderLine> purchaseOrderLines = purchaseOrderLineRepository
                .findByPurchaseOrderId(purchaseOrder.getId());

        boolean fullyReceived = !purchaseOrderLines.isEmpty()
                && purchaseOrderLines.stream()
                .allMatch(line -> nullSafe(line.getOutstandingQuantity())
                        .compareTo(BigDecimal.ZERO) == 0);

        purchaseOrder.setStatus(
                fullyReceived
                        ? PurchaseOrderStatus.RECEIVED
                        : PurchaseOrderStatus.PARTIALLY_RECEIVED);

        purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptResponse> findAll() {
        return repository.findAll().stream()
                .map(GoodsReceiptMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceiptResponse findById(UUID id) {
        return GoodsReceiptMapper.toResponse(getGoodsReceiptWithLines(id));
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceiptResponse findByNumber(String grnNumber) {
        return GoodsReceiptMapper.toResponse(getGoodsReceiptByNumberWithLines(grnNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptResponse> findByPurchaseOrder(UUID purchaseOrderId) {
        return repository.findByPurchaseOrderId(purchaseOrderId).stream()
                .map(GoodsReceiptMapper::toResponse)
                .toList();
    }

    @Override
    public GoodsReceiptResponse loadPurchaseOrderLines(UUID goodsReceiptId) {
        GoodsReceipt goodsReceipt = getGoodsReceipt(goodsReceiptId);

        if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Goods Receipts can load Purchase Order Lines.");
        }

        PurchaseOrder purchaseOrder = goodsReceipt.getPurchaseOrder();
        List<PurchaseOrderLine> poLines = purchaseOrderLineRepository
                .findByPurchaseOrderId(purchaseOrder.getId())
                .stream()
                .filter(line -> line.getOutstandingQuantity() != null
                        && line.getOutstandingQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (poLines.isEmpty()) {
            throw new ResourceNotFoundException("Purchase Order has no outstanding lines to receive.");
        }

        if (goodsReceiptLineRepository.existsByGoodsReceiptId(goodsReceipt.getId())) {
            throw new InvalidWorkflowException("Goods Receipt Lines have already been loaded.");
        }

        for (PurchaseOrderLine poLine : poLines) {
            GoodsReceiptLine receiptLine = GoodsReceiptLine.builder()
                    .goodsReceipt(goodsReceipt)
                    .purchaseOrderLine(poLine)
                    .product(poLine.getProduct())
                    .orderedQuantity(poLine.getOutstandingQuantity())
                    .receivedQuantity(BigDecimal.ZERO)
                    .acceptedQuantity(BigDecimal.ZERO)
                    .rejectedQuantity(BigDecimal.ZERO)
                    .unitCost(poLine.getUnitPrice())
                    .remarks(null)
                    .build();

            goodsReceiptLineRepository.save(receiptLine);
        }

        // Re-query with an EntityGraph so the response is guaranteed to contain
        // the newly persisted lines, even though GoodsReceipt.lines is LAZY.
        GoodsReceipt reloadedGoodsReceipt = getGoodsReceiptWithLines(goodsReceipt.getId());
        return GoodsReceiptMapper.toResponse(reloadedGoodsReceipt);
    }

    @Override
    public void delete(UUID id) {
        GoodsReceipt goodsReceipt = getGoodsReceipt(id);

        if (goodsReceipt.getStatus() != ReceiptStatus.DRAFT) {
            throw new InvalidWorkflowException("Only draft Goods Receipts can be deleted.");
        }

        repository.delete(goodsReceipt);
    }
}
