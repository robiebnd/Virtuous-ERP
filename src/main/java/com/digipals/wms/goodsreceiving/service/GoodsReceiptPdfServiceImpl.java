package com.digipals.wms.goodsreceiving.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoodsReceiptPdfServiceImpl implements GoodsReceiptPdfService {

    private static final float MARGIN = 42f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final GoodsReceiptRepository repository;

    @Override
    public byte[] generateById(UUID id) {
        return generate(getReceipt(id));
    }

    @Override
    public byte[] generateByNumber(String grnNumber) {
        return generate(repository.findByGrnNumber(grnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found: " + grnNumber)));
    }

    private GoodsReceipt getReceipt(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found."));
    }

    private byte[] generate(GoodsReceipt receipt) {
        if (receipt.getStatus() != ReceiptStatus.APPROVED) {
            throw new InvalidWorkflowException("Only approved Goods Receipts can generate the official PDF.");
        }

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float y = PAGE_WIDTH; // overwritten below; keeps the layout calculations explicit
            y = PDRectangle.A4.getHeight() - MARGIN;

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                y = drawHeader(content, receipt, y);
                y = drawDetails(content, receipt, y);
                y = drawTableHeader(content, y);

                List<GoodsReceiptLine> lines = receipt.getLines();
                if (lines != null) {
                    for (GoodsReceiptLine line : lines) {
                        if (y < 90) {
                            drawFooter(content, page);
                            content.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            y = PDRectangle.A4.getHeight() - MARGIN;
                            try (PDPageContentStream next = new PDPageContentStream(document, page)) {
                                y = drawHeader(next, receipt, y);
                                y = drawTableHeader(next, y);
                                y = drawLine(next, line, y);
                                // Continue the remaining lines on the outer loop using a compact page strategy.
                                for (int i = lines.indexOf(line) + 1; i < lines.size(); i++) {
                                    if (y < 90) {
                                        drawFooter(next, page);
                                        break;
                                    }
                                    y = drawLine(next, lines.get(i), y);
                                }
                                y = drawTotals(next, receipt, y);
                                drawAudit(next, receipt, y);
                                drawFooter(next, page);
                            }
                            document.save(output);
                            return output.toByteArray();
                        }
                        y = drawLine(content, line, y);
                    }
                }

                y = drawTotals(content, receipt, y);
                drawAudit(content, receipt, y);
                drawFooter(content, page);
            }

            document.save(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new InvalidWorkflowException("Failed to generate Goods Receipt PDF: " + e.getMessage());
        }
    }

    private float drawHeader(PDPageContentStream c, GoodsReceipt receipt, float y) throws Exception {
        c.setLineWidth(1.2f);
        c.moveTo(MARGIN, y - 8);
        c.lineTo(PAGE_WIDTH - MARGIN, y - 8);
        c.stroke();

        text(c, "VIRTUOUS ERP", MARGIN, y - 28, 18, true);
        text(c, "GOODS RECEIPT NOTE", PAGE_WIDTH - MARGIN - 180, y - 27, 15, true);
        text(c, "GRN " + receipt.getGrnNumber(), PAGE_WIDTH - MARGIN - 180, y - 45, 10, false);
        text(c, "APPROVED", PAGE_WIDTH - MARGIN - 180, y - 60, 9, true);

        return y - 82;
    }

    private float drawDetails(PDPageContentStream c, GoodsReceipt receipt, float y) throws Exception {
        float left = MARGIN;
        float right = MARGIN + CONTENT_WIDTH / 2 + 10;

        text(c, "SUPPLIER", left, y, 8, true);
        text(c, safe(receipt.getPurchaseOrder().getSupplier().getName()), left, y - 15, 10, false);
        text(c, "Code: " + safe(receipt.getPurchaseOrder().getSupplier().getCode()), left, y - 30, 9, false);

        text(c, "GOODS RECEIPT", right, y, 8, true);
        text(c, "GRN Date: " + (receipt.getReceivedDate() == null ? "" : receipt.getReceivedDate().format(DATE_TIME)), right, y - 15, 9, false);
        text(c, "PO Number: " + safe(receipt.getPurchaseOrder().getPoNumber()), right, y - 30, 9, false);
        text(c, "Currency: " + safe(receipt.getPurchaseOrder().getCurrency()), right, y - 45, 9, false);

        float lineY = y - 62;
        c.setLineWidth(0.6f);
        c.moveTo(MARGIN, lineY);
        c.lineTo(PAGE_WIDTH - MARGIN, lineY);
        c.stroke();
        return lineY - 24;
    }

    private float drawTableHeader(PDPageContentStream c, float y) throws Exception {
        text(c, "ITEM", MARGIN, y, 8, true);
        text(c, "SKU", MARGIN + 30, y, 8, true);
        text(c, "DESCRIPTION", MARGIN + 105, y, 8, true);
        text(c, "ORDERED", MARGIN + 285, y, 8, true);
        text(c, "RECEIVED", MARGIN + 350, y, 8, true);
        text(c, "ACCEPTED", MARGIN + 415, y, 8, true);
        text(c, "REJECTED", MARGIN + 480, y, 8, true);
        text(c, "UNIT COST", MARGIN + 540, y, 8, true);
        return y - 14;
    }

    private float drawLine(PDPageContentStream c, GoodsReceiptLine line, float y) throws Exception {
        int index = line.getId() == null ? 0 : Math.abs(line.getId().hashCode() % 9999);
        text(c, String.valueOf(index), MARGIN, y, 8, false);
        text(c, safe(line.getProduct().getSku()), MARGIN + 30, y, 8, false);
        text(c, truncate(safe(line.getProduct().getName()), 28), MARGIN + 105, y, 8, false);
        text(c, money(line.getOrderedQuantity()), MARGIN + 285, y, 8, false);
        text(c, money(line.getReceivedQuantity()), MARGIN + 350, y, 8, false);
        text(c, money(line.getAcceptedQuantity()), MARGIN + 415, y, 8, false);
        text(c, money(line.getRejectedQuantity()), MARGIN + 480, y, 8, false);
        text(c, money(line.getUnitCost()), MARGIN + 540, y, 8, false);
        return y - 16;
    }

    private float drawTotals(PDPageContentStream c, GoodsReceipt receipt, float y) throws Exception {
        BigDecimal acceptedTotal = BigDecimal.ZERO;
        BigDecimal rejectedTotal = BigDecimal.ZERO;
        for (GoodsReceiptLine line : receipt.getLines()) {
            acceptedTotal = acceptedTotal.add(nullSafe(line.getAcceptedQuantity()));
            rejectedTotal = rejectedTotal.add(nullSafe(line.getRejectedQuantity()));
        }

        float lineY = y - 8;
        c.setLineWidth(0.8f);
        c.moveTo(MARGIN + 350, lineY);
        c.lineTo(PAGE_WIDTH - MARGIN, lineY);
        c.stroke();

        text(c, "TOTAL ACCEPTED", MARGIN + 390, lineY - 18, 9, true);
        text(c, money(acceptedTotal), MARGIN + 520, lineY - 18, 9, true);
        text(c, "TOTAL REJECTED", MARGIN + 390, lineY - 34, 9, false);
        text(c, money(rejectedTotal), MARGIN + 520, lineY - 34, 9, false);
        return lineY - 55;
    }

    private void drawAudit(PDPageContentStream c, GoodsReceipt receipt, float y) throws Exception {
        text(c, "AUDIT TRAIL", MARGIN, y, 8, true);
        text(c, "Received By: " + (receipt.getReceivedBy() == null ? "" : receipt.getReceivedBy().getUsername()), MARGIN, y - 16, 9, false);
        text(c, "Approved By: " + (receipt.getApprovedBy() == null ? "" : receipt.getApprovedBy().getUsername()), MARGIN, y - 31, 9, false);
        text(c, "Approved At: " + (receipt.getApprovedAt() == null ? "" : receipt.getApprovedAt().format(DATE_TIME)), MARGIN, y - 46, 9, false);
        if (receipt.getSupplierDeliveryNote() != null && !receipt.getSupplierDeliveryNote().isBlank()) {
            text(c, "Supplier Delivery Note: " + receipt.getSupplierDeliveryNote(), MARGIN, y - 61, 9, false);
        }
    }

    private void drawFooter(PDPageContentStream c, PDPage page) throws Exception {
        float y = 28;
        c.setLineWidth(0.5f);
        c.moveTo(MARGIN, y + 8);
        c.lineTo(PAGE_WIDTH - MARGIN, y + 8);
        c.stroke();
        text(c, "Virtuous ERP - System Generated Document", MARGIN, y - 2, 7, false);
        text(c, "GRN", PAGE_WIDTH - MARGIN - 25, y - 2, 7, false);
    }

    private void text(PDPageContentStream c, String value, float x, float y, float size, boolean bold) throws Exception {
        PDType1Font font = new PDType1Font(bold
                ? Standard14Fonts.FontName.HELVETICA_BOLD
                : Standard14Fonts.FontName.HELVETICA);
        c.beginText();
        c.setFont(font, size);
        c.newLineAtOffset(x, y);
        c.showText(safe(value));
        c.endText();
    }

    private String money(BigDecimal value) {
        return nullSafe(value).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, Math.max(0, max - 3)) + "...";
    }
}
