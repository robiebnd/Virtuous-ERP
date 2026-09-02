package com.digipals.wms.outbound.service;

import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.outbound.dto.O2cRequests.*;
import com.digipals.wms.outbound.entity.*;
import com.digipals.wms.outbound.repository.*;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor
public class OrderToCashService {
    private final CustomerRepository customers; private final SalesOrderRepository orders;
    private final OutboundDeliveryRepository deliveries; private final CustomerInvoiceRepository invoices;
    private final CustomerPaymentRepository payments; private final ProductRepository products;
    private final WarehouseRepository warehouses; private final InventoryBinRepository inventoryBins;

    @Transactional public Customer createCustomer(CustomerRequest r) {
        if (customers.existsByCustomerNumber(r.customerNumber())) throw new IllegalArgumentException("Customer number already exists");
        return customers.save(Customer.builder().customerNumber(r.customerNumber()).name(r.name()).email(r.email()).phone(r.phone()).billingAddress(r.billingAddress()).shippingAddress(r.shippingAddress()).paymentTerms(r.paymentTerms()).creditLimit(r.creditLimit()).creditBlocked(false).build());
    }

    @Transactional public SalesOrder createOrder(SalesOrderRequest r) {
        Customer c=customers.findByCustomerNumber(r.customerNumber()).orElseThrow(()->new IllegalArgumentException("Customer not found"));
        Warehouse w=warehouses.findByCode(r.warehouseCode()).orElseThrow(()->new IllegalArgumentException("Warehouse not found"));
        if(r.lines()==null||r.lines().isEmpty()) throw new IllegalArgumentException("Sales order must contain at least one line");
        SalesOrder o=SalesOrder.builder().orderNumber(number("SO")).customer(c).warehouse(w).orderDate(LocalDateTime.now()).requestedDeliveryDate(r.requestedDeliveryDate()).paymentTerms(r.paymentTerms()).status(SalesOrderStatus.DRAFT).build();
        BigDecimal subtotal=BigDecimal.ZERO; int n=1;
        for(OrderLineRequest lr:r.lines()){
            if(lr.quantity()==null||lr.quantity().signum()<=0) throw new IllegalArgumentException("Quantity must be positive");
            Product p=products.findBySkuIgnoreCase(lr.sku()).orElseThrow(()->new IllegalArgumentException("Product not found: "+lr.sku()));
            BigDecimal price=lr.unitPrice()!=null?lr.unitPrice():Optional.ofNullable(p.getSellingPrice()).orElse(BigDecimal.ZERO);
            BigDecimal total=price.multiply(lr.quantity()); subtotal=subtotal.add(total);
            o.getLines().add(SalesOrderLine.builder().salesOrder(o).lineNumber(n++).product(p).quantity(lr.quantity()).unitPrice(price).discountAmount(BigDecimal.ZERO).taxAmount(BigDecimal.ZERO).lineTotal(total).build());
        }
        o.setSubtotal(subtotal); o.setTotalAmount(subtotal);
        if(Boolean.TRUE.equals(c.getCreditBlocked()) || (c.getCreditLimit()!=null && subtotal.compareTo(c.getCreditLimit())>0)){o.setCreditBlocked(true);o.setStatus(SalesOrderStatus.CREDIT_BLOCKED);}
        return orders.save(o);
    }

    @Transactional public SalesOrder confirmOrder(String number){
        SalesOrder o=order(number); if(o.getStatus()==SalesOrderStatus.CREDIT_BLOCKED)throw new IllegalStateException("Sales order is credit blocked");
        if(o.getStatus()!=SalesOrderStatus.DRAFT)throw new IllegalStateException("Only draft orders can be confirmed");
        for(SalesOrderLine l:o.getLines()) if(available(o.getWarehouse().getId(),l.getProduct().getId()).compareTo(l.getQuantity())<0) throw new IllegalStateException("Insufficient available stock for "+l.getProduct().getSku());
        o.setStatus(SalesOrderStatus.CONFIRMED); return o;
    }

    @Transactional public OutboundDelivery createDelivery(String number){
        SalesOrder o=order(number); if(o.getStatus()!=SalesOrderStatus.CONFIRMED&&o.getStatus()!=SalesOrderStatus.PARTIALLY_DELIVERED)throw new IllegalStateException("Order must be confirmed before delivery");
        OutboundDelivery d=OutboundDelivery.builder().deliveryNumber(number("DEL")).salesOrder(o).warehouse(o.getWarehouse()).deliveryDate(LocalDateTime.now()).status(DeliveryStatus.CREATED).build();
        for(SalesOrderLine l:o.getLines()){BigDecimal q=l.getQuantity().subtract(l.getQuantityDelivered());if(q.signum()>0)d.getLines().add(OutboundDeliveryLine.builder().delivery(d).salesOrderLine(l).product(l.getProduct()).quantity(q).build());}
        if(d.getLines().isEmpty())throw new IllegalStateException("No outstanding quantity to deliver"); return deliveries.save(d);
    }

    @Transactional public OutboundDelivery pick(String number){
        OutboundDelivery d=delivery(number); if(d.getStatus()!=DeliveryStatus.CREATED)throw new IllegalStateException("Delivery is not ready for picking");
        for(OutboundDeliveryLine l:d.getLines()){
            BigDecimal remaining=l.getQuantity();
            List<InventoryBin> stock=inventoryBins.findByProductId(l.getProduct().getId()).stream().filter(i->i.getWarehouse().getId().equals(d.getWarehouse().getId())).filter(i->i.getQuantityOnHand().subtract(i.getQuantityReserved()).signum()>0).toList();
            for(InventoryBin i:stock){BigDecimal free=i.getQuantityOnHand().subtract(i.getQuantityReserved());BigDecimal take=free.min(remaining);if(take.signum()<=0)continue;if(l.getBin()==null)l.setBin(i.getBin());i.setQuantityReserved(i.getQuantityReserved().add(take));l.setPickedQuantity(l.getPickedQuantity().add(take));remaining=remaining.subtract(take);if(remaining.signum()==0)break;}
            if(remaining.signum()>0)throw new IllegalStateException("Unable to pick complete quantity for "+l.getProduct().getSku());
        }
        d.setPicked(true);d.setStatus(DeliveryStatus.PICKED);return d;
    }

    @Transactional public OutboundDelivery pack(String number){OutboundDelivery d=delivery(number);if(d.getStatus()!=DeliveryStatus.PICKED)throw new IllegalStateException("Delivery must be picked first");for(OutboundDeliveryLine l:d.getLines())l.setPackedQuantity(l.getPickedQuantity());d.setPacked(true);d.setStatus(DeliveryStatus.PACKED);return d;}

    @Transactional public OutboundDelivery postGoodsIssue(String number){
        OutboundDelivery d=delivery(number);if(d.getStatus()!=DeliveryStatus.PACKED)throw new IllegalStateException("Delivery must be packed first");
        for(OutboundDeliveryLine l:d.getLines()){BigDecimal q=l.getPackedQuantity();if(q.signum()<=0||l.getBin()==null)throw new IllegalStateException("Invalid picked quantity/bin for "+l.getProduct().getSku());InventoryBin ib=inventoryBins.findByWarehouseIdAndBinIdAndProductId(d.getWarehouse().getId(),l.getBin().getId(),l.getProduct().getId()).orElseThrow(()->new IllegalStateException("Inventory bin not found"));if(ib.getQuantityOnHand().compareTo(q)<0)throw new IllegalStateException("Insufficient stock at bin "+l.getBin().getCode());ib.setQuantityOnHand(ib.getQuantityOnHand().subtract(q));ib.setQuantityReserved(ib.getQuantityReserved().subtract(q).max(BigDecimal.ZERO));l.setIssuedQuantity(q);l.getSalesOrderLine().setQuantityDelivered(l.getSalesOrderLine().getQuantityDelivered().add(q));}
        d.setGoodsIssuePosted(true);d.setGoodsIssueDate(LocalDateTime.now());d.setStatus(DeliveryStatus.GOODS_ISSUED);boolean complete=d.getSalesOrder().getLines().stream().allMatch(l->l.getQuantityDelivered().compareTo(l.getQuantity())>=0);d.getSalesOrder().setStatus(complete?SalesOrderStatus.DELIVERED:SalesOrderStatus.PARTIALLY_DELIVERED);return d;
    }

    @Transactional public CustomerInvoice createInvoice(String number){
        OutboundDelivery d=delivery(number);if(!d.getGoodsIssuePosted())throw new IllegalStateException("Goods Issue must be posted before billing");
        CustomerInvoice i=CustomerInvoice.builder().invoiceNumber(number("INV")).customer(d.getSalesOrder().getCustomer()).salesOrder(d.getSalesOrder()).delivery(d).invoiceDate(LocalDateTime.now()).status(InvoiceStatus.POSTED).currency(d.getSalesOrder().getCurrency()).build();BigDecimal total=BigDecimal.ZERO;
        for(OutboundDeliveryLine dl:d.getLines()){BigDecimal q=dl.getIssuedQuantity();BigDecimal t=q.multiply(dl.getSalesOrderLine().getUnitPrice());i.getLines().add(CustomerInvoiceLine.builder().invoice(i).salesOrderLine(dl.getSalesOrderLine()).product(dl.getProduct()).quantity(q).unitPrice(dl.getSalesOrderLine().getUnitPrice()).lineTotal(t).build());dl.getSalesOrderLine().setQuantityBilled(dl.getSalesOrderLine().getQuantityBilled().add(q));total=total.add(t);}i.setSubtotal(total);i.setTotalAmount(total);i.setBalanceDue(total);return invoices.save(i);
    }

    @Transactional public CustomerPayment receivePayment(PaymentRequest r){CustomerInvoice i=invoices.findByInvoiceNumber(r.invoiceNumber()).orElseThrow(()->new IllegalArgumentException("Invoice not found"));if(r.amount()==null||r.amount().signum()<=0)throw new IllegalArgumentException("Payment amount must be positive");if(r.amount().compareTo(i.getBalanceDue())>0)throw new IllegalArgumentException("Payment exceeds invoice balance");CustomerPayment p=payments.save(CustomerPayment.builder().paymentNumber(number("PAY")).customer(i.getCustomer()).invoice(i).paymentDate(LocalDateTime.now()).amount(r.amount()).paymentMethod(r.paymentMethod()).reference(r.reference()).status(PaymentStatus.CLEARED).build());i.setAmountPaid(i.getAmountPaid().add(r.amount()));i.setBalanceDue(i.getTotalAmount().subtract(i.getAmountPaid()));i.setStatus(i.getBalanceDue().signum()==0?InvoiceStatus.PAID:InvoiceStatus.PARTIALLY_PAID);return p;}

    private SalesOrder order(String n){return orders.findByOrderNumber(n).orElseThrow(()->new IllegalArgumentException("Sales order not found"));}
    private OutboundDelivery delivery(String n){return deliveries.findByDeliveryNumber(n).orElseThrow(()->new IllegalArgumentException("Delivery not found"));}
    private BigDecimal available(UUID w,UUID p){return inventoryBins.findByProductId(p).stream().filter(i->i.getWarehouse().getId().equals(w)).map(i->i.getQuantityOnHand().subtract(i.getQuantityReserved())).reduce(BigDecimal.ZERO,BigDecimal::add);}
    private String number(String prefix){return prefix+"-"+LocalDateTime.now().toString().replaceAll("[-:.T]","").substring(0,14)+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();}
}
