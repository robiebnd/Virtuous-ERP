package com.digipals.wms.manufacturing.service;
import com.digipals.wms.manufacturing.entity.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;
public interface ManufacturingService {
 MasterRecipe createRecipe(MasterRecipe recipe);
 Bom createBom(Bom bom);
 Routing createRouting(Routing routing);
 ProductionOrder createProductionOrder(UUID productId,String plant,String orderType,BigDecimal quantity,LocalDate start,LocalDate finish);
 ProductionOrder releaseProductionOrder(UUID id);
 ProductionConfirmation confirmProduction(UUID id,BigDecimal quantity,BigDecimal scrap,String operation,String remarks);
 CapacityRecord saveCapacity(CapacityRecord record);
 MrpPlan runMrp(String plant,String strategyGroup,int demandCount,int plannedOrderCount);
 KanbanSignal signalKanban(UUID cycleId,String triggerSource);
}