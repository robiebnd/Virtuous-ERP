package com.digipals.wms.supplierquotation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    private final SupplierQuotationRepository quotationRepository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository requisitionLineRepository;
    private final SupplierRepository supplierRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key:}") private String apiKey;
    @Value("${openai.model:gpt-4.1-mini}") private String model;
    @Value("${openai.base-url:https://api.openai.com/v1/responses}") private String baseUrl;
    @Value("${procurement.ai.extraction-mode:mock}") private String extractionMode;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommend(UUID purchaseRequisitionId) {
        if (apiKey == null || apiKey.isBlank()) throw new InvalidWorkflowException("OPENAI_API_KEY is not configured.");
        PurchaseRequisition requisition = requisitionRepository.findById(purchaseRequisitionId).orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        List<SupplierQuotation> quotations = quotationRepository.findByPurchaseRequisitionIdOrderByCreatedAtDesc(purchaseRequisitionId);
        if (quotations.isEmpty()) throw new ResourceNotFoundException("No supplier quotations have been uploaded for this Purchase Requisition.");
        List<PurchaseRequisitionLine> lines = requisitionLineRepository.findByPurchaseRequisitionId(purchaseRequisitionId);
        try {
            JsonNode result = objectMapper.readTree(callOpenAi(buildPrompt(requisition, lines, quotations)));
            String quotationId = result.path("recommendedQuotationId").asText(null);
            String reason = result.path("reason").asText(null);
            SupplierQuotation selected = quotations.stream().filter(q -> q.getId().toString().equals(quotationId)).findFirst().orElseThrow(() -> new InvalidWorkflowException("AI returned an invalid quotation selection."));
            return new LinkedHashMap<>(Map.of("recommendedQuotationId", selected.getId(), "quotationNumber", selected.getQuotationNumber(), "supplierName", selected.getSupplier().getName(), "reason", reason == null || reason.isBlank() ? "Recommended based on the uploaded quotation and Purchase Requisition." : reason));
        } catch (IOException e) { throw new InvalidWorkflowException("AI quotation analysis returned an invalid response."); }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> extractLines(UUID supplierId, MultipartFile file) {
        if (supplierId == null) throw new InvalidWorkflowException("Supplier is required.");
        if (file == null || file.isEmpty()) throw new InvalidWorkflowException("Quotation PDF is required.");
        if (file.getSize() > 10 * 1024 * 1024) throw new InvalidWorkflowException("Quotation PDF must not exceed 10 MB.");
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename() == null ? "quotation.pdf" : file.getOriginalFilename();
        if (!filename.toLowerCase().endsWith(".pdf") && !"application/pdf".equalsIgnoreCase(contentType)) throw new InvalidWorkflowException("Quotation file must be a PDF.");
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));
        try { return extractLinesFromBytes(supplier, filename, file.getBytes()); }
        catch (IOException e) { throw new InvalidWorkflowException("Unable to read quotation PDF."); }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> extractLinesFromQuotation(UUID quotationId) {
        SupplierQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier quotation not found."));
        try {
            Path path = Paths.get(quotation.getFilePath()).toAbsolutePath().normalize();
            if (!Files.exists(path)) throw new ResourceNotFoundException("Quotation document not found: " + quotation.getOriginalFileName());
            return extractLinesFromBytes(quotation.getSupplier(), quotation.getOriginalFileName(), Files.readAllBytes(path));
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF.");
        }
    }

    private Map<String, Object> extractLinesFromBytes(Supplier supplier, String filename, byte[] bytes) {
        if (bytes.length > 10 * 1024 * 1024) throw new InvalidWorkflowException("Quotation PDF must not exceed 10 MB.");
        final String text;
        try (PDDocument document = Loader.loadPDF(bytes)) { text = new PDFTextStripper().getText(document); }
        catch (IOException e) { throw new InvalidWorkflowException("Unable to read quotation PDF."); }
        if (text == null || text.isBlank()) throw new InvalidWorkflowException("Quotation PDF contains no readable text.");

        if ("mock".equalsIgnoreCase(extractionMode)) return buildMockExtractionResponse(supplier, filename, text);
        if (apiKey == null || apiKey.isBlank()) throw new InvalidWorkflowException("OPENAI_API_KEY is not configured.");
        try { return parseExtractionResponse(supplier, filename, callOpenAi(buildExtractionPrompt(supplier, filename, text))); }
        catch (IOException e) { throw new InvalidWorkflowException("AI quotation extraction returned an invalid response."); }
    }

    private Map<String, Object> buildMockExtractionResponse(Supplier supplier, String filename, String text) {
        String normalized = text.replace('\u00A0', ' ').replace('\r', '\n');
        String quotationNumber = firstGroup(normalized, "(?i)quotation\\s*(?:no\\.?|number)\\s*[:\\-]?\\s*([A-Z0-9][A-Z0-9\\-_/]+)");
        String quotationDate = firstGroup(normalized, "(?i)quotation\\s*date\\s*[:\\-]?\\s*(\\d{1,2}\\s+[A-Za-z]+\\s+\\d{4})");
        String currency = firstGroup(normalized, "(?i)currency\\s*[:\\-]?\\s*([A-Z]{3})");
        if (currency == null) currency = firstGroup(normalized, "(?i)\\b(USD|ZWL|ZAR|EUR|GBP)\\b");

        List<Map<String, Object>> lines = new ArrayList<>();
        Pattern linePattern = Pattern.compile("(?m)^\\s*(?:\\d+\\s+)?(PRD[A-Z0-9\\-]+)\\s+(.+?)\\s+(\\d+(?:\\.\\d+)?)\\s+(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)(?:\\s+([A-Z]{3}))?\\s+(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)\\s*$");
        Matcher matcher = linePattern.matcher(normalized);
        while (matcher.find()) addMockLine(lines, matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), currency);

        // PDF table extraction can place each cell on its own line. In that case,
        // use a whitespace-normalized fallback that anchors on the SKU and numeric columns.
        if (lines.isEmpty()) {
            String flat = normalized.replaceAll("\\s+", " ").trim();
            Pattern flatPattern = Pattern.compile("(?i)(PRD[A-Z0-9\\-]+)\\s+(.+?)\\s+(\\d+(?:\\.\\d+)?)\\s+(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)\\s+(?:([A-Z]{3})\\s+)?(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)");
            Matcher flatMatcher = flatPattern.matcher(flat);
            while (flatMatcher.find()) addMockLine(lines, flatMatcher.group(1), flatMatcher.group(2), flatMatcher.group(3), flatMatcher.group(4), flatMatcher.group(5), currency);
        }

        if (lines.isEmpty()) throw new InvalidWorkflowException("Mock extraction could not identify quotation lines in the supplied PDF. Expected rows containing SKU, description, quantity and unit price.");

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

    private void addMockLine(List<Map<String, Object>> lines, String sku, String description, String quantity, String unitPrice, String lineCurrency, String defaultCurrency) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("description", description.trim());
        line.put("sku", sku.trim());
        line.put("quantity", decimal(quantity));
        line.put("unitPrice", decimal(unitPrice));
        line.put("currency", lineCurrency == null ? defaultCurrency : lineCurrency.toUpperCase());
        lines.add(line);
    }

    private String firstGroup(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private java.math.BigDecimal decimal(String value) {
        return new java.math.BigDecimal(value.replace(",", ""));
    }

    private Map<String, Object> parseExtractionResponse(Supplier supplier, String filename, String json) throws IOException {
        JsonNode result = objectMapper.readTree(json); JsonNode lineNodes = result.path("lines");
        if (!lineNodes.isArray() || lineNodes.isEmpty()) throw new InvalidWorkflowException("AI could not extract any quotation lines from the PDF.");
        List<Map<String, Object>> lines = new ArrayList<>();
        for (JsonNode line : lineNodes) { Map<String, Object> item = new LinkedHashMap<>(); item.put("description", nullableText(line, "description")); item.put("sku", nullableText(line, "sku")); item.put("quantity", nullableNumber(line, "quantity")); item.put("unitPrice", nullableNumber(line, "unitPrice")); item.put("currency", nullableText(line, "currency")); lines.add(item); }
        Map<String, Object> response = new LinkedHashMap<>(); response.put("supplierId", supplier.getId()); response.put("supplierName", supplier.getName()); response.put("quotationNumber", nullableText(result, "quotationNumber")); response.put("quotationDate", nullableText(result, "quotationDate")); response.put("lines", lines); response.put("sourceFileName", filename); response.put("extractionMode", "OPENAI"); return response;
    }

    private String buildExtractionPrompt(Supplier supplier, String filename, String text) { return "You extract structured data from supplier quotation documents for a procurement system. Extract only information explicitly present in the quotation. Never invent SKUs, quantities, prices, currencies or dates. Return JSON only with fields quotationNumber, quotationDate and lines. Each line must contain description, sku, quantity, unitPrice and currency. Use null when a field is not present or cannot be determined. Do not calculate missing values. The supplier is already known by the system as '" + supplier.getName() + "'. SOURCE FILE: " + filename + "\n\nQUOTATION TEXT:\n" + text.substring(0, Math.min(text.length(), 16000)); }
    private String nullableText(JsonNode node, String field) { JsonNode value = node.get(field); return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText(); }
    private Object nullableNumber(JsonNode node, String field) { JsonNode value = node.get(field); return value == null || value.isNull() || !value.isNumber() ? null : value.numberValue(); }

    private String buildPrompt(PurchaseRequisition requisition, List<PurchaseRequisitionLine> lines, List<SupplierQuotation> quotations) {
        StringBuilder prompt = new StringBuilder("You are assisting a procurement officer. Compare supplier quotations uploaded for one Purchase Requisition. Recommend the quotation that best satisfies the requisition, considering quoted price, quantities, delivery terms, payment terms and other explicit supplier terms. Do not invent missing values. Return JSON only with fields recommendedQuotationId and reason.\n\n");
        prompt.append("PURCHASE REQUISITION: ").append(requisition.getRequisitionNumber()).append("\nREQUIRED LINES:\n");
        for (PurchaseRequisitionLine line : lines) prompt.append("- ").append(line.getProduct().getSku()).append(" | ").append(line.getProduct().getName()).append(" | qty ").append(line.getQuantity()).append("\n");
        prompt.append("\nUPLOADED QUOTATIONS:\n");
        for (SupplierQuotation quotation : quotations) prompt.append("\nQUOTATION ID: ").append(quotation.getId()).append("\nQUOTATION NUMBER: ").append(quotation.getQuotationNumber()).append("\nSUPPLIER: ").append(quotation.getSupplier().getName()).append("\nDOCUMENT TEXT:\n").append(extractPdfText(quotation)).append("\n");
        return prompt.toString();
    }

    private String extractPdfText(SupplierQuotation quotation) {
        try { Path path = Paths.get(quotation.getFilePath()).toAbsolutePath().normalize(); if (!Files.exists(path)) throw new InvalidWorkflowException("Quotation document is missing: " + quotation.getOriginalFileName()); try (PDDocument document = Loader.loadPDF(path.toFile())) { String text = new PDFTextStripper().getText(document); return text.length() > 12000 ? text.substring(0, 12000) : text; } }
        catch (IOException e) { throw new InvalidWorkflowException("Unable to read quotation document: " + quotation.getOriginalFileName()); }
    }

    private String callOpenAi(String prompt) {
        try {
            Map<String, Object> body = new LinkedHashMap<>(); body.put("model", model); body.put("input", prompt);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl)).header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body))).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) throw providerException(response.statusCode(), response.body());
            JsonNode root = objectMapper.readTree(response.body());
            for (JsonNode output : root.path("output")) for (JsonNode content : output.path("content")) if ("output_text".equals(content.path("type").asText())) return content.path("text").asText();
            throw new InvalidWorkflowException("AI quotation analysis returned no text.");
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InvalidWorkflowException("AI quotation analysis was interrupted."); }
        catch (IOException e) { throw new InvalidWorkflowException("Unable to connect to the AI quotation analysis service."); }
    }

    private InvalidWorkflowException providerException(int status, String body) {
        String type = null, code = null, message = null;
        try { JsonNode error = objectMapper.readTree(body).path("error"); type = nullableText(error, "type"); code = nullableText(error, "code"); message = nullableText(error, "message"); } catch (Exception ignored) { }
        StringBuilder detail = new StringBuilder("OpenAI rejected the request with HTTP ").append(status).append(": ").append(message == null ? "No provider message was returned." : message); if (type != null) detail.append(" [type=").append(type).append("]"); if (code != null) detail.append(" [code=").append(code).append("]"); return new InvalidWorkflowException(detail.toString());
    }
}
