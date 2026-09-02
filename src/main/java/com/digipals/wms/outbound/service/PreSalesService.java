package com.digipals.wms.outbound.service;

import com.digipals.wms.outbound.dto.PreSalesRequests.*;
import com.digipals.wms.outbound.entity.*;
import com.digipals.wms.outbound.repository.SalesInquiryRepository;
import com.digipals.wms.outbound.repository.SalesOrderRepository;
import com.digipals.wms.outbound.repository.SalesQuotationRepository;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.warehouse.entity.Warehouse;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreSalesService {
    private final SalesInquiryRepository inquiryRepository;
    private final SalesQuotationRepository quotationRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final EntityManager entityManager;

    @Transactional
    public SalesInquiry createInquiry(CreateInquiryRequest r) {
        Customer customer = customer(r.customerNumber());
        SalesInquiry inquiry = SalesInquiry.builder()
                .inquiryNumber(nextNumber("INQ"))
                .customer(customer)
                .salesAreaId(salesAreaId(r.salesAreaCode()))
                .inquiryDate(LocalDateTime.now())
                .requestedValidUntil(r.requestedValidUntil())
                .currency(defaultCurrency(r.currency()))
                .notes(r.notes())
                .status(SalesInquiryStatus.DRAFT)
                .build();

        List<SalesInquiryLine> lines = new ArrayList<>();
        int lineNo = 10;
        for (InquiryLineRequest lr : r.lines()) {
            product(lr.sku());
            lines.add(SalesInquiryLine.builder()
                    .inquiry(inquiry).lineNumber(lineNo).sku(lr.sku())
                    .description(lr.description()).quantity(lr.quantity())
                    .requestedDeliveryDate(lr.requestedDeliveryDate()).build());
            lineNo += 10;
        }
        inquiry.setLines(lines);
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public SalesQuotation createQuotation(CreateQuotationRequest r) {
        if (r.validTo().isBefore(r.validFrom()))
            throw bad("Quotation valid-to date cannot be before valid-from date");

        Customer customer = customer(r.customerNumber());
        SalesInquiry inquiry = null;
        if (r.inquiryNumber() != null && !r.inquiryNumber().isBlank()) {
            inquiry = inquiryRepository.findByInquiryNumber(r.inquiryNumber())
                    .orElseThrow(() -> bad("Inquiry not found: " + r.inquiryNumber()));
            if (!inquiry.getCustomer().getId().equals(customer.getId()))
                throw bad("Quotation customer does not match inquiry customer");
            inquiry.setStatus(SalesInquiryStatus.RESPONDED);
        }

        SalesQuotation quotation = SalesQuotation.builder()
                .quotationNumber(nextNumber("QT"))
                .inquiry(inquiry)
                .customer(customer)
                .salesAreaId(salesAreaId(r.salesAreaCode()))
                .quotationDate(LocalDateTime.now())
                .validFrom(r.validFrom()).validTo(r.validTo())
                .status(SalesQuotationStatus.DRAFT)
                .currency(defaultCurrency(r.currency()))
                .notes(r.notes())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        List<SalesQuotationLine> lines = new ArrayList<>();
        int lineNo = 10;
        for (QuotationLineRequest lr : r.lines()) {
            product(lr.sku());
            BigDecimal lineDiscount = nz(lr.discountAmount());
            BigDecimal lineTax = nz(lr.taxAmount());
            BigDecimal gross = lr.quantity().multiply(lr.unitPrice());
            BigDecimal lineTotal = gross.subtract(lineDiscount).add(lineTax);
            subtotal = subtotal.add(gross);
            discount = discount.add(lineDiscount);
            tax = tax.add(lineTax);
            lines.add(SalesQuotationLine.builder()
                    .quotation(quotation).lineNumber(lineNo).sku(lr.sku())
                    .description(lr.description()).quantity(lr.quantity())
                    .unitPrice(lr.unitPrice()).discountAmount(lineDiscount)
                    .taxAmount(lineTax).lineTotal(lineTotal)
                    .requestedDeliveryDate(lr.requestedDeliveryDate()).build());
            lineNo += 10;
        }
        quotation.setLines(lines);
        quotation.setSubtotal(subtotal);
        quotation.setDiscountAmount(discount);
        quotation.setTaxAmount(tax);
        quotation.setTotalAmount(subtotal.subtract(discount).add(tax));
        return quotationRepository.save(quotation);
    }

    @Transactional
    public SalesQuotation sendQuotation(String quotationNumber) {
        SalesQuotation q = quotation(quotationNumber);
        ensureNotExpired(q);
        if (q.getStatus() != SalesQuotationStatus.DRAFT)
            throw bad("Only a DRAFT quotation can be sent");
        q.setStatus(SalesQuotationStatus.SENT);
        return q;
    }

    @Transactional
    public SalesQuotation acceptQuotation(String quotationNumber) {
        SalesQuotation q = quotation(quotationNumber);
        ensureNotExpired(q);
        if (q.getStatus() != SalesQuotationStatus.SENT)
            throw bad("Only a SENT quotation can be accepted");
        q.setStatus(SalesQuotationStatus.ACCEPTED);
        return q;
    }

    @Transactional
    public SalesOrder convertQuotation(String quotationNumber, ConvertQuotationRequest r) {
        SalesQuotation q = quotation(quotationNumber);
        ensureNotExpired(q);
        if (q.getStatus() != SalesQuotationStatus.ACCEPTED)
            throw bad("Quotation must be ACCEPTED before conversion to a sales order");
        if (q.getConvertedOrderNumber() != null)
            throw bad("Quotation has already been converted: " + q.getConvertedOrderNumber());

        Warehouse warehouse = warehouseRepository.findByCode(r.warehouseCode())
                .orElseThrow(() -> bad("Warehouse not found: " + r.warehouseCode()));

        LocalDateTime requestedDate = r.requestedDeliveryDate() != null
                ? r.requestedDeliveryDate()
                : q.getValidTo().atTime(23, 59, 59);

        SalesOrder order = SalesOrder.builder()
                .orderNumber(nextNumber("SO"))
                .customer(q.getCustomer())
                .warehouse(warehouse)
                .orderDate(LocalDateTime.now())
                .requestedDeliveryDate(requestedDate)
                .status(SalesOrderStatus.DRAFT)
                .currency(q.getCurrency())
                .paymentTerms(r.paymentTerms() != null ? r.paymentTerms() : q.getCustomer().getPaymentTerms())
                .subtotal(q.getSubtotal()).discountAmount(q.getDiscountAmount())
                .taxAmount(q.getTaxAmount()).totalAmount(q.getTotalAmount())
                .creditBlocked(false)
                .build();

        List<SalesOrderLine> orderLines = new ArrayList<>();
        for (SalesQuotationLine ql : q.getLines()) {
            Product product = product(ql.getSku());
            orderLines.add(SalesOrderLine.builder()
                    .salesOrder(order).lineNumber(ql.getLineNumber()).product(product)
                    .quantity(ql.getQuantity()).unitPrice(ql.getUnitPrice())
                    .discountAmount(ql.getDiscountAmount()).taxAmount(ql.getTaxAmount())
                    .lineTotal(ql.getLineTotal()).build());
        }
        order.setLines(orderLines);
        SalesOrder saved = salesOrderRepository.save(order);

        q.setConvertedOrderNumber(saved.getOrderNumber());
        q.setStatus(SalesQuotationStatus.CONVERTED);
        return saved;
    }

    private Customer customer(String number) {
        return entityManager.createQuery("select c from Customer c where upper(c.customerNumber)=upper(:number)", Customer.class)
                .setParameter("number", number).getResultStream().findFirst()
                .orElseThrow(() -> bad("Customer not found: " + number));
    }

    private Product product(String sku) {
        return productRepository.findBySkuIgnoreCase(sku)
                .orElseThrow(() -> bad("Product not found: " + sku));
    }

    private java.util.UUID salesAreaId(String code) {
        if (code == null || code.isBlank()) return null;
        return entityManager.createQuery(
                "select s.id from SdFoundationEntities$SalesArea s where upper(s.code)=upper(:code)", java.util.UUID.class)
                .setParameter("code", code).getResultStream().findFirst()
                .orElseThrow(() -> bad("Sales area not found: " + code));
    }

    private Warehouse warehouse(String code) {
        return warehouseRepository.findByCode(code)
                .orElseThrow(() -> bad("Warehouse not found: " + code));
    }

    private SalesQuotation quotation(String number) {
        return quotationRepository.findByQuotationNumber(number)
                .orElseThrow(() -> bad("Quotation not found: " + number));
    }

    private void ensureNotExpired(SalesQuotation q) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(q.getValidFrom()) || today.isAfter(q.getValidTo())) {
            if (q.getStatus() != SalesQuotationStatus.CONVERTED)
                q.setStatus(SalesQuotationStatus.EXPIRED);
            throw bad("Quotation is outside its validity period");
        }
    }

    private String nextNumber(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private String defaultCurrency(String currency) { return currency == null || currency.isBlank() ? "USD" : currency; }
    private BigDecimal nz(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
