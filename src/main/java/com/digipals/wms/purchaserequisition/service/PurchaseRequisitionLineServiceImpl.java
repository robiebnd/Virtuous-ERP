package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.mapper.PurchaseRequisitionLineMapper;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.dto.SetPurchaseRequisitionLineSourceRequest;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.purchaserequisition.validator.PurchaseRequisitionValidator;
import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import com.digipals.wms.purchasinginforecord.repository.PurchasingInfoRecordRepository;
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
    private final PurchasingInfoRecordRepository purchasingInfoRecordRepository;
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
            throw new RuntimeException("Product already exists on this Purchase Requisition.");
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
    public PurchaseRequisitionLineResponse setSourceOfSupply(
            UUID id,
            SetPurchaseRequisitionLineSourceRequest request) {

        PurchaseRequisitionLine line = getLine(id);
        PurchaseRequisition requisition = line.getPurchaseRequisition();
        validator.validateDraft(requisition);

        PurchasingInfoRecord pir = purchasingInfoRecordRepository.findById(
                        request.getPurchasingInfoRecordId())
                .orElseThrow(() -> new RuntimeException(
                        "Purchasing Info Record not found."));

        if (pir.getSupplierProduct() == null
                || pir.getSupplierProduct().getProduct() == null
                || !pir.getSupplierProduct().getProduct().getId().equals(line.getProduct().getId())) {
            throw new RuntimeException(
                    "Purchasing Info Record does not belong to the Product on this requisition line.");
        }

        if (pir.getWarehouse() == null
                || requisition.getWarehouse() == null
                || !pir.getWarehouse().getId().equals(requisition.getWarehouse().getId())) {
            throw new RuntimeException(
                    "Purchasing Info Record does not belong to the Purchase Requisition warehouse.");
        }

        if (pir.getSupplierProduct().getSupplier() == null) {
            throw new RuntimeException(
                    "Purchasing Info Record has no supplier assigned.");
        }

        line.setPurchasingInfoRecord(pir);
        line.setSourceSupplier(pir.getSupplierProduct().getSupplier());

        if (pir.getLastPurchasePrice() != null) {
            line.setEstimatedUnitCost(pir.getLastPurchasePrice());
        }

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
