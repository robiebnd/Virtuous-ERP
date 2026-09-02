package com.digipals.wms.procurementclosure.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementLineRequest;
import com.digipals.wms.goodsmovement.dto.CreateGoodsMovementRequest;
import com.digipals.wms.goodsmovement.dto.GoodsMovementResponse;
import com.digipals.wms.goodsmovement.entity.GoodsMovementType;
import com.digipals.wms.goodsmovement.service.GoodsMovementService;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.procurementclosure.dto.ProcurementClosureRequests.*;
import com.digipals.wms.procurementclosure.entity.*;
import com.digipals.wms.procurementclosure.repository.*;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderLineRepository;
import com.digipals.wms.purchaseorders.repository.PurchaseOrderRepository;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
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
public class ProcurementClosureService {
    private final PurchaseOrderRepository purchaseOrders;
    private final PurchaseOrderLineRepository poLines;
    private final SupplierInvoiceRepository invoices;
    private final SupplierInvoiceLineRepository invoiceLines;
    private final SupplierPaymentRepository payments;
    private final VendorEvaluationRepository evaluations;
    private final GoodsMovementService goodsMovements;
    private final WarehouseRepository warehouses;
    private final BinRepository bins;
    private final ProductRepository products;

    public Map<String,Object> verifyInvoice(SupplierInvoiceRequest r) {
        PurchaseOrder po=po(r.purchaseOrderNumber());
        if(po.getStatus()==PurchaseOrderStatus.CANCELLED||po.getStatus()==PurchaseOrderStatus.DRAFT) throw new InvalidWorkflowException("Only an approved or received Purchase Order can be invoiced.");
        if(r.supplierInvoiceNumber()==null||r.supplierInvoiceNumber().isBlank()) throw new InvalidWorkflowException("Supplier invoice number is required.");
        if(invoices.existsByInvoiceNumber(r.supplierInvoiceNumber().trim())) throw new InvalidWorkflowException("Supplier invoice number already exists.");
        if(r.lines()==null||r.lines().isEmpty()) throw new InvalidWorkflowException("Supplier invoice must contain at least one line.");
        List<PurchaseOrderLine> lines=poLines.findByPurchaseOrderId(po.getId());
        Map<String,PurchaseOrderLine> bySku=lines.stream().collect(Collectors.toMap(l->l.getProduct().getSku().toUpperCase(),l->l));
        Map<UUID,BigDecimal> already=alreadyInvoiced(po.getId());
        SupplierInvoice invoice=SupplierInvoice.builder().invoiceNumber(r.supplierInvoiceNumber().trim()).supplier(po.getSupplier()).purchaseOrder(po).invoiceDate(r.invoiceDate()==null?LocalDateTime.now():r.invoiceDate()).currency(r.currency()==null?po.getCurrency():r.currency().trim().toUpperCase()).status(SupplierInvoiceStatus.POSTED).subtotal(BigDecimal.ZERO).totalAmount(BigDecimal.ZERO).amountPaid(BigDecimal.ZERO).balanceDue(BigDecimal.ZERO).build();
        BigDecimal total=BigDecimal.ZERO;
        for(InvoiceLineRequest lr:r.lines()){
            if(lr.sku()==null||lr.quantity()==null||lr.unitPrice()==null||lr.quantity().signum()<=0||lr.unitPrice().signum()<0) throw new InvalidWorkflowException("Invoice line requires a valid SKU, positive quantity and non-negative unit price.");
            PurchaseOrderLine pol=bySku.get(lr.sku().trim().toUpperCase());
            if(pol==null) throw new ResourceNotFoundException("Purchase Order line not found for SKU: "+lr.sku());
            BigDecimal received=nz(pol.getReceivedQuantity()), prior=nz(already.get(pol.getId()));
            if(prior.add(lr.quantity()).compareTo(received)>0) throw new InvalidWorkflowException("Invoice quantity exceeds received quantity for SKU "+lr.sku());
            if(lr.unitPrice().compareTo(nz(pol.getUnitPrice()))!=0) throw new InvalidWorkflowException("Invoice price does not match Purchase Order price for SKU "+lr.sku());
            BigDecimal lineTotal=lr.quantity().multiply(lr.unitPrice());
            invoice.getLines().add(SupplierInvoiceLine.builder().invoice(invoice).purchaseOrderLine(pol).product(pol.getProduct()).quantity(lr.quantity()).unitPrice(lr.unitPrice()).lineTotal(lineTotal).build());
            total=total.add(lineTotal); already.put(pol.getId(),prior.add(lr.quantity()));
        }
        invoice.setSubtotal(total); invoice.setTotalAmount(total); invoice.setBalanceDue(total);
        SupplierInvoice saved=invoices.save(invoice);
        for(SupplierInvoiceLine l:invoice.getLines()) invoiceLines.save(l);
        Map<String,Object> response=invoiceResponse(saved); response.put("threeWayMatch","PASSED"); response.put("matchBasis","PURCHASE_ORDER + GOODS_RECEIPT + SUPPLIER_INVOICE"); return response;
    }

    public Map<String,Object> pay(SupplierPaymentRequest r){
        SupplierInvoice invoice=invoices.findByInvoiceNumber(r.invoiceNumber()).orElseThrow(()->new ResourceNotFoundException("Supplier invoice not found: "+r.invoiceNumber()));
        if(invoice.getStatus()==SupplierInvoiceStatus.BLOCKED) throw new InvalidWorkflowException("Blocked supplier invoice must be resolved before payment.");
        BigDecimal amount=nz(r.amount()); if(amount.signum()<=0) throw new InvalidWorkflowException("Payment amount must be greater than zero."); if(amount.compareTo(nz(invoice.getBalanceDue()))>0) throw new InvalidWorkflowException("Payment exceeds supplier invoice balance.");
        SupplierPayment payment=payments.save(SupplierPayment.builder().paymentNumber("VPAY-"+UUID.randomUUID().toString().substring(0,8).toUpperCase()).supplier(invoice.getSupplier()).invoice(invoice).paymentDate(LocalDateTime.now()).amount(amount).paymentMethod(r.paymentMethod()).reference(r.reference()).status(SupplierPaymentStatus.CLEARED).build());
        invoice.setAmountPaid(nz(invoice.getAmountPaid()).add(amount)); invoice.setBalanceDue(invoice.getTotalAmount().subtract(invoice.getAmountPaid()).max(BigDecimal.ZERO)); invoice.setStatus(invoice.getBalanceDue().signum()==0?SupplierInvoiceStatus.PAID:SupplierInvoiceStatus.PARTIALLY_PAID); invoices.save(invoice);
        return Map.of("paymentNumber",payment.getPaymentNumber(),"invoiceNumber",invoice.getInvoiceNumber(),"amount",amount,"invoiceStatus",invoice.getStatus().name(),"balanceDue",invoice.getBalanceDue());
    }

    public GoodsMovementResponse goodsIssue(GoodsIssueRequest r){
        Warehouse warehouse=warehouses.findByCode(r.warehouseCode()).orElseThrow(()->new ResourceNotFoundException("Warehouse not found: "+r.warehouseCode()));
        Bin bin=bins.findByWarehouseIdAndCode(warehouse.getId(),r.binCode()).orElseThrow(()->new ResourceNotFoundException("Bin not found: "+r.binCode()));
        Product product=products.findBySkuIgnoreCase(r.sku()).orElseThrow(()->new ResourceNotFoundException("Product not found: "+r.sku()));
        if(r.quantity()==null||r.quantity().signum()<=0) throw new InvalidWorkflowException("Goods issue quantity must be greater than zero.");
        String ref=r.referenceNumber()==null||r.referenceNumber().isBlank()?"GI-CONS-"+UUID.randomUUID().toString().substring(0,8).toUpperCase():r.referenceNumber();
        CreateGoodsMovementRequest request=CreateGoodsMovementRequest.builder().movementType(GoodsMovementType.SHIPMENT).warehouseId(warehouse.getId()).referenceNumber(ref).referenceType(r.referenceType()==null||r.referenceType().isBlank()?"CONSUMPTION":r.referenceType()).remarks(r.remarks()).lines(List.of(CreateGoodsMovementLineRequest.builder().productId(product.getId()).fromBinId(bin.getId()).quantity(r.quantity()).remarks(r.remarks()).build())).build();
        return goodsMovements.post(goodsMovements.create(request).getId());
    }

    @Transactional(readOnly=true)
    public Map<String,Object> reconcile(String poNumber){
        PurchaseOrder po=po(poNumber); List<PurchaseOrderLine> lines=poLines.findByPurchaseOrderId(po.getId()); Map<UUID,BigDecimal> invoiced=alreadyInvoiced(po.getId()); List<Map<String,Object>> detail=new ArrayList<>();
        BigDecimal receivedTotal=BigDecimal.ZERO,invoicedTotal=BigDecimal.ZERO,orderedTotal=BigDecimal.ZERO;
        for(PurchaseOrderLine l:lines){
            BigDecimal ordered=nz(l.getQuantity()),received=nz(l.getReceivedQuantity()),inv=nz(invoiced.get(l.getId())); orderedTotal=orderedTotal.add(ordered);receivedTotal=receivedTotal.add(received);invoicedTotal=invoicedTotal.add(inv);
            Map<String,Object> row=new LinkedHashMap<>(); row.put("sku",l.getProduct().getSku());row.put("orderedQuantity",ordered);row.put("receivedQuantity",received);row.put("invoicedQuantity",inv);row.put("uninvoicedReceivedQuantity",received.subtract(inv).max(BigDecimal.ZERO));row.put("openQuantity",ordered.subtract(received).max(BigDecimal.ZERO));row.put("quantityReconciled",received.compareTo(inv)==0);detail.add(row);
        }
        List<SupplierInvoice> supplierInvoices=invoices.findByPurchaseOrderId(po.getId()); BigDecimal outstandingPayments=supplierInvoices.stream().map(i->nz(i.getBalanceDue())).reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<String,Object> result=new LinkedHashMap<>(); result.put("purchaseOrderNumber",po.getPoNumber());result.put("supplierCode",po.getSupplier().getCode());result.put("orderedQuantity",orderedTotal);result.put("receivedQuantity",receivedTotal);result.put("invoicedQuantity",invoicedTotal);result.put("uninvoicedReceivedQuantity",receivedTotal.subtract(invoicedTotal).max(BigDecimal.ZERO));result.put("invoiceBalanceDue",outstandingPayments);result.put("fullyReceived",receivedTotal.compareTo(orderedTotal)==0);result.put("fullyInvoiced",invoicedTotal.compareTo(receivedTotal)==0);result.put("fullyPaid",outstandingPayments.signum()==0);result.put("cycleReconciled",receivedTotal.compareTo(orderedTotal)==0&&invoicedTotal.compareTo(receivedTotal)==0&&outstandingPayments.signum()==0);result.put("lines",detail);return result;
    }

    public Map<String,Object> close(String poNumber){ PurchaseOrder po=po(poNumber); Map<String,Object> reconciliation=reconcile(poNumber); if(!Boolean.TRUE.equals(reconciliation.get("cycleReconciled"))) throw new InvalidWorkflowException("Purchase Order cannot be closed until receipt, invoice and payment quantities/values reconcile."); po.setStatus(PurchaseOrderStatus.CLOSED);purchaseOrders.save(po);return Map.of("purchaseOrderNumber",po.getPoNumber(),"status",po.getStatus().name(),"message","Procurement cycle closed successfully."); }

    public Map<String,Object> evaluate(VendorEvaluationRequest r){
        PurchaseOrder po=r.purchaseOrderNumber()==null||r.purchaseOrderNumber().isBlank()?null:po(r.purchaseOrderNumber()); Supplier supplier=po==null?null:po.getSupplier(); if(supplier==null) throw new InvalidWorkflowException("Purchase Order number is required for vendor evaluation.");
        validateScore(r.priceScore(),"priceScore");validateScore(r.qualityScore(),"qualityScore");validateScore(r.deliveryScore(),"deliveryScore");validateScore(r.serviceScore(),"serviceScore"); BigDecimal overall=nz(r.priceScore()).add(nz(r.qualityScore())).add(nz(r.deliveryScore())).add(nz(r.serviceScore())).divide(BigDecimal.valueOf(4),2,RoundingMode.HALF_UP);
        VendorEvaluation e=evaluations.save(VendorEvaluation.builder().supplier(supplier).purchaseOrder(po).priceScore(r.priceScore()).qualityScore(r.qualityScore()).deliveryScore(r.deliveryScore()).serviceScore(r.serviceScore()).overallScore(overall).evaluationDate(LocalDateTime.now()).remarks(r.remarks()).build()); return Map.of("supplierCode",supplier.getCode(),"supplierName",supplier.getName(),"purchaseOrderNumber",po.getPoNumber(),"priceScore",e.getPriceScore(),"qualityScore",e.getQualityScore(),"deliveryScore",e.getDeliveryScore(),"serviceScore",e.getServiceScore(),"overallScore",e.getOverallScore());
    }
    private Map<UUID,BigDecimal> alreadyInvoiced(UUID poId){Map<UUID,BigDecimal> map=new HashMap<>();for(SupplierInvoice i:invoices.findByPurchaseOrderId(poId))for(SupplierInvoiceLine l:invoiceLines.findByInvoiceId(i.getId()))map.merge(l.getPurchaseOrderLine().getId(),nz(l.getQuantity()),BigDecimal::add);return map;}
    private Map<String,Object> invoiceResponse(SupplierInvoice i){return Map.of("invoiceNumber",i.getInvoiceNumber(),"purchaseOrderNumber",i.getPurchaseOrder().getPoNumber(),"supplierCode",i.getSupplier().getCode(),"status",i.getStatus().name(),"totalAmount",i.getTotalAmount(),"balanceDue",i.getBalanceDue());}
    private PurchaseOrder po(String n){if(n==null||n.isBlank())throw new InvalidWorkflowException("Purchase Order number is required.");return purchaseOrders.findByPoNumber(n.trim()).orElseThrow(()->new ResourceNotFoundException("Purchase Order not found: "+n));}
    private BigDecimal nz(BigDecimal n){return n==null?BigDecimal.ZERO:n;}
    private void validateScore(BigDecimal s,String name){if(s==null||s.compareTo(BigDecimal.ZERO)<0||s.compareTo(BigDecimal.valueOf(100))>0)throw new InvalidWorkflowException(name+" must be between 0 and 100.");}
}
