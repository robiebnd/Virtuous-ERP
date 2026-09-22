package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.entity.AccountingDocument;
import com.digipals.wms.finance.entity.GlAccount;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.finance.repository.GlAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancePostingServiceTest {

    @Mock AccountingDocumentRepository documentRepository;
    @Mock GlAccountRepository accountRepository;
    @Mock FiscalPeriodService fiscalPeriodService;

    private FinancePostingService service;

    @BeforeEach
    void setUp() {
        service = new FinancePostingService(documentRepository, accountRepository, fiscalPeriodService);
        when(documentRepository.findFirstByReferenceTypeAndReferenceIdAndStatus(anyString(), any(), eq("POSTED")))
                .thenReturn(Optional.empty());
        when(documentRepository.findFirstByReferenceTypeAndReferenceNumberAndStatus(anyString(), anyString(), eq("POSTED")))
                .thenReturn(Optional.empty());
        when(documentRepository.save(any(AccountingDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.findByAccountCode(anyString()))
                .thenAnswer(invocation -> Optional.of(GlAccount.builder()
                        .accountCode(invocation.getArgument(0))
                        .accountName("Test Account")
                        .accountType("ASSET")
                        .controlAccount(false)
                        .build()));
    }

    @Test
    void postsBalancedDocument() {
        UUID referenceId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.of(2026, 9, 21, 10, 0);

        AccountingDocument document = service.postBalancedAtDate(
                "ZW01", "TEST", "TEST_REF", referenceId, "TEST-001", "USD",
                "Balanced test posting",
                List.of(
                        new FinancePostingService.PostingLine("120000", new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null, null, "Debit"),
                        new FinancePostingService.PostingLine("400000", BigDecimal.ZERO, new BigDecimal("100.00"), null, null, null, null, "Credit")
                ),
                date
        );

        assertEquals("POSTED", document.getStatus());
        assertEquals("ZW01", document.getCompanyCode());
        assertEquals(new BigDecimal("100.00"), document.getTotalDebit());
        assertEquals(new BigDecimal("100.00"), document.getTotalCredit());
        assertEquals(2, document.getLines().size());
        verify(fiscalPeriodService).ensurePostingAllowed("ZW01", date);
        verify(documentRepository).save(any(AccountingDocument.class));
    }

    @Test
    void rejectsUnbalancedDocument() {
        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.postBalancedAtDate(
                        "ZW01", "TEST", "TEST_REF", UUID.randomUUID(), "TEST-002", "USD",
                        "Unbalanced",
                        List.of(
                                new FinancePostingService.PostingLine("120000", new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null, null, "Debit"),
                                new FinancePostingService.PostingLine("400000", BigDecimal.ZERO, new BigDecimal("90.00"), null, null, null, null, "Credit")
                        ),
                        LocalDateTime.of(2026, 9, 21, 10, 0)
                )
        );

        assertTrue(ex.getMessage().contains("balanced"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void blocksPostingToClosedFiscalPeriod() {
        LocalDateTime date = LocalDateTime.of(2026, 9, 21, 10, 0);
        doThrow(new InvalidWorkflowException("Posting is blocked because fiscal period is CLOSED."))
                .when(fiscalPeriodService).ensurePostingAllowed("ZW01", date);

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.postBalancedAtDate(
                        "ZW01", "TEST", "TEST_REF", UUID.randomUUID(), "TEST-003", "USD",
                        "Closed period",
                        List.of(
                                new FinancePostingService.PostingLine("120000", new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null, null, "Debit"),
                                new FinancePostingService.PostingLine("400000", BigDecimal.ZERO, new BigDecimal("100.00"), null, null, null, null, "Credit")
                        ),
                        date
                )
        );

        assertTrue(ex.getMessage().contains("CLOSED"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateReferenceId() {
        UUID referenceId = UUID.randomUUID();
        when(documentRepository.findFirstByReferenceTypeAndReferenceIdAndStatus("TEST_REF", referenceId, "POSTED"))
                .thenReturn(Optional.of(AccountingDocument.builder().documentNumber("FI-OLD").status("POSTED").build()));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.postBalancedAtDate(
                        "ZW01", "TEST", "TEST_REF", referenceId, "TEST-004", "USD",
                        "Duplicate",
                        List.of(
                                new FinancePostingService.PostingLine("120000", new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null, null, "Debit"),
                                new FinancePostingService.PostingLine("400000", BigDecimal.ZERO, new BigDecimal("100.00"), null, null, null, null, "Credit")
                        ),
                        LocalDateTime.of(2026, 9, 21, 10, 0)
                )
        );

        assertTrue(ex.getMessage().contains("already posted"));
        verify(documentRepository, never()).save(any());
        verifyNoInteractions(fiscalPeriodService);
    }

    @Test
    void rejectsDuplicateReferenceNumber() {
        when(documentRepository.findFirstByReferenceTypeAndReferenceNumberAndStatus("TEST_REF", "TEST-005", "POSTED"))
                .thenReturn(Optional.of(AccountingDocument.builder().documentNumber("FI-OLD").status("POSTED").build()));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.postBalancedAtDate(
                        "ZW01", "TEST", "TEST_REF", null, "TEST-005", "USD",
                        "Duplicate number",
                        List.of(
                                new FinancePostingService.PostingLine("120000", new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null, null, "Debit"),
                                new FinancePostingService.PostingLine("400000", BigDecimal.ZERO, new BigDecimal("100.00"), null, null, null, null, "Credit")
                        ),
                        LocalDateTime.of(2026, 9, 21, 10, 0)
                )
        );

        assertTrue(ex.getMessage().contains("reference"));
        verify(documentRepository, never()).save(any());
        verifyNoInteractions(fiscalPeriodService);
    }
}
