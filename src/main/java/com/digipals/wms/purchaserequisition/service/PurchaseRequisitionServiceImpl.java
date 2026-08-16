package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PurchaseRequisitionMapper;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.purchaserequisition.validator.PurchaseRequisitionValidator;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseRequisitionServiceImpl implements PurchaseRequisitionService {

    private final PurchaseRequisitionRepository repository;
    private final PurchaseRequisitionLineRepository lineRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final DocumentNumberService documentNumberService;
    private final PurchaseRequisitionValidator validator;
    private final CurrentUserService currentUserService;

    private PurchaseRequisition getRequisition(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase Requisition not found."));
    }

    private Warehouse getWarehouse(UUID id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found."));
    }

    private Supplier getSupplier(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found."));
    }

    private void validateHasLines(PurchaseRequisition requisition) {
        if (lineRepository.countByPurchaseRequisitionId(requisition.getId()) == 0) {
            throw new RuntimeException("Purchase Requisition contains no lines.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PurchaseRequisitionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequisitionResponse findById(UUID id) {
        return PurchaseRequisitionMapper.toResponse(getRequisition(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequisitionResponse findByRequisitionNumber(String requisitionNumber) {
        if (requisitionNumber == null || requisitionNumber.isBlank()) {
            throw new IllegalArgumentException("Requisition number is required.");
        }

        PurchaseRequisition requisition = repository
                .findByRequisitionNumber(requisitionNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase Requisition not found: " + requisitionNumber));

        return PurchaseRequisitionMapper.toResponse(requisition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findByStatus(PurchaseRequisitionStatus status) {
        return repository.findByStatus(status)
                .stream()
                .map(PurchaseRequisitionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findByWarehouse(UUID warehouseId) {
        return repository.findByWarehouseId(warehouseId)
                .stream()
                .map(PurchaseRequisitionMapper::toResponse)
                .toList();
    }

    @Override
    public PurchaseRequisitionResponse create(CreatePurchaseRequisitionRequest request) {
        Warehouse warehouse = getWarehouse(request.getWarehouseId());
        Supplier supplier = getSupplier(request.getSupplierId());

        PurchaseRequisition requisition = PurchaseRequisition.builder()
                .requisitionNumber(documentNumberService.next(DocumentType.PURCHASE_REQUISITION))
                .warehouse(warehouse)
                .supplier(supplier)
                .department(request.getDepartment().trim())
                .remarks(request.getRemarks())
                .status(PurchaseRequisitionStatus.DRAFT)
                .requestedBy(currentUserService.getCurrentUser())
                .build();

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public PurchaseRequisitionResponse update(UUID id, UpdatePurchaseRequisitionRequest request) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateDraft(requisition);

        Supplier supplier = getSupplier(request.getSupplierId());

        requisition.setSupplier(supplier);
        requisition.setDepartment(request.getDepartment().trim());
        requisition.setRemarks(request.getRemarks());

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public void delete(UUID id) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateDraft(requisition);
        repository.delete(requisition);
    }

    @Override
    public PurchaseRequisitionResponse submit(UUID id) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateDraft(requisition);
        validateHasLines(requisition);

        if (requisition.getSupplier() == null) {
            throw new IllegalStateException("Purchase Requisition supplier is required before submission.");
        }

        requisition.setStatus(PurchaseRequisitionStatus.SUBMITTED);
        requisition.setSubmittedAt(LocalDateTime.now());

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public PurchaseRequisitionResponse approve(UUID id) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateSubmitted(requisition);

        requisition.setStatus(PurchaseRequisitionStatus.APPROVED);
        requisition.setApprovedBy(currentUserService.getCurrentUser());
        requisition.setApprovedAt(LocalDateTime.now());

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public PurchaseRequisitionResponse reject(UUID id, String remarks) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateSubmitted(requisition);

        if (remarks == null || remarks.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required.");
        }

        requisition.setStatus(PurchaseRequisitionStatus.REJECTED);
        requisition.setRejectedBy(currentUserService.getCurrentUser());
        requisition.setRejectedAt(LocalDateTime.now());
        requisition.setRejectionReason(remarks.trim());

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public PurchaseRequisitionResponse cancel(UUID id) {
        PurchaseRequisition requisition = getRequisition(id);
        validator.validateCanCancel(requisition);

        requisition.setStatus(PurchaseRequisitionStatus.CANCELLED);
        requisition.setCancelledBy(currentUserService.getCurrentUser());
        requisition.setCancelledAt(LocalDateTime.now());

        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }
}