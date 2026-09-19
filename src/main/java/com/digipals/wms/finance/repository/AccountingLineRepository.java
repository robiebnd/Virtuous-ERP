package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.AccountingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountingLineRepository extends JpaRepository<AccountingLine, UUID> {
    @Query("select l.glAccount.accountCode, l.glAccount.accountName, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) " +
           "from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' " +
           "group by l.glAccount.accountCode, l.glAccount.accountName order by l.glAccount.accountCode")
    List<Object[]> trialBalance();

    @Query("select l.costCenter, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.costCenter is not null group by l.costCenter order by l.costCenter")
    List<Object[]> costCenterActuals();

    @Query("select l.internalOrderCode, coalesce(sum(l.debit),0), coalesce(sum(l.credit),0) from AccountingLine l join l.accountingDocument d where d.status = 'POSTED' and l.internalOrderCode is not null group by l.internalOrderCode order by l.internalOrderCode");
    List<Object[]> internalOrderActuals();

}
