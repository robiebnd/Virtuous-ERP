package com.digipals.wms.goodsreceiving.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.GoodsReceiptLineMapper;
import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptLineRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptLineResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptLineRequest;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptLineServiceImpl implements GoodsReceiptLineService {

    private final GoodsReceiptLineRepository repository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private GoodsReceipt getGoodsReceipt(UUID id) {
        return goodsReceiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goods Receipt not found."));
    }

    private PurchaseOrderLine getPurchaseOrderLine(UUID id) {
        return purchaseOrderLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase Order Line not found."));
    }

    private GoodsReceiptLine getLine(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goods Receipt Line not found."));
    }

    @Override
    public GoodsReceiptLineResponse create(CreateGoodsReceiptLineRequest request) {

        GoodsReceipt goodsReceipt = getGoodsReceipt(request.getGoodsReceiptId());

        validateDraft(goodsReceipt);

        PurchaseOrderLine purchaseOrderLine =
                getPurchaseOrderLine(request.getPurchaseOrderLineId());

        validatePurchaseOrderLineBelongsToReceipt(
                goodsReceipt,
                purchaseOrderLine);

        if (repository.existsByGoodsReceiptIdAndProductId(
                goodsReceipt.getId(),
                purchaseOrderLine.getProduct().getId())) {
            throw new DuplicateResourceException(
                    "Product already exists on this Goods Receipt.");
        }

        BigDecimal outstanding = nullSafe(purchaseOrderLine.getOutstandingQuantity());
        BigDecimal received = nullSafe(request.getReceivedQuantity());
        BigDecimal accepted = nullSafe(request.getAcceptedQuantity());
        BigDecimal rejected = nullSafe(request.getRejectedQuantity());

        validateQuantities(received, accepted, rejected, outstanding);

        GoodsReceiptLine line = GoodsReceiptLine.builder()
                .goodsReceipt(goodsReceipt)
                .purchaseOrderLine(purchaseOrderLine)
                .product(purchaseOrderLine.getProduct())
                .orderedQuantity(outstanding)
                .receivedQuantity(received)
                .acceptedQuantity(accepted)
                .rejectedQuantity(rejected)
                .unitCost(purchaseOrderLine.getUnitPrice())
                .remarks(request.getRemarks())
                .build();

        return GoodsReceiptLineMapper.toResponse(repository.save(line));
    }

    @Override
    public GoodsReceiptLineResponse update(
            UUID id,
            UpdateGoodsReceiptLineRequest request) {

        GoodsReceiptLine line = getLine(id);
        GoodsReceipt receipt = line.getGoodsReceipt();

        validateDraft(receipt);

        BigDecimal received = nullSafe(request.getReceivedQuantity());
        BigDecimal accepted = nullSafe(request.getAcceptedQuantity());
        BigDecimal rejected = nullSafe(request.getRejectedQuantity());
        BigDecimal ordered = nullSafe(line.getOrderedQuantity());

        validateQuantities(received, accepted, rejected, ordered);

        line.setReceivedQuantity(received);
        line.setAcceptedQuantity(accepted);
        line.setRejectedQuantity(rejected);
        line.setRemarks(request.getRemarks());

        return GoodsReceiptLineMapper.toResponse(repository.save(line));
    }

    private void validateDraft(GoodsReceipt receipt) {
        if (receipt.getStatus() != ReceiptStatus.DRAFT) {
            throw new InvalidWorkflowException(
                    "Only draft Goods Receipts can be modified.");
        }
    }

    private void validatePurchaseOrderLineBelongsToReceipt(
            GoodsReceipt goodsReceipt,
            PurchaseOrderLine purchaseOrderLine) {

        if (purchaseOrderLine.getPurchaseOrder() == null
                || goodsReceipt.getPurchaseOrder() == null
                || !purchaseOrderLine.getPurchaseOrder().getId()
                        .equals(goodsReceipt.getPurchaseOrder().getId())) {
            throw new InvalidWorkflowException(
                    "Purchase Order Line does not belong to the Goods Receipt Purchase Order.");
        }

        if (purchaseOrderLine.getOutstandingQuantity() == null
                || purchaseOrderLine.getOutstandingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWorkflowException(
                    "Purchase Order Line has no outstanding quantity to receive.");
        }
    }

    private void validateQuantities(
            BigDecimal received,
            BigDecimal accepted,
            BigDecimal rejected,
            BigDecimal outstanding) {

        if (accepted.add(rejected).compareTo(received) != 0) {
            throw new InvalidWorkflowException(
                    "Accepted quantity + rejected quantity must equal received quantity.");
        }

        if (received.compareTo(outstanding) > 0) {
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

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptLineResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(GoodsReceiptLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceiptLineResponse findById(UUID id) {
        return GoodsReceiptLineMapper.toResponse(getLine(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceiptLineResponse> findByGoodsReceipt(UUID goodsReceiptId) {
        return repository.findByGoodsReceiptId(goodsReceiptId)
                .stream()
                .map(GoodsReceiptLineMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        GoodsReceiptLine line = getLine(id);
        validateDraft(line.getGoodsReceipt());
        repository.delete(line);
    }
}
