package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_notification_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class QualityNotificationTask extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="notification_id") private QualityNotification notification;
 @Column(nullable=false) private String task;
 private String owner;
 @Column(nullable=false) private String status;
 private String completionNotes;
}