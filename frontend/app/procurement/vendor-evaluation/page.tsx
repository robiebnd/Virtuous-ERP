import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function VendorEvaluation() {
  const rows = [
    { Supplier: "Supplier A", Orders: "18", Delivery: "96%", Quality: "98%", Price: "92%", Service: "95%", Status: "COMPLETE" },
    { Supplier: "Supplier B", Orders: "11", Delivery: "82%", Quality: "94%", Price: "88%", Service: "90%", Status: "REVIEW" },
    { Supplier: "Supplier C", Orders: "24", Delivery: "99%", Quality: "97%", Price: "95%", Service: "96%", Status: "COMPLETE" },
  ];
  return <ModuleWorkspace eyebrow="PROCUREMENT / SUPPLIER PERFORMANCE" title="Vendor Evaluation" description="Review supplier performance after procurement cycles using delivery, quality, price and service measures." stats={[{label:"Suppliers Evaluated",value:"38",foot:"Current period",tone:"success"},{label:"Awaiting Review",value:"6",foot:"Evaluation queue",tone:"warning"},{label:"Delivery KPI",value:"94%",foot:"On-time delivery"},{label:"Quality KPI",value:"97%",foot:"Accepted receipts"}]} process={[{label:"Purchasing",detail:"PO performance",status:"Captured",tone:"approved"},{label:"Delivery",detail:"On-time KPI",status:"Captured",tone:"approved"},{label:"Quality",detail:"Receipt quality",status:"Captured",tone:"approved"},{label:"Price",detail:"Cost performance",status:"Captured",tone:"ready"},{label:"Review",detail:"Supplier evaluation",status:"6 pending",tone:"pending"}]} columns={["Supplier","Orders","Delivery","Quality","Price","Service","Status"]} rows={rows} searchPlaceholder="Search supplier..." />;
}