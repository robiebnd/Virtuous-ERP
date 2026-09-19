package com.digipals.wms.finance.repository;
import com.digipals.wms.finance.entity.FxRate; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface FxRateRepository extends JpaRepository<FxRate,UUID> {
    Optional<FxRate> findByRateDateAndFromCurrencyAndToCurrency(java.time.LocalDate rateDate,String fromCurrency,String toCurrency);
    Optional<FxRate> findTopByRateDateLessThanEqualAndFromCurrencyAndToCurrencyAndRateTypeOrderByRateDateDesc(java.time.LocalDate rateDate,String fromCurrency,String toCurrency,String rateType);
}