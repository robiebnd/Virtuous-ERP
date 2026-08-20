package com.digipals.wms.supplierquotation.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import com.digipals.wms.supplierquotation.repository.SupplierQuotationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Quotation extraction service.
 *
 * Important: extraction preserves the supplier's item identifier as
 * supplierItemCode. Product-to-ERP-SKU resolution is performed later when
 * importing the quotation into a Purchase Requisition, using the
 * supplier/product identifier mapping.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuotationAiServiceImpl implements QuotationAiService {

    private final SupplierQuotationRepository quotationRepository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository requisitionLineRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommend(UUID purchaseRequisitionId) {
        throw new InvalidWorkflowException("Quotation recommendation remains available through the configured AI provider and is not part of supplier-item-code mapping.");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> extractLines(UUID supplierId, MultipartFile file) {
        if (supplierId == null) throw new InvalidWorkflowException("Supplier is required.");
        if (file == null || file.isEmpty()) throw new InvalidWorkflowException("Quotation PDF is required.");
        if (file.getSize() > 10 * 1024 * 1024) throw new InvalidWorkflowException("Quotation PDF must not exceed 10 MB.");

        String filename = file.getOriginalFilename() == null ? "quotation.pdf" : file.getOriginalFilename();
        String contentType = file.getContentType();
        if (!filename.toLowerCase().endsWith(".pdf") && !"application/pdf".equalsIgnoreCase(contentType)) {
            throw new InvalidWorkflowException("Quotation file must be a PDF.");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        try {
            return extractLinesFromBytes(supplier, filename, file.getBytes());
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> extractLinesFromQuotation(UUID quotationId) {
        SupplierQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier quotation not found."));
        try {
            Path path = Paths.get(quotation.getFilePath()).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                throw new ResourceNotFoundException("Quotation document not found: " + quotation.getOriginalFileName());
            }
            return extractLinesFromBytes(quotation.getSupplier(), quotation.getOriginalFileName(), Files.readAllBytes(path));
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF.");
        }
    }

    private Map<String, Object> extractLinesFromBytes(Supplier supplier, String filename, byte[] bytes) {
        if (bytes.length > 10 * 1024 * 1024) {
            throw new InvalidWorkflowException("Quotation PDF must not exceed 10 MB.");
        }

        final String text;
        try (PDDocument document = Loader.loadPDF(bytes)) {
            text = new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF.");
        }

        if (text == null || text.isBlank()) {
            throw new InvalidWorkflowException("Quotation PDF contains no readable text.");
        }

        return buildMockExtractionResponse(supplier, filename, text);
    }

    private Map<String, Object> buildMockExtractionResponse(Supplier supplier, String filename, String text) {
        String normalized = text.replace('\u00A0', ' ').replace('\r', '\n');
        List<Map<String, Object>> lines = new ArrayList<>();

        String currency = findCurrency(normalized);
        String quotationNumber = findAfterLabel(normalized, "quotation number");
        if (quotationNumber == null) quotationNumber = findAfterLabel(normalized, "quotation no");

        String[] rawRows = normalized.split("\\n");
        for (String rawRow : rawRows) {
            String row = rawRow.trim();
            if (row.isBlank()) continue;
            String[] cells = row.split("\\s{2,}|\\t");
            if (cells.length < 4) continue;

            String candidateCode = cells[0].trim();
            if (candidateCode.equalsIgnoreCase("SKU") || candidateCode.equalsIgnoreCase("ITEM") || candidateCode.equalsIgnoreCase("CODE")) continue;

            BigDecimal quantity = decimal(lastBeforeLast(cells));
            BigDecimal unitPrice = decimal(cells[cells.length - 1]);
            if (quantity == null || unitPrice == null) continue;

            String supplierItemCode = candidateCode;
            String description = String.join(" ", slice(cells, 1, cells.length - 2)).trim();
            if (supplierItemCode.isBlank() || description.isBlank()) continue;

            Map<String, Object> line = new LinkedHashMap<>();
            line.put("supplierItemCode", supplierItemCode);
            line.put("description", description);
            line.put("quantity", quantity);
            line.put("unitPrice", unitPrice);
            line.put("currency", currency);
            lines.add(line);
        }

        if (lines.isEmpty()) {
            throw new InvalidWorkflowException("Mock extraction could not identify quotation lines in the supplied PDF. Expected rows containing supplier item code, description, quantity and unit price.");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("supplierId", supplier.getId());
        response.put("supplierName", supplier.getName());
        response.put("quotationNumber", quotationNumber);
        response.put("quotationDate", null);
        response.put("lines", lines);
        response.put("sourceFileName", filename);
        response.put("extractionMode", "MOCK");
        return response;
    }

    private String findCurrency(String text) {
        String upper = text.toUpperCase();
        for (String code : List.of("USD", "ZWL", "ZAR", "EUR", "GBP")) {
            if (upper.contains(code)) return code;
        }
        return null;
    }

    private String findAfterLabel(String text, String label) {
        String lower = text.toLowerCase();
        int index = lower.indexOf(label.toLowerCase());
        if (index < 0) return null;
        String tail = text.substring(index + label.length()).trim();
        if (tail.startsWith(":")) tail = tail.substring(1).trim();
        int end = tail.indexOf('\n');
        return (end >= 0 ? tail.substring(0, end) : tail).trim();
    }

    private String[] slice(String[] values, int from, int toExclusive) {
        String[] result = new String[Math.max(0, toExclusive - from)];
        if (result.length > 0) System.arraycopy(values, from, result, 0, result.length);
        return result;
    }

    private String lastBeforeLast(String[] cells) {
        return cells.length >= 2 ? cells[cells.length - 2].trim() : null;
    }

    private BigDecimal decimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
