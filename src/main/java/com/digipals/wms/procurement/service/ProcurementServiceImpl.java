package com.digipals.wms.procurement.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.common.mapper.PurchaseOrderMapper;
import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.procurement.validation.ProcurementValidator;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.*;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaserequisition.entity.*;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcurementServiceImpl implements ProcurementService {

    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository requisitionLineRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final DocumentNumberService documentNumberService;
    private final ProcurementValidator validator;

    @Override
    public PurchaseOrderResponse generatePurchaseOrder(GeneratePurchaseOrderRequest request) {
        PurchaseRequisition requisition = requisitionRepository.findById(request.getPurchaseRequisitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        validator.validateApproved(requisition);
        validator.validateNotConverted(requisition);

        Supplier supplier = request.getSupplierId() != null
                ? supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found."))
                : requisition.getSupplier();
        if (supplier == null) throw new ResourceNotFoundException("No supplier is assigned to the Purchase Requisition.");

        List<PurchaseRequisitionLine> lines = requisitionLineRepository.findByPurchaseRequisitionId(requisition.getId());
        if (lines.isEmpty()) throw new ResourceNotFoundException("Purchase Requisition contains no lines.");

        PurchaseOrder po = purchaseOrderRepository.save(PurchaseOrder.builder()
                .poNumber(documentNumberService.next(DocumentType.PURCHASE_ORDER))
                .supplier(supplier).warehouse(requisition.getWarehouse()).purchaseRequisition(requisition)
                .source(ProcurementSource.REQUISITION).status(PurchaseOrderStatus.DRAFT).build());

        for (PurchaseRequisitionLine line : lines) {
            BigDecimal price = purchaseOrderLineRepository.findLatestUnitPrice(supplier.getId(), line.getProduct().getId())
                    .orElse(line.getProduct().getCostPrice() == null ? BigDecimal.ZERO : line.getProduct().getCostPrice());
            purchaseOrderLineRepository.save(PurchaseOrderLine.builder().purchaseOrder(po)
                    .product(line.getProduct()).quantity(line.getQuantity()).unitPrice(price).build());
        }
        requisition.setStatus(PurchaseRequisitionStatus.CONVERTED_TO_PO);
        requisitionRepository.save(requisition);
        return PurchaseOrderMapper.toResponse(po);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommendPurchaseOrder(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        validator.validateApproved(pr);
        validator.validateNotConverted(pr);
        List<PurchaseRequisitionLine> lines = requisitionLineRepository.findByPurchaseRequisitionId(id);
        if (lines.isEmpty()) throw new ResourceNotFoundException("Purchase Requisition contains no lines.");

        List<Supplier> suppliers = supplierRepository.findByActiveTrue();
        if (suppliers.isEmpty()) throw new ResourceNotFoundException("No active suppliers are available for recommendation.");

        Supplier best = null;
        BigDecimal bestTotal = null;
        int bestHistory = -1;
        List<Map<String,Object>> bestLines = null;
        for (Supplier supplier : suppliers) {
            BigDecimal total = BigDecimal.ZERO;
            int history = 0;
            List<Map<String,Object>> candidateLines = new java.util.ArrayList<>();
            for (PurchaseRequisitionLine line : lines) {
                BigDecimal historical = purchaseOrderLineRepository.findLatestUnitPrice(supplier.getId(), line.getProduct().getId()).orElse(null);
                boolean usedHistory = historical != null && historical.compareTo(BigDecimal.ZERO) > 0;
                BigDecimal price = usedHistory ? historical : (line.getProduct().getCostPrice() == null ? BigDecimal.ZERO : line.getProduct().getCostPrice());
                if (usedHistory) history++;
                BigDecimal lineTotal = line.getQuantity().multiply(price);
                total = total.add(lineTotal);
                Map<String,Object> item = new LinkedHashMap<>();
                item.put("productId", line.getProduct().getId()); item.put("sku", line.getProduct().getSku());
                item.put("productName", line.getProduct().getName()); item.put("requestedQuantity", line.getQuantity());
                item.put("recommendedQuantity", line.getQuantity()); item.put("recommendedUnitPrice", price);
                item.put("estimatedLineTotal", lineTotal); item.put("historicalPriceUsed", usedHistory);
                item.put("pricingBasis", usedHistory ? "LATEST_NON_CANCELLED_SUPPLIER_PRICE" : "PRODUCT_COST_PRICE_FALLBACK");
                candidateLines.add(item);
            }
            boolean preferred = pr.getSupplier() != null && pr.getSupplier().getId().equals(supplier.getId());
            boolean better = best == null || total.compareTo(bestTotal) < 0 || (total.compareTo(bestTotal) == 0 && preferred);
            if (better) { best = supplier; bestTotal = total; bestHistory = history; bestLines = candidateLines; }
        }

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("purchaseRequisitionId", pr.getId()); result.put("requisitionNumber", pr.getRequisitionNumber());
        result.put("recommendedSupplierId", best.getId()); result.put("recommendedSupplierCode", best.getCode());
        result.put("recommendedSupplierName", best.getName()); result.put("estimatedTotal", bestTotal);
        result.put("historicalPriceCoverage", bestHistory + "/" + lines.size());
        result.put("confidence", BigDecimal.valueOf(0.55 + (0.40 * bestHistory / (double) lines.size())).setScale(2, java.math.RoundingMode.HALF_UP));
        result.put("riskLevel", bestHistory == 0 ? "MEDIUM" : "LOW");
        result.put("summary", bestHistory == lines.size() ? "Recommendation is based on historical supplier pricing for all requisition lines." : "Recommendation uses historical pricing where available and product cost price as fallback.");
        result.put("lines", bestLines); result.put("createsPurchaseOrder", false); result.put("autoApprovesPurchaseOrder", false);
        result.put("nextAction", "Review the recommendation, then generate a DRAFT Purchase Order using the recommended supplier.");
        return result;
    }
}
