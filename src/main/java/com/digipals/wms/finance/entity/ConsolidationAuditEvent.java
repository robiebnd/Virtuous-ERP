package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.time.LocalDateTime;
@Entity @Table(name="consolidation_audit_events") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationAuditEvent extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="group_id",nullable=false) private ConsolidationGroup group;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="run_id") private GroupReportingRun run;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="journal_id") private ConsolidationJournal journal;
 @Column(name="event_type",nullable=false,length=40) private String eventType; @Column(name="event_status",nullable=false,length=20) private String eventStatus;
 @Column(name="event_time",nullable=false) private LocalDateTime eventTime; @Column(nullable=false,length=100) private String actor;
 @Column(name="reference_number",length=80) private String referenceNumber; @Column(columnDefinition="TEXT") private String details;
}