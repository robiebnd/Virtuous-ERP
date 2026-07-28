package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PurchaseOrderLineMapper;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchaseorders.dto.CreatePurchaseOrderLineRequest;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderLineRequest;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderLineServiceImpl
        implements PurchaseOrderLineService {

    private final PurchaseOrderLineRepository repository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final ProductRepository productRepository;

    private PurchaseOrder getPurchaseOrder(UUID id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase Order not found."));
    }

    private Product getProduct(UUID id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."));
    }

    private PurchaseOrderLine getLine(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase Order Line not found."));
    }

    @Override
    public PurchaseOrderLineResponse create(
            CreatePurchaseOrderLineRequest request) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrder(request.getPurchaseOrderId());

        if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {

            throw new InvalidWorkflowException(
                    "Lines can only be added to a Draft Purchase Order.");
        }

        Product product =
                getProduct(request.getProductId());

        if (repository.existsByPurchaseOrderIdAndProductId(
                purchaseOrder.getId(),
                product.getId())) {

            throw new DuplicateResourceException(
                    "Product already exists on this Purchase Order.");
        }

        PurchaseOrderLine line =
                PurchaseOrderLine.builder()

                        .purchaseOrder(purchaseOrder)

                        .product(product)

                        .quantity(request.getQuantity())

                        .unitPrice(request.getUnitPrice())

                        .build();

        line = repository.save(line);

        return PurchaseOrderLineMapper.toResponse(line);
    }

    @Override
    public PurchaseOrderLineResponse update(
            UUID id,
            UpdatePurchaseOrderLineRequest request) {

        PurchaseOrderLine line =
                getLine(id);

        if (line.getPurchaseOrder().getStatus()
                != PurchaseOrderStatus.DRAFT) {

            throw new InvalidWorkflowException(
                    "Only Draft Purchase Orders can be modified.");
        }

        line.setQuantity(request.getQuantity());

        line.setUnitPrice(request.getUnitPrice());

        line = repository.save(line);

        return PurchaseOrderLineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderLineResponse findById(
            UUID id) {

        return PurchaseOrderLineMapper.toResponse(
                getLine(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderLineResponse> findAll() {

        return repository.findAll()

                .stream()

                .map(PurchaseOrderLineMapper::toResponse)

                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderLineResponse> findByPurchaseOrder(
            UUID purchaseOrderId) {

        return repository.findByPurchaseOrderId(
                purchaseOrderId)

                .stream()

                .map(PurchaseOrderLineMapper::toResponse)

                .toList();
    }

    @Override
    public void delete(UUID id) {

        PurchaseOrderLine line =
                getLine(id);

        if (line.getPurchaseOrder().getStatus()
                != PurchaseOrderStatus.DRAFT) {

            throw new InvalidWorkflowException(
                    "Only Draft Purchase Orders can be modified.");
        }

        repository.delete(line);
    }
}