package com.digipals.wms.quality.service;
import com.digipals.wms.quality.entity.*; import java.math.BigDecimal; import java.util.*;
public interface QualityManagementService {
 MasterInspectionCharacteristic createCharacteristic(MasterInspectionCharacteristic c);
 InspectionPlan createPlan(InspectionPlan p,List<UUID> ids);
 InspectionLot createLot(UUID productId,String plant,String type,String sourceType,UUID sourceId,BigDecimal quantity);
 InspectionResult recordResult(UUID lotId,UUID characteristicId,BigDecimal measured,String qualitative);
 UsageDecision decide(UUID lotId,String decision,String stockAction,String remarks,UUID binId);
 QualityInfoRecord createQualityInfoRecord(QualityInfoRecord r); QualityInfoRecord getQualityInfoRecord(UUID supplierId,UUID productId);
 QualityCatalogCode createCatalogCode(QualityCatalogCode c); List<QualityCatalogCode> catalog(String type);
 QualityNotification createNotification(QualityNotification n); QualityNotification completeTask(UUID notificationId,UUID taskId,String notes); QualityNotification closeNotification(UUID notificationId);
 List<InspectionLot> openLots();
}