package com.digipals.wms.supplierquotation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import com.digipals.wms.supplierquotation.entity.SupplierQuotationStatus;
import com.digipals.wms.supplierquotation.repository.SupplierQuotationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationAiServiceImpl implements QuotationAiService {

    private final SupplierQuotationRepository quotationRepository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository requisitionLineRepository;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    @Value("${openai.base-url:https://api.openai.com/v1/responses}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recommend(UUID purchaseRequisitionId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new InvalidWorkflowException("OPENAI_API_KEY is not configured.");
        }

        PurchaseRequisition requisition = requisitionRepository.findById(purchaseRequisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));

        List<SupplierQuotation> quotations = quotationRepository
                .findByPurchaseRequisitionIdOrderByCreatedAtDesc(purchaseRequisitionId);

        if (quotations.isEmpty()) {
            throw new ResourceNotFoundException("No supplier quotations have been uploaded for this Purchase Requisition.");
        }

        List<PurchaseRequisitionLine> lines = requisitionLineRepository
                .findByPurchaseRequisitionId(purchaseRequisitionId);

        String prompt = buildPrompt(requisition, lines, quotations);
        String json = callOpenAi(prompt);

        try {
            JsonNode result = objectMapper.readTree(json);
            String quotationId = result.path("recommendedQuotationId").asText(null);
            String reason = result.path("reason").asText(null);

            SupplierQuotation selected = quotations.stream()
                    .filter(q -> q.getId().toString().equals(quotationId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidWorkflowException("AI returned an invalid quotation selection."));

            return new LinkedHashMap<>(Map.of(
                    "recommendedQuotationId", selected.getId(),
                    "quotationNumber", selected.getQuotationNumber(),
                    "supplierName", selected.getSupplier().getName(),
                    "reason", reason == null || reason.isBlank()
                            ? "Recommended based on the uploaded quotation and Purchase Requisition."
                            : reason
            ));
        } catch (IOException e) {
            throw new InvalidWorkflowException("AI quotation analysis returned an invalid response.");
        }
    }

    private String buildPrompt(PurchaseRequisition requisition,
                               List<PurchaseRequisitionLine> lines,
                               List<SupplierQuotation> quotations) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are assisting a procurement officer. Compare supplier quotations uploaded for one Purchase Requisition. ")
                .append("Recommend the quotation that best satisfies the requisition, considering quoted price, quantities, delivery terms, payment terms and other explicit supplier terms. ")
                .append("Do not invent missing values. If information is unavailable, use only what is present in the documents. ")
                .append("Return JSON only with fields recommendedQuotationId and reason.\n\n");

        prompt.append("PURCHASE REQUISITION: ")
                .append(requisition.getRequisitionNumber()).append("\n");
        prompt.append("REQUIRED LINES:\n");
        for (PurchaseRequisitionLine line : lines) {
            prompt.append("- ")
                    .append(line.getProduct().getSku()).append(" | ")
                    .append(line.getProduct().getName()).append(" | qty ")
                    .append(line.getQuantity()).append("\n");
        }

        prompt.append("\nUPLOADED QUOTATIONS:\n");
        for (SupplierQuotation quotation : quotations) {
            prompt.append("\nQUOTATION ID: ").append(quotation.getId()).append("\n")
                    .append("QUOTATION NUMBER: ").append(quotation.getQuotationNumber()).append("\n")
                    .append("SUPPLIER: ").append(quotation.getSupplier().getName()).append("\n")
                    .append("DOCUMENT TEXT:\n").append(extractPdfText(quotation)).append("\n");
        }

        return prompt.toString();
    }

    private String extractPdfText(SupplierQuotation quotation) {
        try {
            Path path = Paths.get(quotation.getFilePath()).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                throw new InvalidWorkflowException("Quotation document is missing: " + quotation.getOriginalFileName());
            }

            try (PDDocument document = Loader.loadPDF(path.toFile())) {
                String text = new PDFTextStripper().getText(document);
                return text.length() > 12000 ? text.substring(0, 12000) : text;
            }
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation document: " + quotation.getOriginalFileName());
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
                throw new InvalidWorkflowException("AI quotation analysis failed with HTTP " + response.statusCode() + ".");
            }

            JsonNode root = objectMapper.readTree(response.body());
            for (JsonNode output : root.path("output")) {
                for (JsonNode content : output.path("content")) {
                    if ("output_text".equals(content.path("type").asText())) {
                        return content.path("text").asText();
                    }
                }
            }

            throw new InvalidWorkflowException("AI quotation analysis returned no text.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InvalidWorkflowException("AI quotation analysis was interrupted.");
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to connect to the AI quotation analysis service.");
        }
    }
}
