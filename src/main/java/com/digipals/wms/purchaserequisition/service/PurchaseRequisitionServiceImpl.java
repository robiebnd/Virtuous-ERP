package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PurchaseRequisitionMapper;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.purchaserequisition.validator.PurchaseRequisitionValidator;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import com.digipals.wms.supplierquotation.repository.SupplierQuotationRepository;
import com.digipals.wms.supplierquotation.service.QuotationAiService;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseRequisitionServiceImpl implements PurchaseRequisitionService {
    private final PurchaseRequisitionRepository repository;
    private final PurchaseRequisitionLineRepository lineRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierQuotationRepository quotationRepository;
    private final QuotationAiService quotationAiService;
    private final DocumentNumberService documentNumberService;
    private final PurchaseRequisitionValidator validator;
    private final CurrentUserService currentUserService;

    private PurchaseRequisition getRequisition(UUID id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found.")); }
    private Warehouse getWarehouse(UUID id) { return warehouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Warehouse not found.")); }
    private Supplier getSupplier(UUID id) { return supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found.")); }
    private void validateHasLines(PurchaseRequisition requisition) { if (lineRepository.countByPurchaseRequisitionId(requisition.getId()) == 0) throw new RuntimeException("Purchase Requisition contains no lines."); }

    private PurchaseRequisitionResponse toResponseWithLines(PurchaseRequisition requisition) {
        PurchaseRequisitionResponse response = PurchaseRequisitionMapper.toResponse(requisition);
        if (requisition.getStatus() != PurchaseRequisitionStatus.REJECTED) {
            response.setRejectionReason(null);
        }
        List<PurchaseRequisitionResponse.LineResponse> lines = lineRepository.findByPurchaseRequisitionId(requisition.getId())
                .stream()
                .map(line -> {
                    BigDecimal unitCost = line.getEstimatedUnitCost() == null
                            ? BigDecimal.ZERO.setScale(2)
                            : line.getEstimatedUnitCost().setScale(2, java.math.RoundingMode.HALF_UP);
                    BigDecimal lineTotal = line.getQuantity()
                            .multiply(unitCost)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                    return PurchaseRequisitionResponse.LineResponse.builder()
                            .id(line.getId())
                            .productId(line.getProduct().getId())
                            .sku(line.getProduct().getSku())
                            .productName(line.getProduct().getName())
                            .quantity(line.getQuantity())
                            .estimatedUnitCost(unitCost)
                            .estimatedLineTotal(lineTotal)
                            .remarks(line.getRemarks())
                            .build();
                })
                .toList();
        response.setLines(lines);
        return response;
    }

    @Override @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findAll() { return repository.findAll().stream().map(this::toResponseWithLines).toList(); }
    @Override @Transactional(readOnly = true)
    public PurchaseRequisitionResponse findById(UUID id) { return toResponseWithLines(getRequisition(id)); }
    @Override @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findByStatus(PurchaseRequisitionStatus status) { return repository.findByStatus(status).stream().map(this::toResponseWithLines).toList(); }
    @Override @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponse> findByWarehouse(UUID warehouseId) { return repository.findByWarehouseId(warehouseId).stream().map(this::toResponseWithLines).toList(); }

    @Override
    public PurchaseRequisitionResponse create(CreatePurchaseRequisitionRequest request) {
        Warehouse warehouse = getWarehouse(request.getWarehouseId());
        Supplier supplier = getSupplier(request.getSupplierId());
        PurchaseRequisition requisition = PurchaseRequisition.builder().requisitionNumber(documentNumberService.next(DocumentType.PURCHASE_REQUISITION)).warehouse(warehouse).supplier(supplier).department(request.getDepartment().trim()).remarks(request.getRemarks()).status(PurchaseRequisitionStatus.DRAFT).requestedBy(currentUserService.getCurrentUser()).build();
        return PurchaseRequisitionMapper.toResponse(repository.save(requisition));
    }

    @Override
    public PurchaseRequisitionResponse update(UUID id, UpdatePurchaseRequisitionRequest request) {
        PurchaseRequisition requisition = getRequisition(id); validator.validateDraft(requisition);
        requisition.setSupplier(getSupplier(request.getSupplierId())); requisition.setDepartment(request.getDepartment().trim()); requisition.setRemarks(request.getRemarks());
        return toResponseWithLines(repository.save(requisition));
    }
    @Override public void delete(UUID id) { PurchaseRequisition requisition = getRequisition(id); validator.validateDraft(requisition); repository.delete(requisition); }
    @Override public PurchaseRequisitionResponse submit(UUID id) { PurchaseRequisition requisition = getRequisition(id); validator.validateDraft(requisition); validateHasLines(requisition); if (requisition.getSupplier() == null) throw new IllegalStateException("Purchase Requisition supplier is required before submission."); requisition.setStatus(PurchaseRequisitionStatus.SUBMITTED); requisition.setSubmittedAt(LocalDateTime.now()); return toResponseWithLines(repository.save(requisition)); }
    @Override public PurchaseRequisitionResponse approve(UUID id) { PurchaseRequisition requisition = getRequisition(id); validator.validateSubmitted(requisition); requisition.setStatus(PurchaseRequisitionStatus.APPROVED); requisition.setApprovedBy(currentUserService.getCurrentUser()); requisition.setApprovedAt(LocalDateTime.now()); return toResponseWithLines(repository.save(requisition)); }
    @Override public PurchaseRequisitionResponse reject(UUID id, String remarks) { PurchaseRequisition requisition = getRequisition(id); validator.validateSubmitted(requisition); if (remarks == null || remarks.isBlank()) throw new IllegalArgumentException("Rejection reason is required."); requisition.setStatus(PurchaseRequisitionStatus.REJECTED); requisition.setRejectedBy(currentUserService.getCurrentUser()); requisition.setRejectedAt(LocalDateTime.now()); requisition.setRejectionReason(remarks.trim()); return toResponseWithLines(repository.save(requisition)); }
    @Override public PurchaseRequisitionResponse cancel(UUID id) { PurchaseRequisition requisition = getRequisition(id); validator.validateCanCancel(requisition); requisition.setStatus(PurchaseRequisitionStatus.CANCELLED); requisition.setCancelledBy(currentUserService.getCurrentUser()); requisition.setCancelledAt(LocalDateTime.now()); return toResponseWithLines(repository.save(requisition)); }

    @Override
    public PurchaseRequisitionResponse importQuotation(UUID requisitionId, UUID quotationId) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        validator.validateDraft(requisition);
        SupplierQuotation quotation = quotationRepository.findById(quotationId).orElseThrow(() -> new ResourceNotFoundException("Supplier quotation not found."));
        if (quotation.getPurchaseRequisition() == null || !requisitionId.equals(quotation.getPurchaseRequisition().getId())) throw new InvalidWorkflowException("Supplier quotation does not belong to this Purchase Requisition.");
        if (quotation.getSupplier() == null || requisition.getSupplier() == null || !quotation.getSupplier().getId().equals(requisition.getSupplier().getId())) throw new InvalidWorkflowException("Supplier quotation supplier does not match the Purchase Requisition supplier.");
        if (lineRepository.countByPurchaseRequisitionId(requisitionId) > 0) throw new InvalidWorkflowException("Purchase Requisition already contains lines. Remove the existing lines before importing a quotation.");

        Map<String, Object> extraction = quotationAiService.extractLinesFromQuotation(quotationId);
        Object rawLines = extraction.get("lines");
        if (!(rawLines instanceof List<?> extractedLines) || extractedLines.isEmpty()) throw new InvalidWorkflowException("No quotation lines were extracted.");

        List<QuotationLineData> lines = new ArrayList<>();
        for (Object rawLine : extractedLines) {
            if (!(rawLine instanceof Map<?, ?> line)) throw new InvalidWorkflowException("Invalid extracted quotation line.");
            String sku = text(line.get("sku"));
            String description = text(line.get("description"));
            BigDecimal quantity = decimal(line.get("quantity"));
            BigDecimal unitPrice = decimal(line.get("unitPrice"));
            if (sku == null || sku.isBlank()) throw new InvalidWorkflowException("A quotation line is missing SKU.");
            if (description == null || description.isBlank()) throw new InvalidWorkflowException("A quotation line is missing product description for SKU " + sku + ".");
            if (quantity == null || quantity.signum() <= 0) throw new InvalidWorkflowException("Quotation line quantity must be greater than zero for SKU " + sku + ".");
            if (unitPrice == null || unitPrice.signum() < 0) throw new InvalidWorkflowException("Quotation line unit price cannot be negative for SKU " + sku + ".");
            Product product = productRepository.findBySku(sku).orElse(null);
            lines.add(new QuotationLineData(sku, description, quantity, unitPrice, product));
        }

        for (QuotationLineData line : lines) {
            if (line.product() == null) {
                Product product = Product.builder()
                        .sku(line.sku())
                        .name(line.description())
                        .description("Automatically created from supplier quotation " + quotation.getQuotationNumber())
                        .costPrice(line.unitPrice())
                        .sellingPrice(line.unitPrice())
                        .active(true)
                        .build();
                line.setProduct(productRepository.save(product));
            }
        }

        for (QuotationLineData line : lines) {
            lineRepository.save(PurchaseRequisitionLine.builder()
                    .purchaseRequisition(requisition)
                    .product(line.product())
                    .quantity(line.quantity())
                    .estimatedUnitCost(line.unitPrice())
                    .build());
        }

        return toResponseWithLines(requisition);
    }

    private String text(Object value) { return value == null ? null : value.toString().trim(); }
    private BigDecimal decimal(Object value) { if (value == null) return null; if (value instanceof BigDecimal decimal) return decimal; if (value instanceof Number number) return new BigDecimal(number.toString()); try { return new BigDecimal(value.toString()); } catch (NumberFormatException e) { return null; } }

    private static final class QuotationLineData {
        private final String sku;
        private final String description;
        private final BigDecimal quantity;
        private final BigDecimal unitPrice;
        private Product product;
        private QuotationLineData(String sku, String description, BigDecimal quantity, BigDecimal unitPrice, Product product) { this.sku = sku; this.description = description; this.quantity = quantity; this.unitPrice = unitPrice; this.product = product; }
        private String sku() { return sku; }
        private String description() { return description; }
        private BigDecimal quantity() { return quantity; }
        private BigDecimal unitPrice() { return unitPrice; }
        private Product product() { return product; }
        private void setProduct(Product product) { this.product = product; }
    }
}
