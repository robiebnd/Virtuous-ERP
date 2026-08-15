package com.digipals.wms.procurement.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.InvalidWorkflowException;
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
import java.math.RoundingMode;
import java.util.ArrayList;
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
        if (supplier == null) {
            throw new ResourceNotFoundException("No supplier is assigned to the Purchase Requisition.");
        }

        List<PurchaseRequisitionLine> lines = requisitionLineRepository.findByPurchaseRequisitionId(requisition.getId());
        if (lines.isEmpty()) {
            throw new ResourceNotFoundException("Purchase Requisition contains no lines.");
        }

        PurchaseOrder po = purchaseOrderRepository.save(PurchaseOrder.builder()
                .poNumber(documentNumberService.next(DocumentType.PURCHASE_ORDER))
                .supplier(supplier)
                .warehouse(requisition.getWarehouse())
                .purchaseRequisition(requisition)
                .source(ProcurementSource.REQUISITION)
                .status(PurchaseOrderStatus.DRAFT)
                .build());

        for (PurchaseRequisitionLine line : lines) {
            BigDecimal price = resolvePrice(supplier, line);
            if (!isValidPrice(price)) {
                throw new InvalidWorkflowException(
                        "Cannot generate Purchase Order: no valid price is available for product "
                                + line.getProduct().getSku() + " (" + line.getProduct().getName()
                                + "). Add a quoted PR price, supplier price, or product cost price before generating the PO.");
            }

            purchaseOrderLineRepository.save(PurchaseOrderLine.builder()
                    .purchaseOrder(po)
                    .product(line.getProduct())
                    .quantity(line.getQuantity())
                    .unitPrice(price.setScale(2, RoundingMode.HALF_UP))
                    .build());
        }

        requisition.setStatus(PurchaseRequisitionStatus.CONVERTED_TO_PO);
        requisitionRepository.save(requisition);
        return PurchaseOrderMapper.toResponse(po);
    }

    /**
     * Pricing priority for PO generation:
     * 1. The approved PR line's quoted/estimated unit cost.
     * 2. Latest historical supplier price.
     * 3. Product cost price.
     *
     * The approved PR price is authoritative because it is the price reviewed
     * and approved during the quotation -> PR workflow.
     */
    private BigDecimal resolvePrice(Supplier supplier, PurchaseRequisitionLine line) {
        if (isValidPrice(line.getEstimatedUnitCost())) {
            return line.getEstimatedUnitCost();
        }

        return purchaseOrderLineRepository
                .findLatestUnitPrice(supplier.getId(), line.getProduct().getId())
                .filter(this::isValidPrice)
                .orElse(line.getProduct().getCostPrice());
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommendPurchaseOrder(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        validator.validateApproved(pr);
        validator.validateNotConverted(pr);

        List<PurchaseRequisitionLine> lines = requisitionLineRepository.findByPurchaseRequisitionId(id);
        if (lines.isEmpty()) {
            throw new ResourceNotFoundException("Purchase Requisition contains no lines.");
        }

        List<Supplier> suppliers = supplierRepository.findByActiveTrue();
        if (suppliers.isEmpty()) {
            throw new ResourceNotFoundException("No active suppliers are available for recommendation.");
        }

        Supplier best = null;
        BigDecimal bestTotal = null;
        int bestHistory = -1;
        List<Map<String, Object>> bestLines = null;
        boolean bestFullyPriced = false;

        for (Supplier supplier : suppliers) {
            BigDecimal total = BigDecimal.ZERO;
            int history = 0;
            boolean fullyPriced = true;
            List<Map<String, Object>> candidateLines = new ArrayList<>();

            for (PurchaseRequisitionLine line : lines) {
                BigDecimal historical = purchaseOrderLineRepository
                        .findLatestUnitPrice(supplier.getId(), line.getProduct().getId())
                        .orElse(null);
                boolean usedHistory = isValidPrice(historical);
                BigDecimal costPrice = line.getProduct().getCostPrice();
                BigDecimal price = usedHistory ? historical : (isValidPrice(costPrice) ? costPrice : null);

                if (usedHistory) {
                    history++;
                }
                if (price == null) {
                    fullyPriced = false;
                } else {
                    total = total.add(line.getQuantity().multiply(price));
                }

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productId", line.getProduct().getId());
                item.put("sku", line.getProduct().getSku());
                item.put("productName", line.getProduct().getName());
                item.put("requestedQuantity", line.getQuantity());
                item.put("recommendedQuantity", line.getQuantity());
                item.put("recommendedUnitPrice", price);
                item.put("estimatedLineTotal", price == null ? null : line.getQuantity().multiply(price));
                item.put("historicalPriceUsed", usedHistory);
                item.put("pricingBasis", usedHistory
                        ? "LATEST_NON_CANCELLED_SUPPLIER_PRICE"
                        : (price == null ? "PRICE_UNAVAILABLE" : "PRODUCT_COST_PRICE_FALLBACK"));
                item.put("priceWarning", price == null);
                candidateLines.add(item);
            }

            boolean preferred = pr.getSupplier() != null && pr.getSupplier().getId().equals(supplier.getId());
            boolean better = false;

            if (best == null) {
                better = true;
            } else if (fullyPriced && !bestFullyPriced) {
                better = true;
            } else if (fullyPriced == bestFullyPriced) {
                if (fullyPriced) {
                    better = total.compareTo(bestTotal) < 0
                            || (total.compareTo(bestTotal) == 0 && history > bestHistory)
                            || (total.compareTo(bestTotal) == 0 && history == bestHistory && preferred);
                } else {
                    better = history > bestHistory
                            || (history == bestHistory && preferred);
                }
            }

            if (better) {
                best = supplier;
                bestTotal = fullyPriced ? total : null;
                bestHistory = history;
                bestLines = candidateLines;
                bestFullyPriced = fullyPriced;
            }
        }

        boolean hasRecommendation = best != null;
        boolean canGeneratePurchaseOrder = bestFullyPriced;
        BigDecimal confidence = calculateConfidence(lines.size(), bestHistory, bestFullyPriced);
        String riskLevel = calculateRiskLevel(bestFullyPriced, bestHistory, lines.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("purchaseRequisitionId", pr.getId());
        result.put("requisitionNumber", pr.getRequisitionNumber());
        result.put("recommendedSupplierId", hasRecommendation ? best.getId() : null);
        result.put("recommendedSupplierCode", hasRecommendation ? best.getCode() : null);
        result.put("recommendedSupplierName", hasRecommendation ? best.getName() : null);
        result.put("supplierRecommendationBasis", bestFullyPriced
                ? "LOWEST_SUPPORTED_ESTIMATED_COST"
                : "BEST_AVAILABLE_PRICING_EVIDENCE");
        result.put("estimatedTotal", bestTotal);
        result.put("historicalPriceCoverage", Math.max(bestHistory, 0) + "/" + lines.size());
        result.put("confidence", confidence);
        result.put("riskLevel", riskLevel);
        result.put("priceAvailable", canGeneratePurchaseOrder);
        result.put("priceWarning", !canGeneratePurchaseOrder);
        result.put("summary", buildSummary(best, bestFullyPriced, bestHistory, lines.size()));
        result.put("lines", bestLines);
        result.put("createsPurchaseOrder", false);
        result.put("autoApprovesPurchaseOrder", false);
        result.put("canGeneratePurchaseOrder", canGeneratePurchaseOrder);
        result.put("nextAction", canGeneratePurchaseOrder
                ? "Review the recommendation, then generate a DRAFT Purchase Order using the recommended supplier."
                : "Obtain missing supplier quotations or product cost prices before generating the Purchase Order.");
        return result;
    }

    private BigDecimal calculateConfidence(int lineCount, int historicalLines, boolean fullyPriced) {
        if (lineCount <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        double coverage = Math.max(0, historicalLines) / (double) lineCount;
        double score = fullyPriced ? 0.60 + (0.35 * coverage) : 0.20 + (0.25 * coverage);
        return BigDecimal.valueOf(Math.min(score, 0.95)).setScale(2, RoundingMode.HALF_UP);
    }

    private String calculateRiskLevel(boolean fullyPriced, int historicalLines, int lineCount) {
        if (!fullyPriced) {
            return "HIGH";
        }
        if (historicalLines < lineCount) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String buildSummary(Supplier supplier, boolean fullyPriced, int historicalLines, int lineCount) {
        if (!fullyPriced) {
            return "A supplier candidate was identified, but one or more requisition lines have no valid historical supplier price or product cost price. A Purchase Order must not be generated until pricing is available.";
        }
        if (historicalLines == lineCount) {
            return "Recommendation is based on supported historical supplier pricing for all requisition lines.";
        }
        return "Recommendation uses historical supplier pricing where available and product cost price as fallback for the remaining lines.";
    }
}
