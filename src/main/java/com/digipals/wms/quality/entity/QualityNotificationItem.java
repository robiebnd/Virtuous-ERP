package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_notification_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class QualityNotificationItem extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="notification_id") private QualityNotification notification;
 @Column(name="defect_type",nullable=false) private String defectType;
 private String objectPart;
 private String cause;
 private String description;
}