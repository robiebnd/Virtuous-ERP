package com.digipals.wms.finance.entity;
import jakarta.persistence.*; import lombok.*; import java.io.Serializable; import java.util.UUID;
@Embeddable @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ConsolidationGroupUnitId implements Serializable { @Column(name="group_id") UUID groupId; @Column(name="unit_id") UUID unitId; }