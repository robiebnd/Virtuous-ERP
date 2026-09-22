package com.digipals.wms.vendorinvoice.service;

import com.digipals.wms.common.document.DocumentType;
import com.digipals.wms.common.document.service.DocumentNumberService;
import com.digipals.wms.common.exception.DuplicateResourceException;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.finance.service.FinancePostingService;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptRepository;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.security.CurrentUserService;
import com.digipals.wms.vendorinvoice.dto.CreateVendorInvoiceRequest;
import com.digipals.wms.vendorinvoice.dto.VendorInvoiceResponse;
import com.digipals.wms.vendorinvoice.entity.*;
import com.digipals.wms.vendorinvoice.repository.VendorInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorInvoiceServiceImpl implements VendorInvoiceService {
    private static final BigDecimal DEFAULT_QUANTITY_TOLERANCE = new BigDecimal("0.00");
    private static final BigDecimal DEFAULT_PRICE_TOLERANCE_PERCENT = new BigDecimal("0.00");

    private final VendorInvoiceRepository invoiceRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final GoodsReceiptLineRepository goodsReceiptLineRepository;
    private final DocumentNumberService documentNumberService;
    private final CurrentUserService currentUserService;
    private final FinancePostingService financePostingService;

    @Override
    public VendorInvoiceResponse create(CreateVendorInvoiceRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found."));
        GoodsReceipt gr = goodsReceiptRepository.findWithLinesById(request.getGoodsReceiptId())
                .orElseThrow(() -> new ResourceNotFoundException("Goods Receipt not found."));

        if (po.getSupplier() == null || gr.getPurchaseOrder() == null || !po.getId().equals(gr.getPurchaseOrder().getId())) {
            throw new InvalidWorkflowException("Goods Receipt must belong to the supplied Purchase Order.");
        }
        if (gr.getStatus() != ReceiptStatus.APPROVED) throw new InvalidWorkflowException("Only an APPROVED Goods Receipt can be invoiced.");
        if (invoiceRepository.existsBySupplierIdAndSupplierInvoiceNumber(po.getSupplier().getId(), request.getSupplierInvoiceNumber().trim())) {
            throw new DuplicateResourceException("Supplier invoice number already exists for this supplier.");
        }

        VendorInvoice invoice = VendorInvoice.builder()
                .invoiceNumber(documentNumberService.next(DocumentType.VENDOR_INVOICE)).supplier(po.getSupplier()).purchaseOrder(po).goodsReceipt(gr)
                .supplierInvoiceNumber(request.getSupplierInvoiceNumber().trim()).invoiceDate(request.getInvoiceDate() == null ? LocalDateTime.now() : request.getInvoiceDate())
                .currency(request.getCurrency().trim().toUpperCase()).taxAmount(nvl(request.getTaxAmount())).createdBy(currentUserService.getCurrentUser()).build();

        BigDecimal subtotal = BigDecimal.ZERO;
        Set<UUID> seenPoLines = new HashSet<>();
        Map<UUID, List<GoodsReceiptLine>> grLines = gr.getLines().stream().collect(Collectors.groupingBy(line -> line.getPurchaseOrderLine().getId()));
        for (CreateVendorInvoiceRequest.Line requestLine : request.getLines()) {
            if (!seenPoLines.add(requestLine.getPurchaseOrderLineId())) throw new InvalidWorkflowException("A Purchase Order line may only occur once on an invoice.");
            PurchaseOrderLine poLine = purchaseOrderLineRepository.findById(requestLine.getPurchaseOrderLineId()).orElseThrow(() -> new ResourceNotFoundException("Purchase Order line not found."));
            if (!poLine.getPurchaseOrder().getId().equals(po.getId())) throw new InvalidWorkflowException("Invoice line does not belong to the Purchase Order.");
            GoodsReceiptLine grLine = resolveReceiptLine(requestLine.getGoodsReceiptLineId(), poLine, grLines.getOrDefault(poLine.getId(), List.of()));
            BigDecimal qty = requestLine.getInvoicedQuantity();
            BigDecimal price = requestLine.getInvoiceUnitPrice();
            BigDecimal lineTotal = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineTotal);
            invoice.addLine(VendorInvoiceLine.builder().purchaseOrderLine(poLine).goodsReceiptLine(grLine).product(poLine.getProduct()).invoicedQuantity(qty).invoiceUnitPrice(price).lineTotal(lineTotal).build());
        }
        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setTotalAmount(invoice.getSubtotal().add(invoice.getTaxAmount()).setScale(2, RoundingMode.HALF_UP));
        return toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public VendorInvoiceResponse match(UUID id) {
        VendorInvoice invoice = getInvoice(id);
        if (invoice.getStatus() == VendorInvoiceStatus.CANCELLED) throw new InvalidWorkflowException("Cancelled invoice cannot be matched.");
        boolean blocked = false;
        List<String> reasons = new ArrayList<>();
        for (VendorInvoiceLine line : invoice.getLines()) {
            PurchaseOrderLine po = line.getPurchaseOrderLine();
            GoodsReceiptLine gr = line.getGoodsReceiptLine();
            BigDecimal poQty = nvl(po.getQuantity());
            BigDecimal grQty = gr == null ? BigDecimal.ZERO : nvl(gr.getAcceptedQuantity());
            BigDecimal invQty = nvl(line.getInvoicedQuantity());
            BigDecimal poPrice = nvl(po.getUnitPrice());
            BigDecimal invPrice = nvl(line.getInvoiceUnitPrice());
            BigDecimal quantityVariance = invQty.subtract(grQty).setScale(2, RoundingMode.HALF_UP);
            BigDecimal priceVariance = invPrice.subtract(poPrice).setScale(2, RoundingMode.HALF_UP);
            line.setQuantityVariance(quantityVariance); line.setPriceVariance(priceVariance);
            BigDecimal qtyTolerance = poQty.multiply(nvl(po.getOverdeliveryTolerancePercent())).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP).add(DEFAULT_QUANTITY_TOLERANCE);
            BigDecimal priceTolerance = poPrice.abs().multiply(DEFAULT_PRICE_TOLERANCE_PERCENT).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
            List<String> lineReasons = new ArrayList<>();
            if (invQty.compareTo(grQty.add(qtyTolerance)) > 0) lineReasons.add("Invoice quantity exceeds approved GR quantity/tolerance");
            if (invQty.compareTo(poQty) > 0 && invQty.compareTo(poQty.add(qtyTolerance)) > 0) lineReasons.add("Invoice quantity exceeds PO quantity/tolerance");
            if (priceVariance.abs().compareTo(priceTolerance) > 0) lineReasons.add("Invoice unit price differs from PO price/tolerance");
            if (gr == null) lineReasons.add("No GR line found for invoice line");
            if (lineReasons.isEmpty()) { line.setMatchStatus(VendorInvoiceMatchStatus.MATCHED); line.setBlockReason(null); }
            else { blocked = true; line.setMatchStatus(VendorInvoiceMatchStatus.BLOCKED); line.setBlockReason(String.join("; ", lineReasons)); reasons.add(String.join(", ", lineReasons)); }
        }
        invoice.setMatchStatus(blocked ? VendorInvoiceMatchStatus.BLOCKED : VendorInvoiceMatchStatus.MATCHED);
        invoice.setStatus(blocked ? VendorInvoiceStatus.BLOCKED : VendorInvoiceStatus.MATCHED);
        invoice.setBlockReason(blocked ? String.join("; ", reasons) : null);
        VendorInvoice saved = invoiceRepository.save(invoice);
        if (!blocked) {
            var accountingDocument = financePostingService.postVendorInvoice(
                    saved.getId(),
                    saved.getInvoiceNumber(),
                    saved.getCurrency(),
                    saved.getTotalAmount()
            );
            saved.setAccountingDocument(accountingDocument);
            saved.setStatus(VendorInvoiceStatus.POSTED);
            saved.setPostedAt(LocalDateTime.now());
            saved = invoiceRepository.save(saved);
        }
        return toResponse(saved);
    }

    @Override @Transactional(readOnly=true)
    public VendorInvoiceResponse findById(UUID id) { return toResponse(getInvoice(id)); }
    @Override @Transactional(readOnly=true)
    public VendorInvoiceResponse findByNumber(String invoiceNumber) { return toResponse(invoiceRepository.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> new ResourceNotFoundException("Vendor Invoice not found."))); }
    @Override @Transactional(readOnly=true)
    public List<VendorInvoiceResponse> findAll() { return invoiceRepository.findAllByOrderByInvoiceDateDesc().stream().map(this::toResponse).toList(); }
    private VendorInvoice getInvoice(UUID id) { return invoiceRepository.findWithLinesById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor Invoice not found.")); }
    private GoodsReceiptLine resolveReceiptLine(UUID requestedId, PurchaseOrderLine poLine, List<GoodsReceiptLine> candidates) {
        if (requestedId != null) { GoodsReceiptLine line = goodsReceiptLineRepository.findById(requestedId).orElseThrow(() -> new ResourceNotFoundException("Goods Receipt line not found.")); if (!line.getPurchaseOrderLine().getId().equals(poLine.getId())) throw new InvalidWorkflowException("Goods Receipt line does not match Purchase Order line."); return line; }
        if (candidates.size() == 1) return candidates.get(0);
        if (candidates.isEmpty()) throw new InvalidWorkflowException("No Goods Receipt line exists for Purchase Order line " + poLine.getId());
        throw new InvalidWorkflowException("Multiple Goods Receipt lines exist; goodsReceiptLineId is required.");
    }
    private BigDecimal nvl(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private VendorInvoiceResponse toResponse(VendorInvoice i) {
        return VendorInvoiceResponse.builder().id(i.getId()).invoiceNumber(i.getInvoiceNumber()).supplierInvoiceNumber(i.getSupplierInvoiceNumber())
                .supplierId(i.getSupplier().getId()).supplierCode(i.getSupplier().getCode()).supplierName(i.getSupplier().getName())
                .purchaseOrderId(i.getPurchaseOrder().getId()).purchaseOrderNumber(i.getPurchaseOrder().getPoNumber()).goodsReceiptId(i.getGoodsReceipt().getId()).goodsReceiptNumber(i.getGoodsReceipt().getGrnNumber())
                .invoiceDate(i.getInvoiceDate()).currency(i.getCurrency()).subtotal(i.getSubtotal()).taxAmount(i.getTaxAmount()).totalAmount(i.getTotalAmount())
                .status(i.getStatus()).matchStatus(i.getMatchStatus()).blockReason(i.getBlockReason())
                .lines(i.getLines().stream().map(l -> VendorInvoiceResponse.Line.builder().id(l.getId()).purchaseOrderLineId(l.getPurchaseOrderLine().getId()).goodsReceiptLineId(l.getGoodsReceiptLine()==null?null:l.getGoodsReceiptLine().getId()).productId(l.getProduct().getId()).sku(l.getProduct().getSku()).invoicedQuantity(l.getInvoicedQuantity()).invoiceUnitPrice(l.getInvoiceUnitPrice()).lineTotal(l.getLineTotal()).quantityVariance(l.getQuantityVariance()).priceVariance(l.getPriceVariance()).matchStatus(l.getMatchStatus()).blockReason(l.getBlockReason()).build()).toList()).build();
    }
}
