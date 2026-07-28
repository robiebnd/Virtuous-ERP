package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
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
public class PurchaseRequisitionServiceImpl
        implements PurchaseRequisitionService {

    private final PurchaseRequisitionRepository repository;

    private final PurchaseRequisitionLineRepository
            lineRepository;

    private final WarehouseRepository
            warehouseRepository;

    private final DocumentNumberService
            documentNumberService;

    private final PurchaseRequisitionValidator
            validator;

    private final CurrentUserService
            currentUserService;

private PurchaseRequisition getRequisition(
        UUID id) {

    return repository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Purchase Requisition not found."));
}

private Warehouse getWarehouse(
        UUID id) {

    return warehouseRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Warehouse not found."));
}

private void validateHasLines(
        PurchaseRequisition requisition) {

    if (lineRepository
            .findByPurchaseRequisitionId(
                    requisition.getId())
            .isEmpty()) {

        throw new RuntimeException(
                "Purchase Requisition contains no lines.");
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
public PurchaseRequisitionResponse findById(
        UUID id) {

    return PurchaseRequisitionMapper.toResponse(
            getRequisition(id));
}
@Override
@Transactional(readOnly = true)
public List<PurchaseRequisitionResponse> findByStatus(
        PurchaseRequisitionStatus status) {

    return repository.findByStatus(status)

            .stream()

            .map(PurchaseRequisitionMapper::toResponse)

            .toList();
}
@Override
@Transactional(readOnly = true)
public List<PurchaseRequisitionResponse> findByWarehouse(
        UUID warehouseId) {

    return repository.findByWarehouseId(
                    warehouseId)

            .stream()

            .map(PurchaseRequisitionMapper::toResponse)

            .toList();
}
@Override
public PurchaseRequisitionResponse create(
        CreatePurchaseRequisitionRequest request) {

    Warehouse warehouse =
            getWarehouse(
                    request.getWarehouseId());

    PurchaseRequisition requisition =
            PurchaseRequisition.builder()

                    .requisitionNumber(
                            documentNumberService.next(
                                    DocumentType.PURCHASE_REQUISITION))

                    .warehouse(
                            warehouse)

                    .department(
                            request.getDepartment())

                    .remarks(
                            request.getRemarks())

                    .status(
                            PurchaseRequisitionStatus.DRAFT)

                    // Will be populated later
                    .requestedBy(
                        currentUserService.getCurrentUser())

                    .build();

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}
@Override
public PurchaseRequisitionResponse update(
        UUID id,
        UpdatePurchaseRequisitionRequest request) {

    PurchaseRequisition requisition =
            getRequisition(id);

    validator.validateDraft(
            requisition);

    requisition.setDepartment(
            request.getDepartment());

    requisition.setRemarks(
            request.getRemarks());

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}
@Override
public void delete(
        UUID id) {

    PurchaseRequisition requisition =
            getRequisition(id);

    validator.validateDraft(
            requisition);

    repository.delete(
            requisition);
}
@Override
public PurchaseRequisitionResponse submit(
        UUID id) {

    PurchaseRequisition requisition =
            getRequisition(id);

    validator.validateDraft(
            requisition);

    validateHasLines(
            requisition);

    requisition.setStatus(
            PurchaseRequisitionStatus.SUBMITTED);

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}
@Override
public PurchaseRequisitionResponse approve(
        UUID id) {

    PurchaseRequisition requisition =
            getRequisition(id);

    validator.validateSubmitted(
            requisition);

    requisition.setStatus(
            PurchaseRequisitionStatus.APPROVED);

    // CurrentUserService will populate later
    requisition.setApprovedBy(
            null);

    requisition.setApprovedAt(
            LocalDateTime.now());

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}
@Override
public PurchaseRequisitionResponse reject(
        UUID id,
        String remarks) {

    PurchaseRequisition requisition =
            getRequisition(id);

    validator.validateSubmitted(
            requisition);

    requisition.setStatus(
            PurchaseRequisitionStatus.REJECTED);

    requisition.setRemarks(
            remarks);

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}
@Override
public PurchaseRequisitionResponse cancel(
        UUID id) {

    PurchaseRequisition requisition =
            getRequisition(id);

    if (requisition.getStatus() ==
            PurchaseRequisitionStatus.CONVERTED_TO_PO) {

        throw new RuntimeException(
                "Converted Purchase Requisitions cannot be cancelled.");
    }

    requisition.setStatus(
            PurchaseRequisitionStatus.CANCELLED);

    repository.save(
            requisition);

    return PurchaseRequisitionMapper.toResponse(
            requisition);
}

}