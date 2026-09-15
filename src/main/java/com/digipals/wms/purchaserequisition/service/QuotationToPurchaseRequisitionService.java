package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.purchaserequisition.validator.PurchaseRequisitionValidator;
import com.digipals.wms.common.mapper.PurchaseRequisitionMapper;
import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import com.digipals.wms.supplierquotation.repository.SupplierQuotationRepository;
import com.digipals.wms.supplierquotation.service.QuotationAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationToPurchaseRequisitionService {
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseRequisitionLineRepository lineRepository;
    private final PurchaseRequisitionValidator validator;
    private final SupplierQuotationRepository quotationRepository;
    private final ProductRepository productRepository;
    private final QuotationAiService quotationAiService;

    public PurchaseRequisitionResponse importLines(UUID requisitionId, UUID quotationId) {
        PurchaseRequisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
        validator.validateDraft(requisition);

        SupplierQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier quotation not found."));

        if (!quotation.getPurchaseRequisition().getId().equals(requisitionId)) {
            throw new IllegalStateException("Supplier quotation does not belong to this Purchase Requisition.");
        }
        if (requisition.getSupplier() == null || !quotation.getSupplier().getId().equals(requisition.getSupplier().getId())) {
            throw new IllegalStateException("Supplier quotation supplier does not match the Purchase Requisition supplier.");
        }

        final byte[] bytes;
        try {
            Path path = Paths.get(quotation.getFilePath()).toAbsolutePath().normalize();
            if (!Files.exists(path)) throw new ResourceNotFoundException("Quotation file not found.");
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read supplier quotation file.");
        }

        MultipartFile file = new StoredQuotationMultipartFile(quotation.getOriginalFileName(), quotation.getContentType(), bytes);
        Map<String, Object> extraction = quotationAiService.extractLines(quotation.getSupplier().getId(), file);
        Object rawLines = extraction.get("lines");
        if (!(rawLines instanceof List<?> lines) || lines.isEmpty()) {
            throw new IllegalStateException("No quotation lines were extracted.");
        }

        String detectedCurrency = null;
        int added = 0;
        for (Object raw : lines) {
            if (!(raw instanceof Map<?, ?> line)) continue;
            String sku = text(line.get("sku"));
            if (sku == null || sku.isBlank()) {
                throw new IllegalStateException("A quotation line has no SKU. Review the quotation before importing it.");
            }

            String lineCurrency = text(line.get("currency"));
            if (lineCurrency != null && !lineCurrency.isBlank()) {
                lineCurrency = lineCurrency.trim().toUpperCase();
                if (lineCurrency.length() != 3) {
                    throw new IllegalStateException("Invalid currency on quotation SKU: " + sku + ". Currency must be a 3-letter ISO code.");
                }
                if (detectedCurrency == null) {
                    detectedCurrency = lineCurrency;
                } else if (!detectedCurrency.equals(lineCurrency)) {
                    throw new IllegalStateException("Quotation contains multiple currencies. A Purchase Requisition must use one currency.");
                }
            }

            Product product = productRepository.findBySku(sku)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for quotation SKU: " + sku));
            java.math.BigDecimal quantity = decimal(line.get("quantity"));
            if (quantity == null || quantity.signum() <= 0) {
                throw new IllegalStateException("Invalid quantity for quotation SKU: " + sku);
            }
            java.math.BigDecimal unitPrice = decimal(line.get("unitPrice"));
            lineRepository.save(PurchaseRequisitionLine.builder()
                    .purchaseRequisition(requisition)
                    .product(product)
                    .quantity(quantity)
                    .estimatedUnitCost(unitPrice)
                    .remarks("Imported from supplier quotation " + quotation.getQuotationNumber())
                    .build());
            added++;
        }

        if (added == 0) throw new IllegalStateException("No valid quotation lines could be imported.");
        if (detectedCurrency != null) {
            requisition.setCurrency(detectedCurrency);
            requisitionRepository.save(requisition);
        }
        return PurchaseRequisitionMapper.toResponse(requisition);
    }

    private static String text(Object value) { return value == null ? null : value.toString(); }
    private static java.math.BigDecimal decimal(Object value) {
        if (value == null) return null;
        try { return new java.math.BigDecimal(value.toString()); }
        catch (NumberFormatException e) { return null; }
    }

    private record StoredQuotationMultipartFile(String name, String contentType, byte[] bytes) implements MultipartFile {
        public String getName() { return name; }
        public String getOriginalFilename() { return name; }
        public String getContentType() { return contentType == null ? "application/pdf" : contentType; }
        public boolean isEmpty() { return bytes.length == 0; }
        public long getSize() { return bytes.length; }
        public byte[] getBytes() { return bytes; }
        public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(bytes); }
        public void transferTo(java.io.File dest) throws IOException { Files.write(dest.toPath(), bytes); }
    }
}
