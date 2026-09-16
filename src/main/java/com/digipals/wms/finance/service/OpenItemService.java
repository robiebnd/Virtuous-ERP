package com.digipals.wms.finance.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.finance.dto.OpenItemResponse;
import com.digipals.wms.finance.entity.AccountingDocument;
import com.digipals.wms.finance.entity.AccountingLine;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.vendorinvoice.entity.VendorInvoice;
import com.digipals.wms.vendorinvoice.entity.VendorInvoiceStatus;
import com.digipals.wms.vendorinvoice.repository.VendorInvoiceRepository;
import com.digipals.wms.vendorpayment.entity.VendorPayment;
import com.digipals.wms.vendorpayment.entity.VendorPaymentStatus;
import com.digipals.wms.vendorpayment.repository.VendorPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenItemService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
    private final VendorInvoiceRepository vendorInvoiceRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final BillingDocumentRepository billingDocumentRepository;
    private final AccountingDocumentRepository accountingDocumentRepository;

    public List<OpenItemResponse> accountsPayable() {
        List<OpenItemResponse> result = new ArrayList<>();
        for (VendorInvoice invoice : vendorInvoiceRepository.findAllByOrderByInvoiceDateDesc()) {
            if (!(invoice.getStatus() == VendorInvoiceStatus.MATCHED || invoice.getStatus() == VendorInvoiceStatus.POSTED || invoice.getStatus() == VendorInvoiceStatus.PAID)) continue;
            BigDecimal original = nvl(invoice.getTotalAmount());
            BigDecimal cleared = vendorPaymentRepository.findAllByVendorInvoiceIdOrderByPaymentDateDesc(invoice.getId()).stream()
                    .filter(p -> p.getStatus() == VendorPaymentStatus.PAID)
                    .map(VendorPayment::getAmount).map(this::nvl).reduce(ZERO, BigDecimal::add);
            BigDecimal open = maxZero(original.subtract(cleared));
            LocalDateTime dueDate = invoice.getInvoiceDate() == null ? null : invoice.getInvoiceDate().plusDays(30);
            result.add(toResponse(invoice.getId(), "AP", invoice.getInvoiceNumber(),
                    invoice.getSupplier() == null ? null : invoice.getSupplier().getCode(),
                    invoice.getSupplier() == null ? null : invoice.getSupplier().getName(),
                    invoice.getInvoiceDate(), dueDate, invoice.getCurrency(), original, cleared, open));
        }
        return result;
    }

    public List<OpenItemResponse> accountsReceivable() {
        List<BillingDocument> invoices = billingDocumentRepository.findAll().stream()
                .filter(b -> b.getStatus() == BillingStatus.POSTED)
                .sorted(Comparator.comparing(BillingDocument::getBillingDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        BigDecimal unappliedAppliedPool = accountingDocumentRepository.findAllByOrderByPostingDateDesc().stream()
                .filter(d -> "INCOMING_PAYMENT".equalsIgnoreCase(d.getDocumentType()) && "POSTED".equalsIgnoreCase(d.getStatus()))
                .flatMap(d -> d.getLines().stream())
                .filter(l -> l.getGlAccount() != null && "120000".equals(l.getGlAccount().getAccountCode()))
                .map(AccountingLine::getCredit).map(this::nvl).reduce(ZERO, BigDecimal::add);

        List<OpenItemResponse> result = new ArrayList<>();
        for (BillingDocument invoice : invoices) {
            BigDecimal original = nvl(invoice.getTotalAmount());
            BigDecimal cleared = min(original, unappliedAppliedPool);
            unappliedAppliedPool = maxZero(unappliedAppliedPool.subtract(cleared));
            BigDecimal open = maxZero(original.subtract(cleared));
            result.add(toResponse(invoice.getId(), "AR", invoice.getBillingNumber(), invoice.getCustomerCode(),
                    invoice.getCustomerCode(), invoice.getBillingDate(), invoice.getDueDate(), invoice.getCurrency(), original, cleared, open));
        }
        return result;
    }

    public AgeingSummary ageing(String type) {
        List<OpenItemResponse> items = "AP".equalsIgnoreCase(type) ? accountsPayable() : accountsReceivable();
        BigDecimal current = ZERO, b1 = ZERO, b2 = ZERO, b3 = ZERO, b4 = ZERO;
        for (OpenItemResponse item : items) {
            if (item.openAmount() == null || item.openAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
            switch (item.ageingBucket()) {
                case "CURRENT" -> current = current.add(item.openAmount());
                case "1_30" -> b1 = b1.add(item.openAmount());
                case "31_60" -> b2 = b2.add(item.openAmount());
                case "61_90" -> b3 = b3.add(item.openAmount());
                default -> b4 = b4.add(item.openAmount());
            }
        }
        return new AgeingSummary(type.toUpperCase(Locale.ROOT), current, b1, b2, b3, b4, current.add(b1).add(b2).add(b3).add(b4), items.size());
    }

    private OpenItemResponse toResponse(UUID id, String type, String reference, String partyCode, String partyName,
                                        LocalDateTime documentDate, LocalDateTime dueDate, String currency,
                                        BigDecimal original, BigDecimal cleared, BigDecimal open) {
        LocalDate due = dueDate == null ? null : dueDate.toLocalDate();
        long days = due == null ? 0 : ChronoUnit.DAYS.between(due, LocalDate.now());
        String bucket = days <= 0 ? "CURRENT" : days <= 30 ? "1_30" : days <= 60 ? "31_60" : days <= 90 ? "61_90" : "91_PLUS";
        String status = open.compareTo(BigDecimal.ZERO) == 0 ? "CLEARED" : cleared.compareTo(BigDecimal.ZERO) > 0 ? "PARTIALLY_CLEARED" : "OPEN";
        return new OpenItemResponse(id, type, reference, partyCode, partyName, documentDate, dueDate, currency,
                original.setScale(2), cleared.setScale(2), open.setScale(2), days, bucket, status);
    }

    private BigDecimal nvl(BigDecimal value) { return value == null ? ZERO : value; }
    private BigDecimal maxZero(BigDecimal value) { return value.compareTo(BigDecimal.ZERO) < 0 ? ZERO : value; }
    private BigDecimal min(BigDecimal a, BigDecimal b) { return a.compareTo(b) < 0 ? a : b; }

    public record AgeingSummary(String type, BigDecimal current, BigDecimal days1To30, BigDecimal days31To60,
                                BigDecimal days61To90, BigDecimal days91Plus, BigDecimal totalOpen, int itemCount) {}
}
