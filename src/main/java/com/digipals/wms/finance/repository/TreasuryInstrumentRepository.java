package com.digipals.wms.finance.repository;

import com.digipals.wms.finance.entity.TreasuryInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TreasuryInstrumentRepository extends JpaRepository<TreasuryInstrument,UUID> {
    Optional<TreasuryInstrument> findByInstrumentNumber(String instrumentNumber);
    List<TreasuryInstrument> findByStatusOrderByMaturityDateAsc(String status);
}