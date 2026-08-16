package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderPdfService {

    private static final float MARGIN = 36f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);
    private static final PDType1Font REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font ITALIC = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final PurchaseOrderRepository repository;

    @Transactional(readOnly = true)
    public byte[] generateById(java.util.UUID id) {
        PurchaseOrder po = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found."));
        return generate(po);
    }

    @Transactional(readOnly = true)
    public byte[] generateByNumber(String poNumber) {
        if (poNumber == null || poNumber.isBlank()) {
            throw new IllegalArgumentException("PO number is required.");
        }
        PurchaseOrder po = repository.findByPoNumber(poNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found: " + poNumber));
        return generate(po);
    }

    private byte[] generate(PurchaseOrder po) {
        if (po.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new InvalidWorkflowException("Only approved Purchase Orders can generate the official PDF.");
        }

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);
            float y = PAGE_HEIGHT - MARGIN;

            y = drawHeader(stream, po, y);
            y -= 14;
            y = drawPartyInformation(stream, po, y);
            y -= 18;
            y = drawLinesHeader(stream, y);

            BigDecimal grandTotal = BigDecimal.ZERO;
            int rowIndex = 0;
            List<PurchaseOrderLine> lines = po.getLines();
            for (PurchaseOrderLine line : lines) {
                if (y < 92) {
                    drawFooter(stream, page);
                    stream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    stream = new PDPageContentStream(document, page);
                    y = PAGE_HEIGHT - MARGIN;
                    y = drawContinuationHeader(stream, po, y);
                    y -= 14;
                    y = drawLinesHeader(stream, y);
                }

                BigDecimal quantity = scale(line.getQuantity());
                BigDecimal unitPrice = scale(line.getUnitPrice());
                BigDecimal lineTotal = scale(line.getLineTotal() == null
                        ? quantity.multiply(unitPrice)
                        : line.getLineTotal());
                grandTotal = grandTotal.add(lineTotal);

                if (rowIndex % 2 == 0) {
                    fillRect(stream, MARGIN, y - 13, CONTENT_WIDTH, 18, 0.96f);
                }
                drawText(stream, safe(line.getProduct() == null ? null : line.getProduct().getSku()), MARGIN + 5, y, 8, REGULAR);
                drawText(stream, truncate(safe(line.getProduct() == null ? null : line.getProduct().getName()), 42), MARGIN + 75, y, 8, REGULAR);
                drawRight(stream, money(quantity), MARGIN + 382, y, 8, REGULAR);
                drawRight(stream, money(unitPrice), MARGIN + 452, y, 8, REGULAR);
                drawRight(stream, money(lineTotal), MARGIN + 526, y, 8, REGULAR);
                y -= 18;
                rowIndex++;
            }

            y -= 4;
            drawTotalBox(stream, y, grandTotal);
            y -= 62;
            y = drawAudit(stream, po, y);
            y -= 18;
            drawTerms(stream, y);
            drawFooter(stream, page);
            stream.close();

            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Purchase Order PDF.", e);
        }
    }

    private float drawHeader(PDPageContentStream s, PurchaseOrder po, float y) throws IOException {
        drawText(s, "VIRTUOUS ERP", MARGIN, y, 18, BOLD);
        drawText(s, "PROCUREMENT & INVENTORY MANAGEMENT", MARGIN, y - 15, 7, REGULAR);
        drawRight(s, "PURCHASE ORDER", PAGE_WIDTH - MARGIN, y, 18, BOLD);
        drawRight(s, po.getPoNumber(), PAGE_WIDTH - MARGIN, y - 18, 11, BOLD);
        drawRight(s, "STATUS: " + po.getStatus().name(), PAGE_WIDTH - MARGIN, y - 32, 8, BOLD);
        line(s, MARGIN, y - 43, PAGE_WIDTH - MARGIN, y - 43, 1f);
        return y - 58;
    }

    private float drawContinuationHeader(PDPageContentStream s, PurchaseOrder po, float y) throws IOException {
        drawText(s, "VIRTUOUS ERP", MARGIN, y, 13, BOLD);
        drawRight(s, "PURCHASE ORDER " + po.getPoNumber() + " — CONTINUED", PAGE_WIDTH - MARGIN, y, 10, BOLD);
        line(s, MARGIN, y - 12, PAGE_WIDTH - MARGIN, y - 12, 0.8f);
        return y - 28;
    }

    private float drawPartyInformation(PDPageContentStream s, PurchaseOrder po, float y) throws IOException {
        float boxHeight = 92;
        float half = (CONTENT_WIDTH - 10) / 2;
        box(s, MARGIN, y - boxHeight, half, boxHeight);
        box(s, MARGIN + half + 10, y - boxHeight, half, boxHeight);

        drawText(s, "SUPPLIER", MARGIN + 10, y - 17, 8, BOLD);
        drawText(s, safe(po.getSupplier() == null ? null : po.getSupplier().getName()), MARGIN + 10, y - 34, 10, BOLD);
        drawText(s, "Code: " + safe(po.getSupplier() == null ? null : po.getSupplier().getCode()), MARGIN + 10, y - 49, 8, REGULAR);

        float rx = MARGIN + half + 20;
        drawText(s, "PURCHASE ORDER DETAILS", rx, y - 17, 8, BOLD);
        drawText(s, "PO Number: " + safe(po.getPoNumber()), rx, y - 34, 8, REGULAR);
        drawText(s, "PO Date: " + (po.getOrderDate() == null ? "" : po.getOrderDate().format(DATE)), rx, y - 47, 8, REGULAR);
        drawText(s, "PR Number: " + safe(po.getPurchaseRequisition() == null ? null : po.getPurchaseRequisition().getRequisitionNumber()), rx, y - 60, 8, REGULAR);
        drawText(s, "Warehouse: " + safe(po.getWarehouse() == null ? null : po.getWarehouse().getCode()) + " - " + safe(po.getWarehouse() == null ? null : po.getWarehouse().getName()), rx, y - 73, 8, REGULAR);
        return y - boxHeight;
    }

    private float drawLinesHeader(PDPageContentStream s, float y) throws IOException {
        fillRect(s, MARGIN, y - 16, CONTENT_WIDTH, 22, 0.82f);
        drawText(s, "SKU", MARGIN + 5, y - 8, 8, BOLD);
        drawText(s, "DESCRIPTION", MARGIN + 75, y - 8, 8, BOLD);
        drawRight(s, "QTY", MARGIN + 382, y - 8, 8, BOLD);
        drawRight(s, "UNIT PRICE", MARGIN + 452, y - 8, 8, BOLD);
        drawRight(s, "TOTAL", MARGIN + 526, y - 8, 8, BOLD);
        return y - 28;
    }

    private void drawTotalBox(PDPageContentStream s, float y, BigDecimal total) throws IOException {
        float x = PAGE_WIDTH - MARGIN - 190;
        box(s, x, y - 42, 190, 42);
        drawText(s, "GRAND TOTAL", x + 10, y - 16, 9, BOLD);
        drawRight(s, "USD " + money(total), x + 180, y - 16, 11, BOLD);
    }

    private float drawAudit(PDPageContentStream s, PurchaseOrder po, float y) throws IOException {
        drawText(s, "APPROVAL & AUDIT", MARGIN, y, 8, BOLD);
        line(s, MARGIN, y - 5, PAGE_WIDTH - MARGIN, y - 5, 0.6f);
        String created = po.getCreatedBy() == null ? null : po.getCreatedBy().getUsername();
        String approved = po.getApprovedBy() == null ? null : po.getApprovedBy().getUsername();
        drawText(s, "Created By: " + safe(created), MARGIN, y - 20, 8, REGULAR);
        drawText(s, "Approved By: " + safe(approved), MARGIN + 200, y - 20, 8, REGULAR);
        drawText(s, "Approved At: " + (po.getApprovedAt() == null ? "" : po.getApprovedAt().format(DATE_TIME)), MARGIN, y - 34, 8, REGULAR);
        return y - 42;
    }

    private void drawTerms(PDPageContentStream s, float y) throws IOException {
        drawText(s, "IMPORTANT", MARGIN, y, 8, BOLD);
        drawText(s, "This purchase order is subject to the applicable supplier terms and approved procurement controls.", MARGIN, y - 14, 7, ITALIC);
        drawText(s, "Please quote the PO number on all supplier correspondence, invoices and delivery documentation.", MARGIN, y - 26, 7, ITALIC);
    }

    private void drawFooter(PDPageContentStream s, PDPage page) throws IOException {
        line(s, MARGIN, 35, PAGE_WIDTH - MARGIN, 35, 0.5f);
        drawText(s, "Virtuous ERP • System Generated Document", MARGIN, 23, 7, REGULAR);
        drawRight(s, "Page " + (page == null ? "" : ""), PAGE_WIDTH - MARGIN, 23, 7, REGULAR);
    }

    private void drawText(PDPageContentStream s, String text, float x, float y, float size, PDType1Font font) throws IOException {
        s.beginText();
        s.setFont(font, size);
        s.newLineAtOffset(x, y);
        s.showText(safe(text));
        s.endText();
    }

    private void drawRight(PDPageContentStream s, String text, float rightX, float y, float size, PDType1Font font) throws IOException {
        String value = safe(text);
        float width = font.getStringWidth(value) / 1000f * size;
        drawText(s, value, rightX - width, y, size, font);
    }

    private void line(PDPageContentStream s, float x1, float y1, float x2, float y2, float width) throws IOException {
        s.setLineWidth(width);
        s.moveTo(x1, y1);
        s.lineTo(x2, y2);
        s.stroke();
    }

    private void box(PDPageContentStream s, float x, float y, float width, float height) throws IOException {
        s.setLineWidth(0.6f);
        s.addRect(x, y, width, height);
        s.stroke();
    }

    private void fillRect(PDPageContentStream s, float x, float y, float width, float height, float gray) throws IOException {
        s.setNonStrokingColor(gray, gray, gray);
        s.addRect(x, y, width, height);
        s.fill();
        s.setNonStrokingColor(0, 0, 0);
    }

    private BigDecimal scale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String money(BigDecimal value) {
        return scale(value).toPlainString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(0, max - 3)) + "...";
    }
}
