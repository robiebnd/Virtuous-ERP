package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionLineRepository;
import com.digipals.wms.purchaserequisition.repository.PurchaseRequisitionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseRequisitionPdfServiceImpl implements PurchaseRequisitionPdfService {

    private static final float MARGIN = 42f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final PurchaseRequisitionRepository repository;
    private final PurchaseRequisitionLineRepository lineRepository;

    @Override
    public byte[] generateById(UUID id) {
        return generate(getRequisition(id));
    }

    @Override
    public byte[] generateByNumber(String requisitionNumber) {
        return generate(repository.findByRequisitionNumber(requisitionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found: " + requisitionNumber)));
    }

    private PurchaseRequisition getRequisition(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Requisition not found."));
    }

    private byte[] generate(PurchaseRequisition pr) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            List<PurchaseRequisitionLine> lines = lineRepository.findByPurchaseRequisitionId(pr.getId());
            int lineIndex = 0;
            int pageNumber = 1;

            while (lineIndex < lines.size() || pageNumber == 1) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    float y = drawHeader(content, pr, pageNumber);
                    y = drawDetails(content, pr, y);
                    y = drawTableHeader(content, y);

                    while (lineIndex < lines.size() && y > 105) {
                        y = drawLine(content, lines.get(lineIndex), lineIndex + 1, y);
                        lineIndex++;
                    }

                    if (lineIndex >= lines.size()) {
                        y = drawTotal(content, lines, y);
                        drawAudit(content, pr, y);
                    }
                    drawFooter(content, pageNumber);
                }

                pageNumber++;
                if (lines.isEmpty()) {
                    break;
                }
            }

            document.save(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate Purchase Requisition PDF: " + e.getMessage(), e);
        }
    }

    private float drawHeader(PDPageContentStream c, PurchaseRequisition pr, int pageNumber) throws Exception {
        float y = PAGE_HEIGHT - MARGIN;
        c.setLineWidth(1.2f);
        c.moveTo(MARGIN, y - 8);
        c.lineTo(PAGE_WIDTH - MARGIN, y - 8);
        c.stroke();

        text(c, "VIRTUOUS ERP", MARGIN, y - 28, 18, true);
        text(c, "PURCHASE REQUISITION", PAGE_WIDTH - MARGIN - 190, y - 27, 15, true);
        text(c, "PR " + pr.getRequisitionNumber(), PAGE_WIDTH - MARGIN - 190, y - 45, 10, false);
        text(c, pr.getStatus().name(), PAGE_WIDTH - MARGIN - 190, y - 60, 9, true);
        if (pageNumber > 1) {
            text(c, "Page " + pageNumber, PAGE_WIDTH - MARGIN - 190, y - 75, 8, false);
        }
        return y - (pageNumber > 1 ? 92 : 82);
    }

    private float drawDetails(PDPageContentStream c, PurchaseRequisition pr, float y) throws Exception {
        float left = MARGIN;
        float right = MARGIN + 275;

        text(c, "REQUEST", left, y, 8, true);
        text(c, "Department: " + safe(pr.getDepartment()), left, y - 15, 9, false);
        text(c, "Requested By: " + (pr.getRequestedBy() == null ? "" : pr.getRequestedBy().getUsername()), left, y - 30, 9, false);
        text(c, "Supplier: " + (pr.getSupplier() == null ? "" : pr.getSupplier().getName()), left, y - 45, 9, false);

        text(c, "FULFILMENT", right, y, 8, true);
        text(c, "Warehouse: " + (pr.getWarehouse() == null ? "" : pr.getWarehouse().getCode()), right, y - 15, 9, false);
        text(c, "Currency: " + safe(pr.getCurrency()), right, y - 30, 9, false);
        text(c, "Created: " + (pr.getCreatedAt() == null ? "" : pr.getCreatedAt().format(DATE_TIME)), right, y - 45, 9, false);

        float lineY = y - 62;
        c.setLineWidth(0.6f);
        c.moveTo(MARGIN, lineY);
        c.lineTo(PAGE_WIDTH - MARGIN, lineY);
        c.stroke();
        return lineY - 24;
    }

    private float drawTableHeader(PDPageContentStream c, float y) throws Exception {
        text(c, "ITEM", MARGIN, y, 8, true);
        text(c, "SKU", MARGIN + 35, y, 8, true);
        text(c, "DESCRIPTION", MARGIN + 120, y, 8, true);
        text(c, "QUANTITY", MARGIN + 315, y, 8, true);
        text(c, "UNIT COST", MARGIN + 400, y, 8, true);
        text(c, "LINE TOTAL", MARGIN + 500, y, 8, true);
        return y - 14;
    }

    private float drawLine(PDPageContentStream c, PurchaseRequisitionLine line, int index, float y) throws Exception {
        BigDecimal quantity = nullSafe(line.getQuantity());
        BigDecimal unitCost = nullSafe(line.getEstimatedUnitCost());
        BigDecimal total = quantity.multiply(unitCost);

        text(c, String.valueOf(index), MARGIN, y, 8, false);
        text(c, line.getProduct() == null ? "" : safe(line.getProduct().getSku()), MARGIN + 35, y, 8, false);
        text(c, truncate(line.getProduct() == null ? "" : safe(line.getProduct().getName()), 30), MARGIN + 120, y, 8, false);
        text(c, money(quantity), MARGIN + 315, y, 8, false);
        text(c, money(unitCost), MARGIN + 400, y, 8, false);
        text(c, money(total), MARGIN + 500, y, 8, false);
        return y - 17;
    }

    private float drawTotal(PDPageContentStream c, List<PurchaseRequisitionLine> lines, float y) throws Exception {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseRequisitionLine line : lines) {
            total = total.add(nullSafe(line.getQuantity()).multiply(nullSafe(line.getEstimatedUnitCost())));
        }

        float lineY = y - 8;
        c.setLineWidth(0.8f);
        c.moveTo(MARGIN + 390, lineY);
        c.lineTo(PAGE_WIDTH - MARGIN, lineY);
        c.stroke();
        text(c, "ESTIMATED TOTAL", MARGIN + 390, lineY - 20, 9, true);
        text(c, money(total), MARGIN + 500, lineY - 20, 9, true);
        return lineY - 48;
    }

    private void drawAudit(PDPageContentStream c, PurchaseRequisition pr, float y) throws Exception {
        text(c, "WORKFLOW AUDIT", MARGIN, y, 8, true);
        text(c, "Submitted At: " + (pr.getSubmittedAt() == null ? "" : pr.getSubmittedAt().format(DATE_TIME)), MARGIN, y - 16, 9, false);
        if (pr.getApprovedBy() != null) {
            text(c, "Approved By: " + pr.getApprovedBy().getUsername(), MARGIN, y - 31, 9, false);
            text(c, "Approved At: " + (pr.getApprovedAt() == null ? "" : pr.getApprovedAt().format(DATE_TIME)), MARGIN, y - 46, 9, false);
        }
        if (pr.getRejectedBy() != null) {
            text(c, "Rejected By: " + pr.getRejectedBy().getUsername(), MARGIN + 270, y - 31, 9, false);
            text(c, "Rejected At: " + (pr.getRejectedAt() == null ? "" : pr.getRejectedAt().format(DATE_TIME)), MARGIN + 270, y - 46, 9, false);
            text(c, "Reason: " + safe(pr.getRejectionReason()), MARGIN + 270, y - 61, 9, false);
        }
    }

    private void drawFooter(PDPageContentStream c, int pageNumber) throws Exception {
        float y = 28;
        c.setLineWidth(0.5f);
        c.moveTo(MARGIN, y + 8);
        c.lineTo(PAGE_WIDTH - MARGIN, y + 8);
        c.stroke();
        text(c, "Virtuous ERP - System Generated Document", MARGIN, y - 2, 7, false);
        text(c, "Page " + pageNumber, PAGE_WIDTH - MARGIN - 35, y - 2, 7, false);
    }

    private void text(PDPageContentStream c, String value, float x, float y, float size, boolean bold) throws Exception {
        PDType1Font font = new PDType1Font(bold ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA);
        c.beginText();
        c.setFont(font, size);
        c.newLineAtOffset(x, y);
        c.showText(safe(value));
        c.endText();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String money(BigDecimal value) {
        return nullSafe(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, Math.max(0, max - 3)) + "...";
    }
}
