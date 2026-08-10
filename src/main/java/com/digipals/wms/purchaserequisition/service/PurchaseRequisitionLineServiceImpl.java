package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.mapper.PurchaseRequisitionLineMapper;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.purchaserequisition.validator.PurchaseRequisitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseRequisitionLineServiceImpl implements PurchaseRequisitionLineService {

    private final PurchaseRequisitionLineRepository repository;
    private final PurchaseRequisitionRepository purchaseRequisitionRepository;
    private final ProductRepository productRepository;
    private final PurchaseRequisitionValidator validator;

    private PurchaseRequisition getPurchaseRequisition(UUID id) {
        return purchaseRequisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition not found."));
    }

    private PurchaseRequisitionLine getLine(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition Line not found."));
    }

    private Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found."));
    }

    @Override
    public PurchaseRequisitionLineResponse create(
            UUID purchaseRequisitionId,
            CreatePurchaseRequisitionLineRequest request) {

        PurchaseRequisition requisition = getPurchaseRequisition(purchaseRequisitionId);
        validator.validateDraft(requisition);

        Product product = getProduct(request.getProductId());

        if (repository.existsByPurchaseRequisitionIdAndProductId(
                requisition.getId(), product.getId())) {
            throw new RuntimeException(
                    "Product already exists on this Purchase Requisition.");
        }

        PurchaseRequisitionLine line = PurchaseRequisitionLine.builder()
                .purchaseRequisition(requisition)
                .product(product)
                .quantity(request.getQuantity())
                .estimatedUnitCost(request.getEstimatedUnitCost())
                .remarks(request.getRemarks())
                .build();

        return PurchaseRequisitionLineMapper.toResponse(repository.save(line));
    }

    @Override
    public PurchaseRequisitionLineResponse update(
            UUID id,
            UpdatePurchaseRequisitionLineRequest request) {

        PurchaseRequisitionLine line = getLine(id);
        validator.validateDraft(line.getPurchaseRequisition());

        line.setQuantity(request.getQuantity());
        line.setEstimatedUnitCost(request.getEstimatedUnitCost());
        line.setRemarks(request.getRemarks());

        return PurchaseRequisitionLineMapper.toResponse(repository.save(line));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionLineResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PurchaseRequisitionLineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequisitionLineResponse findById(UUID id) {
        return PurchaseRequisitionLineMapper.toResponse(getLine(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionLineResponse> findByPurchaseRequisition(
            UUID purchaseRequisitionId) {

        getPurchaseRequisition(purchaseRequisitionId);

        return repository.findByPurchaseRequisitionId(purchaseRequisitionId)
                .stream()
                .map(PurchaseRequisitionLineMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        PurchaseRequisitionLine line = getLine(id);
        validator.validateDraft(line.getPurchaseRequisition());
        repository.delete(line);
    }
}
