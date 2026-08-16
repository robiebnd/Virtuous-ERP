package com.digipals.wms.supplierquotation.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.supplier.repository.SupplierRepository;
import com.digipals.wms.supplierquotation.dto.SupplierQuotationResponse;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierQuotationServiceImpl implements SupplierQuotationService {

    private static final Pattern QUOTATION_DATE_PATTERN = Pattern.compile(
            "(?i)quotation\\s*date\\s*[:\\-]?\\s*(\\d{1,2}\\s+[A-Za-z]+\\s+\\d{4})"
    );
    private static final DateTimeFormatter QUOTATION_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    private final SupplierQuotationRepository repository;
    private final PurchaseRequisitionRepository purchaseRequisitionRepository;
    private final SupplierRepository supplierRepository;

    @Value("${wms.quotation.upload-dir:uploads/quotations}")
    private String uploadDirectory;

    @Override
    public SupplierQuotationResponse upload(UUID purchaseRequisitionId,
                                            UUID supplierId,
                                            String quotationNumber,
                                            MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidWorkflowException("Quotation file is required.");
        }

        PurchaseRequisition requisition = purchaseRequisitionRepository.findById(purchaseRequisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

        if (quotationNumber == null || quotationNumber.isBlank()) {
            throw new InvalidWorkflowException("Supplier quotation number is required.");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "quotation"
                : file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "-" + originalName;

        try {
            Path directory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            Path target = directory.resolve(storedName).normalize();

            if (!target.getParent().equals(directory)) {
                throw new InvalidWorkflowException("Invalid quotation file name.");
            }

            byte[] pdfBytes = file.getBytes();
            LocalDate quotationDate = extractQuotationDate(pdfBytes);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            SupplierQuotation quotation = repository.save(SupplierQuotation.builder()
                    .quotationNumber(quotationNumber.trim())
                    .supplier(supplier)
                    .purchaseRequisition(requisition)
                    .quotationDate(quotationDate)
                    .originalFileName(originalName)
                    .storedFileName(storedName)
                    .filePath(target.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .status(SupplierQuotationStatus.UPLOADED)
                    .build());

            return toResponse(quotation);
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to store supplier quotation file.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierQuotationResponse> findByPurchaseRequisition(UUID purchaseRequisitionId) {
        return repository.findByPurchaseRequisitionIdOrderByCreatedAtDesc(purchaseRequisitionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LocalDate extractQuotationDate(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);
            Matcher matcher = QUOTATION_DATE_PATTERN.matcher(text == null ? "" : text);
            if (!matcher.find()) {
                return null;
            }

            String rawDate = matcher.group(1).trim();
            try {
                return LocalDate.parse(rawDate, QUOTATION_DATE_FORMATTER);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        } catch (IOException e) {
            throw new InvalidWorkflowException("Unable to read quotation PDF to determine quotation date.");
        }
    }

    private SupplierQuotationResponse toResponse(SupplierQuotation quotation) {
        PurchaseRequisition requisition = quotation.getPurchaseRequisition();
        Supplier supplier = quotation.getSupplier();

        return SupplierQuotationResponse.builder()
                .id(quotation.getId())
                .quotationNumber(quotation.getQuotationNumber())
                .supplierId(supplier.getId())
                .supplierCode(supplier.getCode())
                .supplierName(supplier.getName())
                .purchaseRequisitionId(requisition.getId())
                .requisitionNumber(requisition.getRequisitionNumber())
                .quotationDate(quotation.getQuotationDate())
                .originalFileName(quotation.getOriginalFileName())
                .contentType(quotation.getContentType())
                .fileSize(quotation.getFileSize())
                .status(quotation.getStatus())
                .uploadedAt(quotation.getCreatedAt())
                .build();
    }
}
