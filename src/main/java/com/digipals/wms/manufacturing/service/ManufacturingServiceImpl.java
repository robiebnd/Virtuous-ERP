package com.digipals.wms.manufacturing.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.integration.IntegrationEventTypes;
import com.digipals.wms.integration.outbox.IntegrationOutboxService;
import com.digipals.wms.manufacturing.entity.*;
import com.digipals.wms.manufacturing.repository.*;
import com.digipals.wms.products.Product;
import com.digipals.wms.products.ProductRepository;
import com.digipals.wms.inventory.service.InventoryService;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorybin.repository.InventoryBinRepository;
import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.bin.repository.BinRepository;
import com.digipals.wms.warehouse.repository.WarehouseRepository;
import com.digipals.wms.salesorder.entity.SalesOrder;
import com.digipals.wms.salesorder.entity.SalesOrderItem;
import com.digipals.wms.salesorder.entity.SalesOrderStatus;
import com.digipals.wms.salesorder.repository.SalesOrderRepository;
import com.digipals.wms.quality.repository.InspectionPlanRepository;
import com.digipals.wms.quality.service.QualityManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufacturingServiceImpl implements ManufacturingService {
 private final ProductRepository products;
 private final MasterRecipeRepository recipes;
 private final InspectionPlanRepository inspectionPlans;
 private final QualityManagementService qualityManagementService;
 private final BomRepository boms;
 private final RoutingRepository routings;
 private final ProductionOrderRepository orders;
 private final ProductionConfirmationRepository confirmations;
 private final CapacityRecordRepository capacities;
 private final MrpPlanRepository mrps;
 private final MrpComponentRequirementRepository mrpComponents;
 private final KanbanControlCycleRepository cycles;
 private final KanbanSignalRepository signals;
 private final IntegrationOutboxService outbox;
 private final InventoryService inventoryService;
 private final InventoryBinRepository inventoryBins;
 private final BinRepository binRepository;
 private final WarehouseRepository warehouseRepository;
 private final SalesOrderRepository salesOrders;

 public List<ProductionOrder> listProductionOrders(){return orders.findAll();}
 public List<Bom> listBoms(){return boms.findAll();}
 public MasterRecipe createRecipe(MasterRecipe r){if(r.getProduct()==null||r.getProduct().getId()==null)throw new InvalidWorkflowException("Recipe product is required.");if(r.getRecipeNumber()==null||r.getRecipeNumber().isBlank())r.setRecipeNumber("RCP-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());r.setStatus("ACTIVE");int seq=10;for(RecipeOperation o:r.getOperations()){o.setRecipe(r);if(o.getOperationNo()==null)o.setOperationNo(seq);seq+=10;}return recipes.save(r);}
 public Bom createBom(Bom b){if(b.getProduct()==null||b.getProduct().getId()==null)throw new InvalidWorkflowException("BOM product is required.");if(b.getItems()==null||b.getItems().isEmpty())throw new InvalidWorkflowException("BOM must contain at least one component.");if(b.getBomNumber()==null||b.getBomNumber().isBlank())b.setBomNumber("BOM-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());b.setStatus("ACTIVE");int seq=1;Set<UUID> components=new HashSet<>();for(BomItem i:b.getItems()){if(i.getComponentProduct()==null||i.getComponentProduct().getId()==null)throw new InvalidWorkflowException("BOM component product is required.");if(i.getComponentProduct().getId().equals(b.getProduct().getId()))throw new InvalidWorkflowException("A product cannot be its own BOM component.");if(!components.add(i.getComponentProduct().getId()))throw new InvalidWorkflowException("Duplicate BOM component: "+i.getComponentProduct().getId());i.setBom(b);i.setSequenceNo(i.getSequenceNo()==null?seq:i.getSequenceNo());seq++;if(i.getQuantity()==null||i.getQuantity().signum()<=0)throw new InvalidWorkflowException("BOM component quantity must be greater than zero.");}return boms.save(b);}
 public Routing createRouting(Routing r){if(r.getProduct()==null||r.getProduct().getId()==null)throw new InvalidWorkflowException("Routing product is required.");if(r.getRoutingNumber()==null||r.getRoutingNumber().isBlank())r.setRoutingNumber("RTG-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());r.setStatus("ACTIVE");int seq=10;for(RoutingOperation o:r.getOperations()){o.setRouting(r);if(o.getOperationNo()==null)o.setOperationNo(seq);seq+=10;}return routings.save(r);}

 public ProductionOrder createProductionOrder(UUID productId,String plant,String type,BigDecimal qty,LocalDate start,LocalDate finish){
  Product p=products.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found."));
  if(qty==null||qty.signum()<=0)throw new InvalidWorkflowException("Production quantity must be greater than zero.");
  Bom bom=boms.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(productId,plant,"ACTIVE").orElse(null);
  Routing routing=routings.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(productId,plant,"ACTIVE").orElse(null);
  ProductionOrder o=orders.save(ProductionOrder.builder().orderNumber("MO-"+UUID.randomUUID().toString().substring(0,10).toUpperCase()).product(p).bom(bom).routing(routing).plantCode(plant).plannedQuantity(qty).confirmedQuantity(BigDecimal.ZERO).receivedQuantity(BigDecimal.ZERO).orderType(type==null?"DISCRETE":type.toUpperCase()).status("CREATED").plannedStartDate(start).plannedFinishDate(finish).build());
  if(bom!=null)for(BomItem i:bom.getItems())o.getMaterials().add(ProductionOrderMaterial.builder().productionOrder(o).componentProduct(i.getComponentProduct()).requiredQuantity(i.getQuantity().multiply(qty)).issuedQuantity(BigDecimal.ZERO).build());
  return orders.save(o);
 }

 public ProductionOrder releaseProductionOrder(UUID id){ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));if(!"CREATED".equals(o.getStatus()))throw new InvalidWorkflowException("Only created production orders can be released.");if(o.getBom()==null)throw new InvalidWorkflowException("Active BOM is required before release.");o.setStatus("RELEASED");return o;}

 public ProductionConfirmation confirmProduction(UUID id,BigDecimal qty,BigDecimal scrap,String operation,String remarks){
  ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));
  if(!Set.of("RELEASED","IN_PROCESS").contains(o.getStatus()))throw new InvalidWorkflowException("Production order must be released before confirmation.");
  if(qty==null||qty.signum()<=0)throw new InvalidWorkflowException("Confirmation quantity must be greater than zero.");
  if(scrap==null||scrap.signum()<0)throw new InvalidWorkflowException("Scrap quantity cannot be negative.");
  BigDecimal total=o.getConfirmedQuantity().add(qty);
  if(total.compareTo(o.getPlannedQuantity())>0)throw new InvalidWorkflowException("Confirmed quantity cannot exceed planned quantity.");
  ProductionConfirmation c=confirmations.save(ProductionConfirmation.builder().productionOrder(o).quantity(qty).scrapQuantity(scrap).confirmationTime(LocalDateTime.now()).operation(operation).remarks(remarks).build());
  o.setConfirmedQuantity(total);o.setStatus(total.compareTo(o.getPlannedQuantity())==0?"CONFIRMED":"IN_PROCESS");orders.save(o);
  if(inspectionPlans.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(o.getProduct().getId(),o.getPlantCode(),"ACTIVE").isPresent())qualityManagementService.createLot(o.getProduct().getId(),o.getPlantCode(),"03","PRODUCTION_ORDER",o.getId(),qty);
  outbox.enqueue(IntegrationEventTypes.MANUFACTURING_PRODUCTION_CONFIRMED,"PRODUCTION_ORDER",o.getId(),new com.digipals.wms.integration.manufacturing.ProductionConfirmedEvent(o.getId(),o.getOrderNumber(),o.getProduct().getSku(),qty,scrap,qty.multiply(o.getProduct().getCostPrice()==null?BigDecimal.ZERO:o.getProduct().getCostPrice()),"USD",LocalDateTime.now()),LocalDateTime.now());
  return c;
 }

 public void issueComponents(UUID id,UUID binId,BigDecimal multiplier){
  ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));
  if(!Set.of("RELEASED","IN_PROCESS").contains(o.getStatus()))throw new InvalidWorkflowException("Production order must be released before component issue.");
  if(multiplier==null||multiplier.signum()<=0)throw new InvalidWorkflowException("Quantity multiplier must be greater than zero.");
  Bin bin=binRepository.findById(binId).orElseThrow(()->new ResourceNotFoundException("Component bin not found."));
  var wh=warehouseRepository.findByCode(o.getPlantCode()).orElseThrow(()->new ResourceNotFoundException("Plant warehouse not found."));
  for(ProductionOrderMaterial m:o.getMaterials()){BigDecimal target=m.getRequiredQuantity().multiply(multiplier);BigDecimal remaining=target.subtract(m.getIssuedQuantity());if(remaining.signum()>0){inventoryService.issueStock(wh,bin,m.getComponentProduct(),remaining,o.getOrderNumber(),"PRODUCTION_COMPONENT_ISSUE","Production component issue",null);m.setIssuedQuantity(m.getIssuedQuantity().add(remaining));}}
  o.setStatus("IN_PROCESS");orders.save(o);
 }

 public ProductionConfirmation receiveFinishedGoods(UUID id,UUID binId,BigDecimal qty){
  ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));
  if(!Set.of("IN_PROCESS","CONFIRMED").contains(o.getStatus()))throw new InvalidWorkflowException("Production order is not ready for finished-goods receipt.");
  if(qty==null||qty.signum()<=0)throw new InvalidWorkflowException("Receipt quantity must be greater than zero.");
  BigDecimal remaining=o.getConfirmedQuantity().subtract(o.getReceivedQuantity());
  if(qty.compareTo(remaining)>0)throw new InvalidWorkflowException("Receipt cannot exceed the unreceived confirmed quantity.");
  Bin bin=binRepository.findById(binId).orElseThrow(()->new ResourceNotFoundException("Finished-goods bin not found."));
  var wh=warehouseRepository.findByCode(o.getPlantCode()).orElseThrow(()->new ResourceNotFoundException("Plant warehouse not found."));
  boolean inspect=inspectionPlans.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(o.getProduct().getId(),o.getPlantCode(),"ACTIVE").isPresent();
  if(inspect)inventoryService.receiveQualityStock(wh,bin,o.getProduct(),qty,o.getOrderNumber(),"PRODUCTION_RECEIPT","Finished goods awaiting QM",null);else inventoryService.receiveStock(wh,bin,o.getProduct(),qty,o.getOrderNumber(),"PRODUCTION_RECEIPT","Finished goods receipt",null);
  o.setReceivedQuantity(o.getReceivedQuantity().add(qty));orders.save(o);
  return confirmations.save(ProductionConfirmation.builder().productionOrder(o).quantity(qty).scrapQuantity(BigDecimal.ZERO).confirmationTime(LocalDateTime.now()).operation("FG_RECEIPT").remarks(inspect?"Received to quality stock":"Received to unrestricted stock").build());
 }

 public ProductionOrder closeProductionOrder(UUID id){ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));if(!"CONFIRMED".equals(o.getStatus()))throw new InvalidWorkflowException("Only fully confirmed orders can be closed.");if(o.getMaterials().stream().anyMatch(m->m.getIssuedQuantity().compareTo(m.getRequiredQuantity())<0))throw new InvalidWorkflowException("All required components must be issued before closure.");if(o.getReceivedQuantity().compareTo(o.getConfirmedQuantity())!=0)throw new InvalidWorkflowException("All confirmed finished goods must be received before closure.");o.setStatus("CLOSED");return orders.save(o);}

 public MrpPlan runMrp(UUID productId,String plant,String strategy,BigDecimal grossDemand,BigDecimal currentStock,BigDecimal safetyStock,BigDecimal lotSize){
  Product p=products.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found."));
  if(plant==null||plant.isBlank())throw new InvalidWorkflowException("MRP plant is required.");
  if(grossDemand==null)grossDemand=calculateOpenSalesDemand(p);
  if(currentStock==null)currentStock=calculateAvailablePlantStock(p,plant);
  if(safetyStock==null)safetyStock=BigDecimal.ZERO;
  if(lotSize==null)lotSize=BigDecimal.ONE;
  validateMrpQuantity(grossDemand,"Gross demand");validateMrpQuantity(currentStock,"Current stock");validateMrpQuantity(safetyStock,"Safety stock");if(lotSize.signum()<=0)throw new InvalidWorkflowException("Lot size must be greater than zero.");
  BigDecimal openSupply=calculateOpenProductionSupply(p,plant);
  BigDecimal net=grossDemand.add(safetyStock).subtract(currentStock).subtract(openSupply).max(BigDecimal.ZERO);
  BigDecimal planned=lotRound(net,lotSize);
  ProductionOrder generated=planned.signum()>0?createProductionOrder(productId,plant,"MRP",planned,LocalDate.now(),LocalDate.now()):null;
  MrpPlan plan=mrps.save(MrpPlan.builder().plantCode(plant).strategyGroup(strategy).runTime(LocalDateTime.now()).status("COMPLETED").demandCount(grossDemand.intValue()).plannedOrderCount(planned.signum()>0?1:0).product(p).grossRequirement(grossDemand).availableStock(currentStock).safetyStock(safetyStock).netRequirement(net).plannedOrderQuantity(planned).generatedProductionOrderId(generated==null?null:generated.getId()).build());
  if(planned.signum()>0 && plan.getId()!=null)explodeBomRequirements(plan,p,planned,plant,lotSize,new HashSet<>(),1);
  return plan;
 }

 private void explodeBomRequirements(MrpPlan plan,Product parent,BigDecimal parentPlanned,String plant,BigDecimal lotSize,Set<UUID> path,int level){
  if(!path.add(parent.getId()))throw new InvalidWorkflowException("Circular BOM detected at product "+parent.getSku()+".");
  Bom bom=boms.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(parent.getId(),plant,"ACTIVE").orElse(null);
  if(bom==null){path.remove(parent.getId());return;}
  for(BomItem item:bom.getItems()){
   Product component=item.getComponentProduct();
   BigDecimal gross=item.getQuantity().multiply(parentPlanned);
   BigDecimal stock=calculateAvailablePlantStock(component,plant);
   BigDecimal supply=calculateOpenProductionSupply(component,plant);
   BigDecimal net=gross.subtract(stock).subtract(supply).max(BigDecimal.ZERO);
   BigDecimal planned=lotRound(net,lotSize);
   ProductionOrder generated=null;
   if(planned.signum()>0 && boms.findFirstByProductIdAndPlantCodeAndStatusOrderByCreatedAtDesc(component.getId(),plant,"ACTIVE").isPresent())
       generated=createProductionOrder(component.getId(),plant,"MRP_DEPENDENT",planned,LocalDate.now(),LocalDate.now());
   mrpComponents.save(MrpComponentRequirement.builder().mrpPlan(plan).componentProduct(component).parentProductId(parent.getId()).grossRequirement(gross).availableStock(stock).openSupply(supply).netRequirement(net).plannedOrderQuantity(planned).generatedProductionOrderId(generated==null?null:generated.getId()).bomLevel(level).build());
   if(planned.signum()>0 && generated!=null)explodeBomRequirements(plan,component,planned,plant,lotSize,path,level+1);
  }
  path.remove(parent.getId());
 }

 private BigDecimal calculateOpenSalesDemand(Product p){
  return salesOrders.findByStatus(SalesOrderStatus.CREATED).stream().flatMap(o->o.getItems().stream()).filter(i->p.getSku().equalsIgnoreCase(i.getMaterialCode())).map(SalesOrderItem::getQuantity).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
 }
 private BigDecimal calculateAvailablePlantStock(Product p,String plant){
  var wh=warehouseRepository.findByCode(plant).orElseThrow(()->new ResourceNotFoundException("Plant warehouse not found: "+plant));
  return inventoryBins.findByWarehouseId(wh.getId()).stream().filter(i->i.getProduct()!=null&&p.getId().equals(i.getProduct().getId())).map(this::availableUnrestricted).reduce(BigDecimal.ZERO,BigDecimal::add);
 }
 private BigDecimal availableUnrestricted(InventoryBin i){BigDecimal on=i.getQuantityOnHand()==null?BigDecimal.ZERO:i.getQuantityOnHand();BigDecimal reserved=i.getQuantityReserved()==null?BigDecimal.ZERO:i.getQuantityReserved();return on.subtract(reserved).max(BigDecimal.ZERO);}
 private BigDecimal calculateOpenProductionSupply(Product p,String plant){
  return orders.findByProductIdAndPlantCodeAndStatusIn(p.getId(),plant,List.of("CREATED","RELEASED","IN_PROCESS")).stream().map(o->o.getPlannedQuantity().subtract(o.getConfirmedQuantity()).max(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add);
 }
 private BigDecimal lotRound(BigDecimal value,BigDecimal lotSize){return value.signum()==0?BigDecimal.ZERO:value.divide(lotSize,0,RoundingMode.CEILING).multiply(lotSize);}
 private void validateMrpQuantity(BigDecimal v,String name){if(v.signum()<0)throw new InvalidWorkflowException(name+" cannot be negative.");}

 public CapacityRecord scheduleProductionOrder(UUID id,LocalDate date){ProductionOrder o=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Production order not found."));if(o.getRouting()==null)throw new InvalidWorkflowException("Routing is required for capacity scheduling.");BigDecimal minutes=BigDecimal.ZERO;for(RoutingOperation op:o.getRouting().getOperations()){BigDecimal setup=op.getSetupMinutes()==null?BigDecimal.ZERO:op.getSetupMinutes();BigDecimal run=op.getRunMinutes()==null?BigDecimal.ZERO:op.getRunMinutes().multiply(o.getPlannedQuantity());minutes=minutes.add(setup).add(run);}String wc=o.getRouting().getOperations().stream().map(RoutingOperation::getWorkCenter).filter(Objects::nonNull).findFirst().orElse("DEFAULT");CapacityRecord r=capacities.findByWorkCenterAndCapacityDate(wc,date).orElseGet(()->CapacityRecord.builder().workCenter(wc).capacityDate(date).availableMinutes(BigDecimal.ZERO).plannedMinutes(BigDecimal.ZERO).build());if(r.getAvailableMinutes().compareTo(r.getPlannedMinutes().add(minutes))<0)throw new InvalidWorkflowException("Insufficient capacity for "+wc+" on "+date);r.setPlannedMinutes(r.getPlannedMinutes().add(minutes));capacities.save(r);o.setPlannedStartDate(date);o.setStatus("RELEASED");orders.save(o);return r;}
 public CapacityRecord saveCapacity(CapacityRecord r){if(r.getAvailableMinutes()==null||r.getAvailableMinutes().signum()<0||r.getPlannedMinutes()==null||r.getPlannedMinutes().signum()<0)throw new InvalidWorkflowException("Capacity values cannot be negative.");return capacities.save(r);}
 public MrpPlan runMrp(String plant,String strategy,int demand,int planned){if(demand<0||planned<0)throw new InvalidWorkflowException("MRP counts cannot be negative.");return mrps.save(MrpPlan.builder().plantCode(plant).strategyGroup(strategy).runTime(LocalDateTime.now()).status("COMPLETED").demandCount(demand).plannedOrderCount(planned).build());}
 public KanbanSignal signalKanban(UUID cycleId,String trigger){
  KanbanControlCycle c=cycles.findById(cycleId).orElseThrow(()->new ResourceNotFoundException("Kanban control cycle not found."));
  if(c.getContainers()==null||c.getContainers()<=0||c.getContainerQuantity()==null||c.getContainerQuantity().signum()<=0)throw new InvalidWorkflowException("Kanban container configuration is invalid.");
  BigDecimal replenishment=c.getContainerQuantity().multiply(BigDecimal.valueOf(c.getContainers()));
  ProductionOrder generated=null;
  if("PRODUCTION".equalsIgnoreCase(c.getReplenishmentStrategy())||"INTERNAL_PRODUCTION".equalsIgnoreCase(c.getReplenishmentStrategy()))
      generated=createProductionOrder(c.getProduct().getId(),c.getPlantCode(),"KANBAN",replenishment,LocalDate.now(),LocalDate.now());
  return signals.save(KanbanSignal.builder().controlCycle(c).status(generated==null?"WAITING_REPLENISHMENT":"REPLENISHMENT_CREATED").triggerSource(trigger==null?"MANUAL":trigger).generatedProductionOrderId(generated==null?null:generated.getId()).build());
 }
}