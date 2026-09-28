package com.digipals.wms.manufacturing.controller;
import com.digipals.wms.manufacturing.entity.*;
import com.digipals.wms.manufacturing.service.ManufacturingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/v1/manufacturing") @RequiredArgsConstructor
public class ManufacturingController {
 private final ManufacturingService service;
 @PostMapping("/boms") public Bom bom(@RequestBody Bom b){return service.createBom(b);}
 @PostMapping("/routings") public Routing routing(@RequestBody Routing r){return service.createRouting(r);}
 @PostMapping("/production-orders") public ProductionOrder order(@RequestBody OrderRequest r){return service.createProductionOrder(r.productId(),r.plantCode(),r.orderType(),r.quantity(),r.plannedStartDate(),r.plannedFinishDate());}
 @PostMapping("/production-orders/{id}/release") public ProductionOrder release(@PathVariable UUID id){return service.releaseProductionOrder(id);}
 @PostMapping("/production-orders/{id}/confirm") public ProductionConfirmation confirm(@PathVariable UUID id,@RequestBody ConfirmRequest r){return service.confirmProduction(id,r.quantity(),r.scrapQuantity(),r.operation(),r.remarks());}
 @PostMapping("/capacity") public CapacityRecord capacity(@RequestBody CapacityRecord r){return service.saveCapacity(r);}
 @PostMapping("/mrp/run") public MrpPlan mrp(@RequestBody MrpRequest r){return service.runMrp(r.plantCode(),r.strategyGroup(),r.demandCount(),r.plannedOrderCount());}
 @PostMapping("/kanban/{cycleId}/signal") public KanbanSignal signal(@PathVariable UUID cycleId,@RequestParam(required=false) String triggerSource){return service.signalKanban(cycleId,triggerSource);}
 public record OrderRequest(UUID productId,String plantCode,String orderType,BigDecimal quantity,LocalDate plannedStartDate,LocalDate plannedFinishDate){}
 public record ConfirmRequest(BigDecimal quantity,BigDecimal scrapQuantity,String operation,String remarks){}
 public record MrpRequest(String plantCode,String strategyGroup,int demandCount,int plannedOrderCount){}
}