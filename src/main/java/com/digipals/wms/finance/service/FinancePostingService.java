package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.entity.AccountingDocument;
import com.digipals.wms.finance.entity.AccountingLine;
import com.digipals.wms.finance.entity.GlAccount;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.finance.repository.GlAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancePostingService {
    private static final String POSTED = "POSTED";
    private static final String COMPANY_CODE = "ZW01";
    private final AccountingDocumentRepository documentRepository;
    private final GlAccountRepository accountRepository;
    private final FiscalPeriodService fiscalPeriodService;

    public AccountingDocument postBalanced(String documentType, String referenceType, UUID referenceId, String referenceNumber, String currency, String description, List<PostingLine> postings) {
        return postBalancedAtDate(documentType, referenceType, referenceId, referenceNumber, currency, description, postings, LocalDateTime.now());
    }

    public AccountingDocument postBalancedAtDate(String documentType, String referenceType, UUID referenceId, String referenceNumber, String currency, String description, List<PostingLine> postings, LocalDateTime postingDate) {
        return postBalancedAtDate(COMPANY_CODE, documentType, referenceType, referenceId, referenceNumber, currency, description, postings, postingDate);
    }

    public AccountingDocument postBalancedAtDate(String companyCode, String documentType, String referenceType, UUID referenceId, String referenceNumber, String currency, String description, List<PostingLine> postings, LocalDateTime postingDate) {
        return postBalancedAtDateInternal(companyCode, documentType, referenceType, referenceId, referenceNumber, currency, description, postings, postingDate, true);
    }

    public AccountingDocument postSystemBalancedAtDate(String documentType, String referenceType, UUID referenceId, String referenceNumber, String currency, String description, List<PostingLine> postings, LocalDateTime postingDate) {
        return postBalancedAtDateInternal(COMPANY_CODE, documentType, referenceType, referenceId, referenceNumber, currency, description, postings, postingDate, false);
    }

    private AccountingDocument postBalancedAtDateInternal(String companyCode, String documentType, String referenceType, UUID referenceId, String referenceNumber, String currency, String description, List<PostingLine> postings, LocalDateTime postingDate, boolean enforceFiscalPeriod) {
        if (referenceId != null && documentRepository.findFirstByReferenceTypeAndReferenceIdAndStatus(referenceType, referenceId, POSTED).isPresent()) throw new InvalidWorkflowException("Accounting document already posted for " + referenceType + " " + referenceNumber + ".");
        if (postingDate == null) throw new InvalidWorkflowException("Posting date is required.");
        String normalizedCompanyCode = companyCode == null || companyCode.isBlank() ? COMPANY_CODE : companyCode.trim().toUpperCase(Locale.ROOT);
        if (enforceFiscalPeriod) fiscalPeriodService.ensurePostingAllowed(normalizedCompanyCode, postingDate);
        if (postings == null || postings.size() < 2) throw new InvalidWorkflowException("Accounting document requires at least two lines.");
        BigDecimal totalDebit = postings.stream().map(p -> nvl(p.debit())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCredit = postings.stream().map(p -> nvl(p.credit())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        if (totalDebit.compareTo(BigDecimal.ZERO) <= 0 || totalDebit.compareTo(totalCredit) != 0) throw new InvalidWorkflowException("Accounting document must be balanced: debit must equal credit and be greater than zero.");
        String normalizedCurrency = currency == null ? "USD" : currency.trim().toUpperCase(Locale.ROOT);
        if (!normalizedCurrency.matches("[A-Z]{3}")) throw new InvalidWorkflowException("Accounting currency must be a 3-letter ISO code.");
        LocalDateTime now = LocalDateTime.now();
        AccountingDocument document = AccountingDocument.builder().documentNumber(nextDocumentNumber()).documentType(documentType).documentDate(postingDate).postingDate(postingDate).companyCode(normalizedCompanyCode).currency(normalizedCurrency).referenceType(referenceType).referenceId(referenceId).referenceNumber(referenceNumber).description(description).totalDebit(totalDebit).totalCredit(totalCredit).status(POSTED).build();
        int lineNumber = 1;
        for (PostingLine posting : postings) {
            GlAccount account = accountRepository.findByAccountCode(posting.accountCode()).orElseThrow(() -> new InvalidWorkflowException("GL account not configured: " + posting.accountCode()));
            BigDecimal debit = nvl(posting.debit()).setScale(2, RoundingMode.HALF_UP); BigDecimal credit = nvl(posting.credit()).setScale(2, RoundingMode.HALF_UP);
            if (debit.compareTo(BigDecimal.ZERO) < 0 || credit.compareTo(BigDecimal.ZERO) < 0 || (debit.compareTo(BigDecimal.ZERO) > 0 && credit.compareTo(BigDecimal.ZERO) > 0)) throw new InvalidWorkflowException("Each accounting line must contain either a debit or a credit.");
            document.addLine(AccountingLine.builder().glAccount(account).lineNumber(lineNumber++).debit(debit).credit(credit).companyCode(normalizedCompanyCode).partnerCompanyCode(posting.partnerCompanyCode()).taxCode(posting.taxCode()).taxBase(nvl(posting.taxBase())).taxAmount(nvl(posting.taxAmount())).profitabilitySegmentId(posting.profitabilitySegmentId()).costCenter(posting.costCenter()).profitCenter(posting.profitCenter()).functionalArea(posting.functionalArea()).segment(posting.segment()).lineText(posting.lineText()).internalOrderCode(posting.internalOrderCode()).wbsElement(posting.wbsElement()).build());
        }
        return documentRepository.save(document);
    }

    public AccountingDocument postGoodsReceipt(UUID id, String grnNumber, String currency, BigDecimal amount) {
        return postBalanced("GOODS_RECEIPT", "GRN", id, grnNumber, currency, "Goods receipt " + grnNumber, List.of(new PostingLine("110000", amount, BigDecimal.ZERO, null, null, null, null, "Inventory receipt"), new PostingLine("210000", BigDecimal.ZERO, amount, null, null, null, null, "GR/IR liability")));
    }

    public AccountingDocument postGoodsIssueWithCogs(UUID deliveryId, String deliveryNumber, String currency, BigDecimal cogsAmount) {
        BigDecimal cogs = nvl(cogsAmount).setScale(2, RoundingMode.HALF_UP);
        if (cogs.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Goods issue COGS must be greater than zero.");
        return postBalanced("GOODS_ISSUE", "OUTBOUND_DELIVERY", deliveryId, deliveryNumber, currency, "Goods issue and inventory consumption " + deliveryNumber, List.of(
                new PostingLine("500000", cogs, BigDecimal.ZERO, null, null, null, null, "Cost of goods sold"),
                new PostingLine("110000", BigDecimal.ZERO, cogs, null, null, null, null, "Inventory consumption at PGI")
        ));
    }

    public AccountingDocument postVendorInvoice(UUID id, String invoiceNumber, String currency, BigDecimal amount) {
        return postBalanced("VENDOR_INVOICE", "VENDOR_INVOICE", id, invoiceNumber, currency, "Vendor invoice " + invoiceNumber, List.of(new PostingLine("210000", amount, BigDecimal.ZERO, null, null, null, null, "Clear GR/IR"), new PostingLine("200000", BigDecimal.ZERO, amount, null, null, null, null, "Vendor payable")));
    }

    public AccountingDocument postVendorPayment(UUID id, String paymentNumber, String currency, BigDecimal amount) {
        return postBalanced("VENDOR_PAYMENT", "VENDOR_PAYMENT", id, paymentNumber, currency, "Vendor payment " + paymentNumber, List.of(new PostingLine("200000", amount, BigDecimal.ZERO, null, null, null, null, "Clear vendor payable"), new PostingLine("100000", BigDecimal.ZERO, amount, null, null, null, null, "Bank payment")));
    }

    public AccountingDocument postCustomerInvoice(UUID id, String billingNumber, String currency, BigDecimal amount) {
        return postBalanced("CUSTOMER_INVOICE", "BILLING_DOCUMENT", id, billingNumber, currency, "Customer invoice " + billingNumber, List.of(new PostingLine("120000", amount, BigDecimal.ZERO, null, null, null, null, "Customer receivable"), new PostingLine("400000", BigDecimal.ZERO, amount, null, null, null, null, "Sales revenue")));
    }

    public AccountingDocument postCustomerInvoiceWithCogs(UUID id, String billingNumber, String currency, BigDecimal revenueAmount, BigDecimal cogsAmount) {
        BigDecimal revenue = nvl(revenueAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cogs = nvl(cogsAmount).setScale(2, RoundingMode.HALF_UP);
        if (revenue.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidWorkflowException("Customer invoice revenue must be greater than zero.");
        if (cogs.compareTo(BigDecimal.ZERO) < 0) throw new InvalidWorkflowException("COGS cannot be negative.");
        java.util.ArrayList<PostingLine> lines = new java.util.ArrayList<>(List.of(new PostingLine("120000", revenue, BigDecimal.ZERO, null, null, null, null, "Customer receivable"), new PostingLine("400000", BigDecimal.ZERO, revenue, null, null, null, null, "Sales revenue")));
        if (cogs.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(new PostingLine("500000", cogs, BigDecimal.ZERO, null, null, null, null, "Cost of goods sold"));
            lines.add(new PostingLine("110000", BigDecimal.ZERO, cogs, null, null, null, null, "Inventory consumption"));
        }
        return postBalanced("CUSTOMER_INVOICE", "BILLING_DOCUMENT", id, billingNumber, currency, "Customer invoice and COGS " + billingNumber, lines);
    }

    public AccountingDocument postIncomingPayment(UUID id, String paymentNumber, String currency, BigDecimal paymentAmount, BigDecimal appliedAmount) {
        BigDecimal payment = nvl(paymentAmount); BigDecimal applied = nvl(appliedAmount); BigDecimal unapplied = payment.subtract(applied);
        if (payment.compareTo(BigDecimal.ZERO) <= 0 || applied.compareTo(BigDecimal.ZERO) <= 0 || unapplied.compareTo(BigDecimal.ZERO) < 0) throw new InvalidWorkflowException("Invalid incoming payment clearing amounts.");
        List<PostingLine> lines = new java.util.ArrayList<>(List.of(new PostingLine("100000", payment, BigDecimal.ZERO, null, null, null, null, "Bank receipt"), new PostingLine("120000", BigDecimal.ZERO, applied, null, null, null, null, "Clear customer receivable")));
        if (unapplied.compareTo(BigDecimal.ZERO) > 0) lines.add(new PostingLine("220000", BigDecimal.ZERO, unapplied, null, null, null, null, "Customer advance / unapplied cash"));
        return postBalanced("INCOMING_PAYMENT", "INCOMING_PAYMENT", id, paymentNumber, currency, "Incoming payment " + paymentNumber, lines);
    }

    private BigDecimal nvl(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private String nextDocumentNumber() { return "FI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT); }
    public record PostingLine(String accountCode, BigDecimal debit, BigDecimal credit, String costCenter, String profitCenter, String functionalArea, String segment, String lineText, String internalOrderCode, String wbsElement, String partnerCompanyCode, String taxCode, BigDecimal taxBase, BigDecimal taxAmount, UUID profitabilitySegmentId) {
        public PostingLine(String accountCode, BigDecimal debit, BigDecimal credit, String costCenter, String profitCenter, String functionalArea, String segment, String lineText) {
            this(accountCode, debit, credit, costCenter, profitCenter, functionalArea, segment, lineText, null, null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null);
        }
        public PostingLine(String accountCode, BigDecimal debit, BigDecimal credit, String costCenter, String profitCenter, String functionalArea, String segment, String lineText, String internalOrderCode, String wbsElement) {
            this(accountCode, debit, credit, costCenter, profitCenter, functionalArea, segment, lineText, internalOrderCode, wbsElement, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null);
        }
        public PostingLine withPartner(String partnerCompanyCode) {
            return new PostingLine(accountCode, debit, credit, costCenter, profitCenter, functionalArea, segment, lineText, internalOrderCode, wbsElement, partnerCompanyCode, taxCode, taxBase, taxAmount, profitabilitySegmentId);
        }
    }
}
