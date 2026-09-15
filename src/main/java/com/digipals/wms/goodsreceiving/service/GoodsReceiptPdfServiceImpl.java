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
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoodsReceiptPdfServiceImpl implements GoodsReceiptPdfService {

    private static final float MARGIN = 42f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
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
            List<GoodsReceiptLine> lines = receipt.getLines() == null ? List.of() : receipt.getLines();
            int lineIndex = 0;
            int pageNumber = 1;

            while (lineIndex < lines.size() || pageNumber == 1) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    float y = drawHeader(content, receipt, pageNumber);
                    y = drawDetails(content, receipt, y);
                    y = drawTableHeader(content, y);

                    while (lineIndex < lines.size() && y > 95) {
                        y = drawLine(content, lines.get(lineIndex), lineIndex + 1, y);
                        lineIndex++;
                    }

                    if (lineIndex >= lines.size()) {
                        y = drawTotals(content, receipt, y);
                        drawAudit(content, receipt, y);
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
            throw new InvalidWorkflowException("Failed to generate Goods Receipt PDF: " + e.getMessage());
        }
    }

    private float drawHeader(PDPageContentStream c, GoodsReceipt receipt, int pageNumber) throws Exception {
        float y = PAGE_HEIGHT - MARGIN;
        c.setLineWidth(1.2f);
        c.moveTo(MARGIN, y - 8);
        c.lineTo(PAGE_WIDTH - MARGIN, y - 8);
        c.stroke();

        text(c, "VIRTUOUS ERP", MARGIN, y - 28, 18, true);
        text(c, "GOODS RECEIPT NOTE", PAGE_WIDTH - MARGIN - 180, y - 27, 15, true);
        text(c, "GRN " + receipt.getGrnNumber(), PAGE_WIDTH - MARGIN - 180, y - 45, 10, false);
        text(c, "APPROVED", PAGE_WIDTH - MARGIN - 180, y - 60, 9, true);
        if (pageNumber > 1) {
            text(c, "Page " + pageNumber, PAGE_WIDTH - MARGIN - 180, y - 75, 8, false);
        }

        return y - (pageNumber > 1 ? 92 : 82);
    }

    private float drawDetails(PDPageContentStream c, GoodsReceipt receipt, float y) throws Exception {
        float left = MARGIN;
        float right = MARGIN + 275;

        text(c, "SUPPLIER", left, y, 8, true);
        text(c, safe(receipt.getPurchaseOrder().getSupplier().getName()), left, y - 15, 10, false);
        text(c, "Code: " + safe(receipt.getPurchaseOrder().getSupplier().getCode()), left, y - 30, 9, false);

        text(c, "GOODS RECEIPT", right, y, 8, true);
        text(c, "GRN Date: " + (receipt.getReceivedDate() == null ? "" : receipt.getReceivedDate().format(DATE_TIME)), right, y - 15, 9, false);
        text(c, "PO Number: " + safe(receipt.getPurchaseOrder().getPoNumber()), right, y - 30, 9, false);
        text(c, "Currency: " + safe(receipt.getPurchaseOrder().getCurrency()), right, y - 45, 9, false);
        text(c, "Warehouse: " + safe(receipt.getWarehouse().getCode()), right, y - 60, 9, false);

        float lineY = y - 77;
        c.setLineWidth(0.6f);
        c.moveTo(MARGIN, lineY);
        c.lineTo(PAGE_WIDTH - MARGIN, lineY);
        c.stroke();
        return lineY - 24;
    }

    private float drawTableHeader(PDPageContentStream c, float y) throws Exception {
        text(c, "ITEM", MARGIN, y, 7.5f, true);
        text(c, "SKU", MARGIN + 30, y, 7.5f, true);
        text(c, "DESCRIPTION", MARGIN + 100, y, 7.5f, true);
        text(c, "ORDERED", MARGIN + 275, y, 7.5f, true);
        text(c, "RECEIVED", MARGIN + 340, y, 7.5f, true);
        text(c, "ACCEPTED", MARGIN + 405, y, 7.5f, true);
        text(c, "REJECTED", MARGIN + 470, y, 7.5f, true);
        text(c, "UNIT COST", MARGIN + 530, y, 7.5f, true);
        return y - 14;
    }

    private float drawLine(PDPageContentStream c, GoodsReceiptLine line, int index, float y) throws Exception {
        text(c, String.valueOf(index), MARGIN, y, 8, false);
        text(c, safe(line.getProduct().getSku()), MARGIN + 30, y, 8, false);
        text(c, truncate(safe(line.getProduct().getName()), 27), MARGIN + 100, y, 8, false);
        text(c, money(line.getOrderedQuantity()), MARGIN + 275, y, 8, false);
        text(c, money(line.getReceivedQuantity()), MARGIN + 340, y, 8, false);
        text(c, money(line.getAcceptedQuantity()), MARGIN + 405, y, 8, false);
        text(c, money(line.getRejectedQuantity()), MARGIN + 470, y, 8, false);
        text(c, money(line.getUnitCost()), MARGIN + 530, y, 8, false);
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
        text(c, money(acceptedTotal), MARGIN + 530, lineY - 18, 9, true);
        text(c, "TOTAL REJECTED", MARGIN + 390, lineY - 34, 9, false);
        text(c, money(rejectedTotal), MARGIN + 530, lineY - 34, 9, false);
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
        return nullSafe(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
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
