package com.digipals.wms.grir.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.digipals.wms.goodsreceiving.repository.GoodsReceiptLineRepository;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.vendorinvoice.entity.VendorInvoice;
import com.digipals.wms.vendorinvoice.entity.VendorInvoiceStatus;
import com.digipals.wms.vendorinvoice.repository.VendorInvoiceRepository;
import com.digipals.wms.grir.dto.GrIrResponse;
import com.digipals.wms.grir.entity.GrIrStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GrIrServiceImpl implements GrIrService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsReceiptLineRepository goodsReceiptLineRepository;
    private final VendorInvoiceRepository vendorInvoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public GrIrResponse reconcile(UUID purchaseOrderId) {
        PurchaseOrder po = getPo(purchaseOrderId);
        BigDecimal poValue = zero();
        for (PurchaseOrderLine line : po.getLines()) {
            poValue = poValue.add(nvl(line.getLineTotal()));
        }

        BigDecimal receivedValue = zero();
        for (PurchaseOrderLine line : po.getLines()) {
            List<GoodsReceiptLine> receipts = goodsReceiptLineRepository.findByPurchaseOrderLineId(line.getId());
            for (GoodsReceiptLine gr : receipts) {
                if (gr.getGoodsReceipt() != null && gr.getGoodsReceipt().getStatus() == ReceiptStatus.APPROVED) {
                    receivedValue = receivedValue.add(nvl(gr.getAcceptedQuantity()).multiply(nvl(line.getUnitPrice())));
                }
            }
        }

        BigDecimal invoicedValue = zero();
        List<VendorInvoice> invoices = vendorInvoiceRepository.findAllByPurchaseOrderId(purchaseOrderId);
        for (VendorInvoice invoice : invoices) {
            if (invoice.getStatus() != VendorInvoiceStatus.CANCELLED) {
                invoicedValue = invoicedValue.add(nvl(invoice.getSubtotal()));
            }
        }

        BigDecimal balance = receivedValue.subtract(invoicedValue).setScale(2, RoundingMode.HALF_UP);
        GrIrStatus status = balance.compareTo(BigDecimal.ZERO) == 0 ? GrIrStatus.BALANCED : GrIrStatus.VARIANCE;
        String remarks = balance.compareTo(BigDecimal.ZERO) == 0 ? "GR/IR is balanced." : "GR/IR has an open variance of " + balance;
        return response(po, poValue, receivedValue, invoicedValue, balance, status, remarks);
    }

    @Override
    public GrIrResponse close(UUID purchaseOrderId) {
        GrIrResponse result = reconcile(purchaseOrderId);
        if (result.getStatus() != GrIrStatus.BALANCED) {
            throw new InvalidWorkflowException("GR/IR cannot be closed while a variance remains.");
        }
        return GrIrResponse.builder().purchaseOrderId(result.getPurchaseOrderId()).purchaseOrderNumber(result.getPurchaseOrderNumber())
                .poValue(result.getPoValue()).receivedValue(result.getReceivedValue()).invoicedValue(result.getInvoicedValue())
                .grIrBalance(result.getGrIrBalance()).status(GrIrStatus.CLOSED).remarks("GR/IR reconciliation closed.").build();
    }

    private PurchaseOrder getPo(UUID id) {
        return purchaseOrderRepository.findWithLinesById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found."));
    }
    private GrIrResponse response(PurchaseOrder po, BigDecimal poValue, BigDecimal received, BigDecimal invoiced, BigDecimal balance, GrIrStatus status, String remarks) {
        return GrIrResponse.builder().purchaseOrderId(po.getId()).purchaseOrderNumber(po.getPoNumber()).poValue(scale(poValue)).receivedValue(scale(received))
                .invoicedValue(scale(invoiced)).grIrBalance(scale(balance)).status(status).remarks(remarks).build();
    }
    private BigDecimal nvl(BigDecimal value) { return value == null ? zero() : value; }
    private BigDecimal zero() { return BigDecimal.ZERO; }
    private BigDecimal scale(BigDecimal value) { return nvl(value).setScale(2, RoundingMode.HALF_UP); }
}
