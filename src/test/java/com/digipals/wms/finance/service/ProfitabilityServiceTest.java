package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.ProfitabilityPostRequest;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfitabilityServiceTest {

    @Mock ProfitabilitySegmentRepository segments;
    @Mock AccountingLineRepository lines;
    @Mock CompanyCodeRepository companies;
    @Mock GlAccountRepository glAccounts;
    @Mock ProfitabilityPostingRepository profitabilityPostings;
    @Mock FinancePostingService postingService;

    private ProfitabilityService service;
    private UUID segmentId;

    @BeforeEach
    void setUp() {
        service = new ProfitabilityService(segments, lines, companies, glAccounts, profitabilityPostings, postingService);
        segmentId = UUID.randomUUID();
    }

    @Test
    void postsRevenueToGlAndCopa() {
        ProfitabilitySegment segment = ProfitabilitySegment.builder()
                .segmentCode("SEG-001").segmentName("Domestic").companyCode("ZW01").active(true).build();
        segment.setId(segmentId);
        GlAccount revenue = GlAccount.builder().accountCode("400000").accountName("Sales").accountType("REVENUE").controlAccount(false).build();
        AccountingLine line = AccountingLine.builder().glAccount(revenue).build();
        AccountingDocument document = AccountingDocument.builder().documentNumber("FI-TEST").status("POSTED").build();
        document.addLine(line);

        when(segments.findById(segmentId)).thenReturn(Optional.of(segment));
        when(glAccounts.findByAccountCode("400000")).thenReturn(Optional.of(revenue));
        when(postingService.postBalancedAtDate(anyString(), eq("COPA_POSTING"), eq("COPA_POSTING"), any(UUID.class),
                anyString(), eq("USD"), anyString(), anyList(), any(LocalDate.class).atStartOfDay()))
                .thenReturn(document);
        when(profitabilityPostings.save(any(ProfitabilityPosting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountingDocument result = service.post(new ProfitabilityPostRequest(
                "ZW01", segmentId, "400000", new BigDecimal("250.00"),
                true, "USD", LocalDate.of(2026, 9, 21), new BigDecimal("10")
        ));

        assertSame(document, result);
        verify(postingService).postBalancedAtDate(
                eq("ZW01"), eq("COPA_POSTING"), eq("COPA_POSTING"),
                any(UUID.class), eq(segmentId.toString()), eq("USD"),
                contains("CO-PA posting SEG-001"), anyList(), any()
        );
        verify(profitabilityPostings).save(any(ProfitabilityPosting.class));
    }

    @Test
    void rejectsRevenuePostingToExpenseAccount() {
        ProfitabilitySegment segment = ProfitabilitySegment.builder()
                .segmentCode("SEG-002").segmentName("Retail").companyCode("ZW01").active(true).build();
        segment.setId(segmentId);
        GlAccount expense = GlAccount.builder().accountCode("500000").accountName("COGS").accountType("EXPENSE").build();

        when(segments.findById(segmentId)).thenReturn(Optional.of(segment));
        when(glAccounts.findByAccountCode("500000")).thenReturn(Optional.of(expense));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.post(new ProfitabilityPostRequest(
                        "ZW01", segmentId, "500000", new BigDecimal("100.00"),
                        true, "USD", LocalDate.of(2026, 9, 21), BigDecimal.ONE
                ))
        );

        assertTrue(ex.getMessage().contains("revenue GL account"));
        verifyNoInteractions(postingService);
        verifyNoInteractions(profitabilityPostings);
    }

    @Test
    void rejectsZeroProfitabilityAmount() {
        ProfitabilitySegment segment = ProfitabilitySegment.builder()
                .segmentCode("SEG-003").segmentName("Wholesale").companyCode("ZW01").active(true).build();
        segment.setId(segmentId);
        GlAccount revenue = GlAccount.builder().accountCode("400000").accountName("Sales").accountType("REVENUE").build();

        when(segments.findById(segmentId)).thenReturn(Optional.of(segment));
        when(glAccounts.findByAccountCode("400000")).thenReturn(Optional.of(revenue));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.post(new ProfitabilityPostRequest(
                        "ZW01", segmentId, "400000", BigDecimal.ZERO,
                        true, "USD", LocalDate.of(2026, 9, 21), BigDecimal.ONE
                ))
        );

        assertTrue(ex.getMessage().contains("greater than zero"));
        verifyNoInteractions(postingService);
    }
}
