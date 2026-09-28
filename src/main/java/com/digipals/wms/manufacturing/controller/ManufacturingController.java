package com.digipals.wms.manufacturing.controller;
import com.digipals.wms.manufacturing.entity.*; import com.digipals.wms.manufacturing.service.ManufacturingService; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/v1/manufacturing") @RequiredArgsConstructor
public class ManufacturingController {
 private final ManufacturingService service;
 @PostMapping("/recipes") public MasterRecipe recipe(@RequestBody MasterRecipe r){return service.createRecipe(r);}
 @PostMapping("/boms") public Bom bom(@RequestBody Bom b){return service.createBom(b);}
 @PostMapping("/routings") public Routing routing(@RequestBody Routing r){return service.createRouting(r);}
 @PostMapping("/production-orders") public ProductionOrder order(@RequestBody OrderRequest r){return service.createProductionOrder(r.productId(),r.plantCode(),r.orderType(),r.quantity(),r.plannedStartDate(),r.plannedFinishDate());}
 @PostMapping("/production-orders/{id}/release") public ProductionOrder release(@PathVariable UUID id){return service.releaseProductionOrder(id);}
 @PostMapping("/production-orders/{id}/issue-components") public void issue(@PathVariable UUID id,@RequestBody IssueRequest r){service.issueComponents(id,r.binId(),r.quantityMultiplier());}
 @PostMapping("/production-orders/{id}/confirm") public ProductionConfirmation confirm(@PathVariable UUID id,@RequestBody ConfirmRequest r){return service.confirmProduction(id,r.quantity(),r.scrapQuantity(),r.operation(),r.remarks());}
 @PostMapping("/production-orders/{id}/receipt") public ProductionConfirmation receipt(@PathVariable UUID id,@RequestBody ReceiptRequest r){return service.receiveFinishedGoods(id,r.binId(),r.quantity());}
 @PostMapping("/production-orders/{id}/close") public ProductionOrder close(@PathVariable UUID id){return service.closeProductionOrder(id);}
 @PostMapping("/capacity") public CapacityRecord capacity(@RequestBody CapacityRecord r){return service.saveCapacity(r);}
 @PostMapping("/mrp/run") public MrpPlan mrp(@RequestBody MrpRequest r){return r.productId()==null?service.runMrp(r.plantCode(),r.strategyGroup(),r.demandCount(),r.plannedOrderCount()):service.runMrp(r.productId(),r.plantCode(),r.strategyGroup(),r.grossDemand(),r.currentStock(),r.safetyStock(),r.lotSize());}
 @PostMapping("/kanban/{cycleId}/signal") public KanbanSignal signal(@PathVariable UUID cycleId,@RequestParam(required=false) String triggerSource){return service.signalKanban(cycleId,triggerSource);}
 public record OrderRequest(UUID productId,String plantCode,String orderType,BigDecimal quantity,LocalDate plannedStartDate,LocalDate plannedFinishDate){}
 public record IssueRequest(UUID binId,BigDecimal quantityMultiplier){}
 public record ConfirmRequest(BigDecimal quantity,BigDecimal scrapQuantity,String operation,String remarks){}
 public record ReceiptRequest(UUID binId,BigDecimal quantity){}
 public record MrpRequest(UUID productId,String plantCode,String strategyGroup,int demandCount,int plannedOrderCount,BigDecimal grossDemand,BigDecimal currentStock,BigDecimal safetyStock,BigDecimal lotSize){}
}