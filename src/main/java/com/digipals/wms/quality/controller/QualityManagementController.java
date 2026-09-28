package com.digipals.wms.quality.controller;
import com.digipals.wms.quality.entity.*;
import com.digipals.wms.quality.service.QualityManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController @RequestMapping("/api/v1/quality") @RequiredArgsConstructor
public class QualityManagementController {
 private final QualityManagementService service;
 @PostMapping("/characteristics") public MasterInspectionCharacteristic characteristic(@RequestBody MasterInspectionCharacteristic c){return service.createCharacteristic(c);}
 @PostMapping("/plans") public InspectionPlan plan(@RequestBody PlanRequest r){return service.createPlan(r.plan(),r.characteristicIds()==null?List.of():r.characteristicIds());}
 @PostMapping("/lots") public InspectionLot lot(@RequestBody LotRequest r){return service.createLot(r.productId(),r.plantCode(),r.inspectionType(),r.sourceDocumentType(),r.sourceDocumentId(),r.quantity());}
 @PostMapping("/lots/{id}/results") public InspectionResult result(@PathVariable UUID id,@RequestBody ResultRequest r){return service.recordResult(id,r.characteristicId(),r.measuredValue(),r.qualitativeResult());}
 @PostMapping("/lots/{id}/decision") public UsageDecision decision(@PathVariable UUID id,@RequestBody DecisionRequest r){return service.decide(id,r.decisionCode(),r.stockAction(),r.remarks(),r.binId());}
 @PostMapping("/info-records") public QualityInfoRecord infoRecord(@RequestBody QualityInfoRecord r){return service.createQualityInfoRecord(r);}
 @GetMapping("/info-records/{supplierId}/{productId}") public QualityInfoRecord infoRecord(@PathVariable UUID supplierId,@PathVariable UUID productId){return service.getQualityInfoRecord(supplierId,productId);}
 @PostMapping("/catalogs") public QualityCatalogCode catalogCode(@RequestBody QualityCatalogCode c){return service.createCatalogCode(c);}
 @GetMapping("/catalogs/{type}") public List<QualityCatalogCode> catalog(@PathVariable String type){return service.catalog(type);}
 @PostMapping("/notifications") public QualityNotification notification(@RequestBody QualityNotification n){return service.createNotification(n);}
 @PostMapping("/notifications/{id}/tasks/{taskId}/complete") public QualityNotification completeTask(@PathVariable UUID id,@PathVariable UUID taskId,@RequestParam(required=false) String notes){return service.completeTask(id,taskId,notes);}
 @PostMapping("/notifications/{id}/close") public QualityNotification closeNotification(@PathVariable UUID id){return service.closeNotification(id);}
 @GetMapping("/lots/open") public List<InspectionLot> open(){return service.openLots();}
 @GetMapping("/characteristics") public List<MasterInspectionCharacteristic> characteristics(){return service.listCharacteristics();}
 @GetMapping("/plans") public List<InspectionPlan> plans(){return service.listPlans();}
 @GetMapping("/lots") public List<InspectionLot> lots(){return service.listLots();}
 @GetMapping("/notifications") public List<QualityNotification> notifications(){return service.listNotifications();}
 @GetMapping("/info-records") public List<QualityInfoRecord> infoRecords(){return service.listInfoRecords();}
 @GetMapping("/catalogs") public List<QualityCatalogCode> catalogs(){return service.listCatalogCodes();}
 public record PlanRequest(InspectionPlan plan,List<UUID> characteristicIds){}
 public record LotRequest(UUID productId,String plantCode,String inspectionType,String sourceDocumentType,UUID sourceDocumentId,BigDecimal quantity){}
 public record ResultRequest(UUID characteristicId,BigDecimal measuredValue,String qualitativeResult){}
 public record DecisionRequest(String decisionCode,String stockAction,String remarks,UUID binId){}
}