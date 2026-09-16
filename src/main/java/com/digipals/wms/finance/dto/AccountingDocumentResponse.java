package com.digipals.wms.finance.dto;

import com.digipals.wms.finance.entity.AccountingDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AccountingDocumentResponse(UUID id, String documentNumber, String documentType,
                                         LocalDateTime documentDate, LocalDateTime postingDate,
                                         String companyCode, String currency, String referenceType,
                                         UUID referenceId, String referenceNumber, String description,
                                         BigDecimal totalDebit, BigDecimal totalCredit, String status,
                                         List<Line> lines) {
    public static AccountingDocumentResponse from(AccountingDocument document) {
        return new AccountingDocumentResponse(document.getId(), document.getDocumentNumber(), document.getDocumentType(),
                document.getDocumentDate(), document.getPostingDate(), document.getCompanyCode(), document.getCurrency(),
                document.getReferenceType(), document.getReferenceId(), document.getReferenceNumber(), document.getDescription(),
                document.getTotalDebit(), document.getTotalCredit(), document.getStatus(),
                document.getLines().stream().map(line -> new Line(line.getLineNumber(), line.getGlAccount().getAccountCode(),
                        line.getGlAccount().getAccountName(), line.getDebit(), line.getCredit(), line.getCostCenter(),
                        line.getProfitCenter(), line.getFunctionalArea(), line.getSegment(), line.getLineText())).toList());
    }
    public record Line(Integer lineNumber, String accountCode, String accountName, BigDecimal debit, BigDecimal credit,
                       String costCenter, String profitCenter, String functionalArea, String segment, String lineText) {}
}
