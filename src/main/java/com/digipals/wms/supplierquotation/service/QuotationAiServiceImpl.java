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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationAiServiceImpl implements QuotationAiService {

    private static final Pattern SUPPLIER_ITEM_CODE_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9._/-]*-[A-Za-z0-9._/-]+$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+(?:,[0-9]{3})*(?:\\.[0-9]+)?$");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?i)date\\s*:?\\s*([0-9]{1,2}\\s+[A-Za-z]+\\s+[0-9]{4}|[0-9]{4}-[0-9]{2}-[0-9]{2})");

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
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            text = stripper.getText(document);
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
        String quotationDate = findDate(normalized);

        for (String rawRow : normalized.split("\\n")) {
            Map<String, Object> line = parseQuotationRow(rawRow);
            if (line != null) {
                line.put("currency", currency);
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            throw new InvalidWorkflowException("Quotation extraction could not identify any line items. The PDF may use a different table layout or may be scanned/image-only. No supplier item, description, quantity and unit-price combination could be reliably extracted.");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("supplierId", supplier.getId());
        response.put("supplierName", supplier.getName());
        response.put("quotationNumber", quotationNumber);
        response.put("quotationDate", quotationDate);
        response.put("lines", lines);
        response.put("sourceFileName", filename);
        response.put("extractionMode", "MOCK");
        return response;
    }

    /**
     * Handles PDF table rows with or without a leading line number and with
     * optional line-total columns. The parser anchors on the supplier item
     * code rather than assuming that the first whitespace-delimited token is
     * the code.
     *
     * Examples:
     *   1 SC-BGF-001 Broiler Grower Feed 100 27.00 2700.00
     *   SC-BGF-001 Broiler Grower Feed 100 27.00
     */
    private Map<String, Object> parseQuotationRow(String rawRow) {
        if (rawRow == null || rawRow.isBlank()) return null;

        String row = rawRow.replace('\u00A0', ' ').trim();
        if (isQuotationHeader(row) || isNonItemRow(row)) return null;

        String[] tokens = row.split("\\s+");
        int codeIndex = -1;
        for (int i = 0; i < tokens.length; i++) {
            if (SUPPLIER_ITEM_CODE_PATTERN.matcher(tokens[i]).matches()) {
                codeIndex = i;
                break;
            }
        }
        if (codeIndex < 0) return null;

        String supplierItemCode = tokens[codeIndex].trim();
        List<Integer> numericIndexes = new ArrayList<>();
        for (int i = codeIndex + 1; i < tokens.length; i++) {
            if (NUMBER_PATTERN.matcher(tokens[i]).matches()) {
                numericIndexes.add(i);
            }
        }

        // A normal quotation row has quantity + unit price. A common table
        // also has a third number at the end for line total.
        if (numericIndexes.size() < 2) return null;

        int quantityIndex = numericIndexes.get(numericIndexes.size() - 2);
        int unitPriceIndex = numericIndexes.get(numericIndexes.size() - 1);

        // If there are three numeric values, the last one is normally line
        // total, so use the third-from-last as quantity and second-from-last
        // as unit price.
        if (numericIndexes.size() >= 3) {
            quantityIndex = numericIndexes.get(numericIndexes.size() - 3);
            unitPriceIndex = numericIndexes.get(numericIndexes.size() - 2);
        }

        if (quantityIndex <= codeIndex || unitPriceIndex <= quantityIndex) return null;

        StringBuilder descriptionBuilder = new StringBuilder();
        for (int i = codeIndex + 1; i < quantityIndex; i++) {
            if (descriptionBuilder.length() > 0) descriptionBuilder.append(' ');
            descriptionBuilder.append(tokens[i]);
        }
        String description = descriptionBuilder.toString().trim();
        if (description.isBlank()) return null;

        BigDecimal quantity = decimal(tokens[quantityIndex]);
        BigDecimal unitPrice = decimal(tokens[unitPriceIndex]);
        if (quantity == null || unitPrice == null) return null;

        Map<String, Object> line = new LinkedHashMap<>();
        line.put("supplierItemCode", supplierItemCode);
        line.put("description", description);
        line.put("quantity", quantity);
        line.put("unitPrice", unitPrice);
        return line;
    }

    private boolean isQuotationHeader(String row) {
        String normalized = row.toLowerCase().replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        return normalized.equals("no supplier item code description qty unit price line total")
                || normalized.equals("supplier item code description qty unit price line total")
                || normalized.equals("item code description qty unit price")
                || normalized.equals("supplier item code description quantity unit price")
                || normalized.startsWith("no supplier item code")
                || normalized.startsWith("supplier item code");
    }

    private boolean isNonItemRow(String row) {
        String lower = row.toLowerCase();
        return lower.startsWith("subtotal")
                || lower.startsWith("total")
                || lower.startsWith("tax")
                || lower.startsWith("quotation no")
                || lower.startsWith("quotation number")
                || lower.startsWith("date ")
                || lower.startsWith("valid until")
                || lower.startsWith("customer")
                || lower.startsWith("delivery")
                || lower.startsWith("currency")
                || lower.startsWith("supplier notes")
                || lower.startsWith("prices are")
                || lower.startsWith("thank you");
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

        // PDFBox can place the following Date field on the same extracted
        // line. Stop at the next known metadata label rather than returning
        // the date as part of the quotation number.
        Matcher nextLabel = Pattern.compile("(?i)\\s+(?:date|valid until|currency|customer|delivery)\\s*:?").matcher(tail);
        if (nextLabel.find()) {
            tail = tail.substring(0, nextLabel.start()).trim();
        }

        int end = tail.indexOf('\n');
        return (end >= 0 ? tail.substring(0, end) : tail).trim();
    }

    private String findDate(String text) {
        Matcher matcher = DATE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
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
