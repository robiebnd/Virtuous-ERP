package com.digipals.wms.supplierquotation.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.productsupplieridentifier.repository.ProductSupplierIdentifierRepository;
import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import com.digipals.wms.supplierquotation.repository.SupplierQuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotationResolutionService {

    private final SupplierQuotationRepository quotationRepository;
    private final ProductSupplierIdentifierRepository identifierRepository;
    private final ProductRepository productRepository;
    private final QuotationAiService quotationAiService;

    public Map<String, Object> resolve(UUID quotationId) {
        SupplierQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier quotation not found."));

        Map<String, Object> extraction = quotationAiService.extractLinesFromQuotation(quotationId);
        Object rawLines = extraction.get("lines");
        if (!(rawLines instanceof List<?> extractedLines) || extractedLines.isEmpty()) {
            throw new IllegalStateException("No quotation lines were extracted.");
        }

        List<Map<String, Object>> lines = new ArrayList<>();
        boolean hasUnresolved = false;
        boolean hasConflict = false;
        int lineNumber = 0;

        for (Object rawLine : extractedLines) {
            lineNumber++;
            if (!(rawLine instanceof Map<?, ?> raw)) {
                throw new IllegalStateException("Invalid extracted quotation line at line " + lineNumber + ".");
            }

            String supplierItemCode = text(raw.get("supplierItemCode"));
            String legacySku = text(raw.get("sku"));
            String description = text(raw.get("description"));
            BigDecimal quantity = decimal(raw.get("quantity"));
            BigDecimal unitPrice = decimal(raw.get("unitPrice"));
            String currency = text(raw.get("currency"));

            Product product = null;
            ProductSupplierIdentifier identifier = null;
            String resolutionStatus;

            if (supplierItemCode != null && !supplierItemCode.isBlank()) {
                identifier = identifierRepository
                        .findBySupplierIdAndSupplierItemCodeIgnoreCase(
                                quotation.getSupplier().getId(), supplierItemCode)
                        .orElse(null);
                if (identifier != null) {
                    product = identifier.getProduct();
                    if (!Boolean.TRUE.equals(identifier.getActive())) {
                        resolutionStatus = "INACTIVE_MAPPING";
                        hasConflict = true;
                    } else {
                        resolutionStatus = "RESOLVED";
                    }
                } else {
                    resolutionStatus = "UNRESOLVED";
                    hasUnresolved = true;
                }
            } else if (legacySku != null && !legacySku.isBlank()) {
                product = productRepository.findBySku(legacySku).orElse(null);
                resolutionStatus = product == null ? "UNRESOLVED" : "RESOLVED";
                if (product == null) hasUnresolved = true;
            } else {
                resolutionStatus = "UNRESOLVED";
                hasUnresolved = true;
            }

            // If both identities are present, they must agree. Never silently choose one.
            if (product != null && legacySku != null && !legacySku.isBlank()
                    && !legacySku.equalsIgnoreCase(product.getSku())) {
                resolutionStatus = "CONFLICT";
                hasConflict = true;
            }

            Map<String, Object> line = new LinkedHashMap<>();
            line.put("lineNumber", lineNumber);
            line.put("supplierItemCode", supplierItemCode);
            line.put("supplierItemName", identifier == null ? null : identifier.getSupplierItemName());
            line.put("description", description);
            line.put("quantity", quantity);
            line.put("unitPrice", unitPrice);
            line.put("currency", currency);
            line.put("resolutionStatus", resolutionStatus);
            line.put("productId", product == null ? null : product.getId());
            line.put("sku", product == null ? legacySku : product.getSku());
            line.put("productName", product == null ? null : product.getName());
            lines.add(line);
        }

        String overallStatus = hasConflict
                ? "REVIEW_REQUIRED"
                : hasUnresolved ? "MAPPING_REQUIRED" : "READY";

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("quotationId", quotation.getId());
        response.put("quotationNumber", quotation.getQuotationNumber());
        response.put("supplierId", quotation.getSupplier().getId());
        response.put("supplierCode", quotation.getSupplier().getCode());
        response.put("supplierName", quotation.getSupplier().getName());
        response.put("status", overallStatus);
        response.put("lines", lines);
        return response;
    }

    private String text(Object value) {
        return value == null ? null : value.toString().trim();
    }

    private BigDecimal decimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return new BigDecimal(number.toString());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
