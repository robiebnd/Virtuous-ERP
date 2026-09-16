"use client";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function SalesOrders() {
  const rows = [
    { Document: "SO-000241", Customer: "Farmgate Foods", Items: "6", Value: "USD 12,450.00", Status: "READY", Delivery: "18 Sep 2026" },
    { Document: "SO-000240", Customer: "Mbare Retail", Items: "4", Value: "USD 8,920.00", Status: "IN_PROGRESS", Delivery: "19 Sep 2026" },
    { Document: "SO-000238", Customer: "AgriGrow Zimbabwe", Items: "3", Value: "USD 5,680.00", Status: "INVOICED", Delivery: "17 Sep 2026" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / SALES" title="Sales Orders" description="Capture customer demand, apply pricing and payment terms, approve orders and follow the downstream document flow." createHref="/order-to-cash/sales-orders/new" createLabel="Create Sales Order" stats={[{label:"Open Orders",value:"31",foot:"9 ready for delivery"},{label:"Order Value",value:"USD 241K",foot:"Current open orders"},{label:"Blocked",value:"3",foot:"Credit or pricing review",tone:"warning"},{label:"Due Today",value:"7",foot:"Delivery schedule"}]} process={[{label:"Create",detail:"Customer & items",status:"Complete",tone:"approved"},{label:"Price",detail:"Conditions & tax",status:"Complete",tone:"approved"},{label:"Approve",detail:"Order release",status:"Ready",tone:"ready"},{label:"Deliver",detail:"Pick & dispatch",status:"Pending",tone:"pending"},{label:"Bill",detail:"Invoice customer",status:"Pending",tone:"draft"}]} columns={["Document","Customer","Items","Value","Status","Delivery"]} rows={rows} searchPlaceholder="Search sales order or customer..." />;
}