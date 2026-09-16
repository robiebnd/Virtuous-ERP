import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function GrIrReconciliation() {
  const rows = [
    { PO: "PO-000087", Supplier: "Supplier A", GRN: "GRN-000063", Received: "10 EA", Invoiced: "10 EA", Variance: "USD 0.00", Status: "MATCHED" },
    { PO: "PO-000086", Supplier: "Supplier B", GRN: "GRN-000062", Received: "20 EA", Invoiced: "20 EA", Variance: "USD 120.00", Status: "EXCEPTION" },
    { PO: "PO-000084", Supplier: "Supplier C", GRN: "GRN-000060", Received: "50 EA", Invoiced: "50 EA", Variance: "USD 0.00", Status: "CLEARED" },
  ];
  return <ModuleWorkspace eyebrow="PROCUREMENT / FINANCIAL CONTROL" title="GR/IR Reconciliation" description="Reconcile goods receipt and invoice balances after 3-way matching and surface quantity or value exceptions for resolution." stats={[{label:"Open GR/IR Items",value:"12",foot:"Require reconciliation"},{label:"Matched",value:"84",foot:"No variance",tone:"success"},{label:"Exceptions",value:"3",foot:"Quantity or price variance",tone:"warning"},{label:"Cleared This Period",value:"76",foot:"Reconciled items"}]} process={[{label:"Goods Receipt",detail:"Receipt posted",status:"Posted",tone:"ready"},{label:"Invoice",detail:"Invoice posted",status:"Posted",tone:"ready"},{label:"Compare",detail:"Quantity & value",status:"Review",tone:"pending"},{label:"Resolve",detail:"Variance action",status:"3 open",tone:"pending"},{label:"Clear",detail:"Close GR/IR",status:"Pending",tone:"draft"}]} columns={["PO","Supplier","GRN","Received","Invoiced","Variance","Status"]} rows={rows} searchPlaceholder="Search PO, GRN or supplier..." />;
}
