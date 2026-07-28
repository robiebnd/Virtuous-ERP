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
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
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
public class GoodsReceiptLineServiceImpl
        implements GoodsReceiptLineService {

    private final GoodsReceiptLineRepository repository;

    private final GoodsReceiptRepository goodsReceiptRepository;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final ProductRepository productRepository;

    private GoodsReceipt getGoodsReceipt(
            UUID id) {

        return goodsReceiptRepository.findById(id)

                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Goods Receipt not found."));
    }

    private PurchaseOrderLine getPurchaseOrderLine(
            UUID id) {

        return purchaseOrderLineRepository.findById(id)

                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Purchase Order Line not found."));
    }

    private Product getProduct(
            UUID id) {

        return productRepository.findById(id)

                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Product not found."));
    }

    private GoodsReceiptLine getLine(
            UUID id) {

        return repository.findById(id)

                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Goods Receipt Line not found."));
    }
@Override
public GoodsReceiptLineResponse create(
        CreateGoodsReceiptLineRequest request) {

    GoodsReceipt goodsReceipt =
            getGoodsReceipt(
                    request.getGoodsReceiptId());

    if (goodsReceipt.getStatus()
            != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Lines can only be added to a draft Goods Receipt.");
    }

    PurchaseOrderLine purchaseOrderLine =
            getPurchaseOrderLine(
                    request.getPurchaseOrderLineId());

    Product product =
            getProduct(
                    request.getProductId());

    if (repository.existsByGoodsReceiptIdAndProductId(

            goodsReceipt.getId(),

            product.getId())) {

        throw new DuplicateResourceException(
                "Product already exists on this Goods Receipt.");
    }

    if (request.getAcceptedQuantity()

            .add(request.getRejectedQuantity())

            .compareTo(request.getReceivedQuantity()) != 0) {

        throw new InvalidWorkflowException(

                "Accepted + Rejected quantity must equal Received quantity.");
    }

    if (request.getReceivedQuantity()

            .compareTo(purchaseOrderLine.getQuantity()) > 0) {

        throw new InvalidWorkflowException(

                "Received quantity cannot exceed ordered quantity.");
    }

    GoodsReceiptLine line =
            GoodsReceiptLine.builder()

                    .goodsReceipt(
                            goodsReceipt)

                    .purchaseOrderLine(
                            purchaseOrderLine)

                    .product(
                            product)

                    .orderedQuantity(
                            purchaseOrderLine.getQuantity())

                    .receivedQuantity(
                            request.getReceivedQuantity())

                    .acceptedQuantity(
                            request.getAcceptedQuantity())

                    .rejectedQuantity(
                            request.getRejectedQuantity())

                    .unitCost(
                            request.getUnitCost())

                    .remarks(
                            request.getRemarks())

                    .build();

    line =
            repository.save(
                    line);

    return GoodsReceiptLineMapper.toResponse(
            line);
}
/* 
@Override
public GoodsReceiptLineResponse update(
        UUID id,
        UpdateGoodsReceiptLineRequest request) {

    GoodsReceiptLine line =
            getLine(id);


    GoodsReceipt receipt =
            line.getGoodsReceipt();

    if (line.getGoodsReceipt().getStatus()
            != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Goods Receipts can be modified.");
    }

    if (request.getAcceptedQuantity()

            .add(request.getRejectedQuantity())

            .compareTo(request.getReceivedQuantity()) != 0) {

        throw new InvalidWorkflowException(
                "Accepted + Rejected quantity must equal Received quantity.");
    }

    if (request.getReceivedQuantity()

            .compareTo(line.getOrderedQuantity()) > 0) {

        throw new InvalidWorkflowException(
                "Received quantity cannot exceed ordered quantity.");
    }




    line.setReceivedQuantity(
            request.getReceivedQuantity());

    line.setAcceptedQuantity(
            request.getAcceptedQuantity());

    line.setRejectedQuantity(
            request.getRejectedQuantity());

    line.setUnitCost(
            request.getUnitCost());

    line.setRemarks(
            request.getRemarks());

    line =
            repository.save(line);

    return GoodsReceiptLineMapper.toResponse(
            line);
}
*/
@Override
public GoodsReceiptLineResponse update(
        UUID id,
        UpdateGoodsReceiptLineRequest request) {

    GoodsReceiptLine line =
            getLine(id);


    GoodsReceipt receipt =
            line.getGoodsReceipt();

    if (receipt.getStatus() != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only Draft Goods Receipts can be updated.");
    }

    BigDecimal total =
            request.getAcceptedQuantity()
                    .add(request.getRejectedQuantity());

    if (total.compareTo(line.getReceivedQuantity()) != 0) {

        throw new InvalidWorkflowException(
                "Accepted Quantity + Rejected Quantity must equal Received Quantity.");
    }

    line.setAcceptedQuantity(
            request.getAcceptedQuantity());

    line.setRejectedQuantity(
            request.getRejectedQuantity());

    line.setRemarks(
            request.getRemarks());

    line =
            repository.save(line);
            
    return GoodsReceiptLineMapper.toResponse(line);
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
public GoodsReceiptLineResponse findById(
        UUID id) {

    return GoodsReceiptLineMapper.toResponse(
            getLine(id));
}

@Override
@Transactional(readOnly = true)
public List<GoodsReceiptLineResponse> findByGoodsReceipt(
        UUID goodsReceiptId) {

    return repository.findByGoodsReceiptId(
                    goodsReceiptId)

            .stream()

            .map(GoodsReceiptLineMapper::toResponse)

            .toList();
}

@Override
public void delete(
        UUID id) {

    GoodsReceiptLine line =
            getLine(id);

    if (line.getGoodsReceipt().getStatus()
            != ReceiptStatus.DRAFT) {

        throw new InvalidWorkflowException(
                "Only draft Goods Receipts can be modified.");
    }

    repository.delete(line);
}
       
}