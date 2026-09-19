package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.AccountingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountingLineRepository extends JpaRepository<AccountingLine, UUID> {
    @Query("select l.glAccount.accountCode, l.glAccount.accountName, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) " +
           "from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' " +
           "group by l.glAccount.accountCode, l.glAccount.accountName order by l.glAccount.accountCode")
    List<Object[]> trialBalance();

    @Query("select l.costCenter, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.costCenter is not null and l.glAccount.accountType = 'EXPENSE' group by l.costCenter order by l.costCenter")
    List<Object[]> costCenterActuals();

    @Query("select l.internalOrderCode, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.internalOrderCode is not null and l.glAccount.accountType = 'EXPENSE' group by l.internalOrderCode order by l.internalOrderCode")
    List<Object[]> internalOrderActuals();

    @Query("select l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and EXTRACT(YEAR FROM d.postingDate) = :fiscalYear group by l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType order by l.glAccount.accountCode")
    List<Object[]> yearEndBalances(@Param("fiscalYear") int fiscalYear);

    @Query("select coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.internalOrderCode = :orderCode and l.glAccount.accountType = 'EXPENSE'")
    Object[] internalOrderActual(@Param("orderCode") String orderCode);

    @Query("select l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.companyCode = :companyCode and d.postingDate >= :startDate and d.postingDate < :endDate group by l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType order by l.glAccount.accountCode")
    List<Object[]> companyPeriodBalances(@Param("companyCode") String companyCode, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("select l.companyCode, l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.companyCode in :companyCodes and d.postingDate >= :startDate and d.postingDate < :endDate group by l.companyCode, l.glAccount.accountCode, l.glAccount.accountName, l.glAccount.accountType order by l.companyCode, l.glAccount.accountCode")
    List<Object[]> groupPeriodBalances(@Param("companyCodes") List<String> companyCodes, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("select l.profitabilitySegmentId, l.glAccount.accountType, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.companyCode = :companyCode and l.profitabilitySegmentId is not null and d.postingDate >= :startDate and d.postingDate < :endDate group by l.profitabilitySegmentId, l.glAccount.accountType")
    List<Object[]> profitabilityBySegment(@Param("companyCode") String companyCode, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
    @Query("select l.glAccount.accountCode, l.glAccount.accountName, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.companyCode = :companyCode group by l.glAccount.accountCode, l.glAccount.accountName order by l.glAccount.accountCode")
    List<Object[]> trialBalanceByCompany(@Param("companyCode") String companyCode);

}
