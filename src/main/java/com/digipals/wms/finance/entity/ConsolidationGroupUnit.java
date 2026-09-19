package com.digipals.wms.finance.entity;
import jakarta.persistence.*; import lombok.*; import java.io.Serializable; import java.util.UUID;
@Entity @Table(name="consolidation_group_units") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @IdClass(ConsolidationGroupUnit.Key.class)
public class ConsolidationGroupUnit { @Id @Column(name="group_id") UUID groupId; @Id @Column(name="unit_id") UUID unitId;
 public record Key(UUID groupId,UUID unitId) implements Serializable {} }