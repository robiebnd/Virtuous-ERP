package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.*;
@Entity @Table(name="qm_notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class QualityNotification extends BaseEntity {
 @Column(name="notification_number",nullable=false,unique=true) private String notificationNumber;
 @Column(name="notification_type",nullable=false) private String notificationType;
 @Column(nullable=false) private String priority;
 @Column(name="short_text",nullable=false) private String shortText;
 @Column(nullable=false) private String status;
 @Column(name="reference_object_type") private String referenceObjectType;
 @Column(name="reference_object_id") private UUID referenceObjectId;
 @OneToMany(mappedBy="notification",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<QualityNotificationItem> items=new ArrayList<>();
 @OneToMany(mappedBy="notification",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<QualityNotificationTask> tasks=new ArrayList<>();
}