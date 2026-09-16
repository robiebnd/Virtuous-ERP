import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function Deliveries() {
  const rows = [
    { Document: "DEL-000318", SalesOrder: "SO-000241", Customer: "Farmgate Foods", Warehouse: "MAIN", Status: "READY", Due: "18 Sep 2026" },
    { Document: "DEL-000317", SalesOrder: "SO-000240", Customer: "Mbare Retail", Warehouse: "MAIN", Status: "PICKING", Due: "19 Sep 2026" },
    { Document: "DEL-000315", SalesOrder: "SO-000238", Customer: "AgriGrow Zimbabwe", Warehouse: "S001", Status: "DISPATCHED", Due: "17 Sep 2026" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / LOGISTICS" title="Deliveries" description="Execute outbound fulfilment from approved sales orders through picking, goods issue, dispatch and proof of delivery." stats={[{label:"Open Deliveries",value:"14",foot:"4 awaiting picking"},{label:"Picking",value:"6",foot:"Warehouse work queue"},{label:"Dispatched",value:"18",foot:"Current period"},{label:"Exceptions",value:"2",foot:"Short or delayed",tone:"warning"}]} process={[{label:"Sales Order",detail:"Released order",status:"Released",tone:"ready"},{label:"Picking",detail:"Allocate stock",status:"In progress",tone:"pending"},{label:"Goods Issue",detail:"Reduce inventory",status:"Pending",tone:"draft"},{label:"Dispatch",detail:"Carrier / route",status:"Pending",tone:"draft"},{label:"POD",detail:"Customer receipt",status:"Pending",tone:"draft"}]} columns={["Document","SalesOrder","Customer","Warehouse","Status","Due"]} rows={rows} searchPlaceholder="Search delivery, sales order or customer..." />;
}