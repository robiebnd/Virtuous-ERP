package com.digipals.wms.manufacturing.service;
import com.digipals.wms.manufacturing.entity.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public interface ManufacturingService {
 MasterRecipe createRecipe(MasterRecipe r); Bom createBom(Bom b); Routing createRouting(Routing r);
 ProductionOrder createProductionOrder(UUID productId,String plant,String type,BigDecimal qty,LocalDate start,LocalDate finish);
 ProductionOrder releaseProductionOrder(UUID id);
 ProductionConfirmation confirmProduction(UUID id,BigDecimal qty,BigDecimal scrap,String operation,String remarks);
 void issueComponents(UUID orderId, UUID binId, BigDecimal quantityMultiplier);
 ProductionConfirmation receiveFinishedGoods(UUID orderId, UUID binId, BigDecimal quantity);
 ProductionOrder closeProductionOrder(UUID id);
 CapacityRecord saveCapacity(CapacityRecord r); CapacityRecord scheduleProductionOrder(UUID orderId,LocalDate date);
 MrpPlan runMrp(String plant,String strategy,int demand,int planned);
 MrpPlan runMrp(UUID productId,String plant,String strategy,BigDecimal grossDemand,BigDecimal currentStock,BigDecimal safetyStock,BigDecimal lotSize);
 KanbanSignal signalKanban(UUID cycleId,String trigger);
}