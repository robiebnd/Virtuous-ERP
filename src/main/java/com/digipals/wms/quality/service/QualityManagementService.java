package com.digipals.wms.quality.service;
import com.digipals.wms.quality.entity.*;
import java.math.BigDecimal;
import java.util.*;
public interface QualityManagementService {
 MasterInspectionCharacteristic createCharacteristic(MasterInspectionCharacteristic c);
 InspectionPlan createPlan(InspectionPlan p,List<UUID> characteristicIds);
 InspectionLot createLot(UUID productId,String plant,String inspectionType,String sourceType,UUID sourceId,BigDecimal quantity);
 InspectionResult recordResult(UUID lotId,UUID characteristicId,BigDecimal measured,String qualitative);
 UsageDecision decide(UUID lotId,String decision,String stockAction,String remarks);
 QualityNotification createNotification(QualityNotification n);
 List<InspectionLot> openLots();
}