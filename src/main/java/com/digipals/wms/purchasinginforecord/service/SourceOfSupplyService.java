package com.digipals.wms.purchasinginforecord.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchasinginforecord.dto.ApplySourceOfSupplyRequest;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordResponse;
import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import com.digipals.wms.purchasinginforecord.repository.PurchasingInfoRecordRepository;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SourceOfSupplyService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchasingInfoRecordRepository repository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository requisitionLineRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> simulate(UUID productId, UUID warehouseId, LocalDate deliveryDate) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));

        LocalDate effectiveDate = deliveryDate == null ? LocalDate.now() : deliveryDate;
        List<PurchasingInfoRecord> candidates = activeCandidates(productId, warehouseId, effectiveDate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("productId", productId);
        response.put("warehouseId", warehouseId);
        response.put("deliveryDate", effectiveDate);
        response.put("sourceDetermined", candidates.size() == 1);
        response.put("multipleSources", candidates.size() > 1);
        response.put("candidates", candidates.stream().map(this::toResponse).toList());
        response.put("selectedSource", candidates.isEmpty() ? null : toResponse(candidates.get(0)));
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> determineForRequisition(UUID requisitionId, LocalDate deliveryDate) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        LocalDate effectiveDate = deliveryDate == null ? LocalDate.now() : deliveryDate;

        List<Map<String, Object>> lines = requisitionLineRepository
                .findByPurchaseRequisitionId(requisitionId)
                .stream()
                .map(line -> determineLine(line, requisition.getWarehouse().getId(), effectiveDate))
                .toList();

        long unresolved = lines.stream().filter(line -> "NO_SOURCE".equals(line.get("status"))).count();
        long ambiguous = lines.stream().filter(line -> Boolean.TRUE.equals(line.get("multipleSources"))).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("purchaseRequisitionId", requisitionId);
        result.put("requisitionNumber", requisition.getRequisitionNumber());
        result.put("deliveryDate", effectiveDate);
        result.put("lineCount", lines.size());
        result.put("unresolvedLineCount", unresolved);
        result.put("ambiguousLineCount", ambiguous);
        result.put("readyForSourceAssignment", unresolved == 0);
        result.put("lines", lines);
        return result;
    }

    public PurchaseRequisitionResponse apply(UUID requisitionId, UUID lineId, ApplySourceOfSupplyRequest request) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        if (requisition.getStatus() != PurchaseRequisitionStatus.DRAFT) {
            throw new InvalidWorkflowException("Source of Supply can only be assigned while the Purchase Requisition is in DRAFT.");
        }

        PurchaseRequisitionLine line = requisitionLineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition line not found."));
        if (!requisitionId.equals(line.getPurchaseRequisition().getId())) {
            throw new InvalidWorkflowException("Purchase Requisition line does not belong to this requisition.");
        }

        PurchasingInfoRecord record = repository.findById(request.getPurchasingInfoRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchasing info record not found."));
        validateCandidate(record, line.getProduct().getId(), requisition.getWarehouse().getId(), requisition.getCurrency(), LocalDate.now());

        if (requisition.getSupplier() == null || !requisition.getSupplier().getId().equals(record.getSupplierProduct().getSupplier().getId())) {
            throw new InvalidWorkflowException(
                    "Selected source supplier " + record.getSupplierProduct().getSupplier().getCode()
                            + " does not match the Purchase Requisition supplier. Create the requisition for the selected supplier before assignment.");
        }

        line.setSourceSupplier(record.getSupplierProduct().getSupplier());
        line.setPurchasingInfoRecord(record);
        if (record.getLastPurchasePrice() != null && record.getLastPurchasePrice().compareTo(BigDecimal.ZERO) >= 0) {
            line.setEstimatedUnitCost(record.getLastPurchasePrice().setScale(2, java.math.RoundingMode.HALF_UP));
        }
        requisitionLineRepository.save(line);

        return com.digipals.wms.common.mapper.PurchaseRequisitionMapper.toResponse(requisition);
    }

    private Map<String, Object> determineLine(PurchaseRequisitionLine line, UUID warehouseId, LocalDate deliveryDate) {
        List<PurchasingInfoRecord> candidates = activeCandidates(line.getProduct().getId(), warehouseId, deliveryDate);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("lineId", line.getId());
        result.put("productId", line.getProduct().getId());
        result.put("sku", line.getProduct().getSku());
        result.put("productName", line.getProduct().getName());
        result.put("requestedQuantity", line.getQuantity());
        result.put("status", candidates.isEmpty() ? "NO_SOURCE" : "SOURCE_AVAILABLE");
        result.put("multipleSources", candidates.size() > 1);
        result.put("candidates", candidates.stream().map(this::toResponse).toList());
        result.put("selectedSource", candidates.isEmpty() ? null : toResponse(candidates.get(0)));
        return result;
    }

    private List<PurchasingInfoRecord> activeCandidates(UUID productId, UUID warehouseId, LocalDate date) {
        return repository.findByWarehouseId(warehouseId).stream()
                .filter(record -> Boolean.TRUE.equals(record.getActive()))
                .filter(record -> record.getSupplierProduct().getProduct().getId().equals(productId))
                .filter(record -> record.getValidFrom() == null || !date.isBefore(record.getValidFrom()))
                .filter(record -> record.getValidTo() == null || !date.isAfter(record.getValidTo()))
                .sorted(Comparator
                        .comparing((PurchasingInfoRecord r) -> Boolean.TRUE.equals(r.getAutomaticSourcing())).reversed()
                        .thenComparing(Comparator.comparing(
                                (PurchasingInfoRecord r) -> Boolean.TRUE.equals(r.getRegularSupplier())).reversed())
                        .thenComparing(r -> r.getLastPurchasePrice() == null ? BigDecimal.valueOf(Double.MAX_VALUE) : r.getLastPurchasePrice())
                        .thenComparing(r -> r.getPlannedDeliveryDays() == null ? Integer.MAX_VALUE : r.getPlannedDeliveryDays()))
                .toList();
    }

    private void validateCandidate(PurchasingInfoRecord record, UUID productId, UUID warehouseId, String requisitionCurrency, LocalDate date) {
        if (!Boolean.TRUE.equals(record.getActive())) throw new InvalidWorkflowException("Purchasing info record is inactive.");
        if (!record.getWarehouse().getId().equals(warehouseId)) throw new InvalidWorkflowException("Purchasing info record does not belong to the requisition warehouse.");
        if (!record.getSupplierProduct().getProduct().getId().equals(productId)) throw new InvalidWorkflowException("Purchasing info record does not belong to the requisition product.");
        if (record.getValidFrom() != null && date.isBefore(record.getValidFrom())) throw new InvalidWorkflowException("Purchasing info record is not yet valid.");
        if (record.getValidTo() != null && date.isAfter(record.getValidTo())) throw new InvalidWorkflowException("Purchasing info record has expired.");
        if (requisitionCurrency != null && !requisitionCurrency.equalsIgnoreCase(record.getCurrency())) {
            throw new InvalidWorkflowException("Purchasing info record currency " + record.getCurrency() + " does not match Purchase Requisition currency " + requisitionCurrency + ".");
        }
    }

    private PurchaseRequisition getRequisition(UUID id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
    }

    private PurchasingInfoRecordResponse toResponse(PurchasingInfoRecord entity) {
        var supplierProduct = entity.getSupplierProduct();
        var product = supplierProduct.getProduct();
        var supplier = supplierProduct.getSupplier();
        var warehouse = entity.getWarehouse();

        return PurchasingInfoRecordResponse.builder()
                .id(entity.getId())
                .supplierProductIdentifierId(supplierProduct.getId())
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .supplierId(supplier.getId())
                .supplierCode(supplier.getCode())
                .supplierName(supplier.getName())
                .supplierItemCode(supplierProduct.getSupplierItemCode())
                .supplierItemName(supplierProduct.getSupplierItemName())
                .warehouseId(warehouse.getId())
                .warehouseCode(warehouse.getCode())
                .warehouseName(warehouse.getName())
                .currency(entity.getCurrency())
                .lastPurchasePrice(entity.getLastPurchasePrice())
                .standardOrderQuantity(entity.getStandardOrderQuantity())
                .plannedDeliveryDays(entity.getPlannedDeliveryDays())
                .validFrom(entity.getValidFrom())
                .validTo(entity.getValidTo())
                .regularSupplier(entity.getRegularSupplier())
                .automaticSourcing(entity.getAutomaticSourcing())
                .active(entity.getActive())
                .build();
    }
}
