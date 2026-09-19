package com.digipals.wms.finance.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="consolidation_group_units") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsolidationGroupUnit { @EmbeddedId private ConsolidationGroupUnitId id; }