package com.digipals.wms.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="treasury_instruments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TreasuryInstrument {
    @Id @GeneratedValue(strategy=GenerationType.UUID) UUID id;
    @Column(name="instrument_number", nullable=false, unique=true, length=80) String instrumentNumber;
    @Column(name="instrument_type", nullable=false, length=30) String instrumentType;
    @Column(nullable=false, length=150) String counterparty;
    @Column(nullable=false, length=3) String currency;
    @Column(name="notional_amount", nullable=false, precision=19, scale=2) BigDecimal notionalAmount;
    @Column(name="trade_date", nullable=false) LocalDate tradeDate;
    @Column(name="maturity_date") LocalDate maturityDate;
    @Column(nullable=false, length=20) String status;
    @Column(name="valuation_amount", nullable=false, precision=19, scale=2) BigDecimal valuationAmount;
    @Column(name="last_valuation_date") LocalDate lastValuationDate;
    @Column(name="hedge_designated", nullable=false) boolean hedgeDesignated;
}