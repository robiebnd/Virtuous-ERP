package com.digipals.wms.supplierquotation.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotationAiServiceImpl implements QuotationAiService {

    private final SupplierRepository supplierRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    @Value("${openai.base-url:https://api.openai.com/v1/responses}")
    private String baseUrl;

    @Override
    public Map<String, Object> extractLines(UUID supplierId, MultipartFile file) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new InvalidWorkflowException("OPENAI_API_KEY is not configured.");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidWorkflowException("Quotation file is required.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidWorkflowException("Quotation file must not exceed 10 MB.");
        }
        if (file.getContentType() != null && !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new InvalidWorkflowException("Only PDF supplier quotations are supported.");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        String documentText = extractPdfText(file);
        String prompt = buildExtractionPrompt(supplier, documentText);
        String json = callOpenAi(prompt);

        try {
            JsonNode root = objectMapper.readTree(json);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("supplierId", supplier.getId());
            response.put("supplierName", supplier.getName());
            response.put("quotationNumber", nullableText(root, "quotationNumber"));
            response.put("quotationDate", nullableText(root, "quotationDate"));

            List<Map<String, Object>> lines = new ArrayList<>();
            JsonNode extractedLines = root.path("lines");
            if (!extractedLines.isArray()) {
                throw new InvalidWorkflowException("AI quotation extraction returned no lines.");
            }

            for (JsonNode line : extractedLines) {
                String description = nullableText(line, "description");
                BigDecimal quantity = nullableDecimal(line, "quantity");
                BigDecimal unitPrice = nullableDecimal(line, "unitPrice");

                if (description == null || quantity == null || quantity.signum() <= 0) {
                    continue;
                }

                Map<String, Object> extracted = new LinkedHashMap<>();
                extracted.put("description", description);
                extracted.put("sku", nullableText(line, "sku"));
                extracted.put("quantity", quantity);
                extracted.put("unitPrice", unitPrice);
                extracted.put("currency", nullableText(line, "currency"));
                lines.add(extracted);
            }

            if (lines.isEmpty()) {
                throw new InvalidWorkflowException("No valid quotation lines could be extracted.");
            }

            response.put("lines", lines);
            response.put("sourceFileName", file.getOriginalFilename());
            return response;
        } catch (IOException e) {
            throw new InvalidWorkflowException("AI quotation extraction returned an invalid response.");
        }
    }

    private String buildExtractionPrompt(Supplier supplier, String documentText) {
        return "You extract structured purchasing data from a supplier quotation. "
                + "The result will be reviewed by a procurement user while creating a Purchase Requisition. "
                + "Extract only information explicitly present in the quotation. Do not invent products, SKUs, quantities, prices, dates or currency. "
                + "Return JSON only in this exact shape: "
                + "{\"quotationNumber\":string|null,\"quotationDate\":string|null,\"lines\":[{\"description\":string,\"sku\":string|null,\"quantity\":number,\"unitPrice\":number|null,\"currency\":string|null}]}. "
                + "Do not calculate or recommend quantities. Do not select a supplier. Do not create a Purchase Requisition or Purchase Order. "
                + "Supplier in the ERP: " + supplier.getName() + "\n\n"
                + "QUOTATION DOCUMENT TEXT:\n" + documentText;
    }

    private String extractPdfText(MultipartFile file) {
        try {
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                String text = new PDFTextStripper().getText(document);
                if (text == null || text.isBlank()) {
                    throw new InvalidWorkflowException("The quotation PDF contains no readable text.");
                }
                return text.length() > 16000 ? text.substring(0, 16000) : text;
            }
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF.");
        }
    }

    private String callOpenAi(String prompt) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("input", prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new InvalidWorkflowException("AI quotation extraction failed with HTTP " + response.statusCode() + ".");
            }

            JsonNode root = objectMapper.readTree(response.body());
            for (JsonNode output : root.path("output")) {
                for (JsonNode content : output.path("content")) {
                    if ("output_text".equals(content.path("type").asText())) {
                        return content.path("text").asText();
                    }
                }
            }

            throw new InvalidWorkflowException("AI quotation extraction returned no text.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InvalidWorkflowException("AI quotation extraction was interrupted.");
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to connect to the AI quotation extraction service.");
        }
    }

    private String nullableText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText().trim();
    }

    private BigDecimal nullableDecimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !value.isNumber()) {
            return null;
        }
        return value.decimalValue();
    }
}
