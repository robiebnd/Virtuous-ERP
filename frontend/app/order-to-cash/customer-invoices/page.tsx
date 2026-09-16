import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function CustomerInvoices() {
  const rows = [
    { Document: "CINV-000184", Customer: "Farmgate Foods", SalesOrder: "SO-000238", Amount: "USD 5,680.00", Status: "POSTED", Due: "17 Oct 2026" },
    { Document: "CINV-000183", Customer: "Sunrise Poultry", SalesOrder: "SO-000235", Amount: "USD 14,210.00", Status: "PAID", Due: "10 Sep 2026" },
    { Document: "CINV-000181", Customer: "Mbare Retail", SalesOrder: "SO-000230", Amount: "USD 8,340.00", Status: "OVERDUE", Due: "05 Sep 2026" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / BILLING" title="Customer Invoices" description="Generate customer billing from completed deliveries, manage invoice status and hand off open items to accounts receivable." createHref="/order-to-cash/customer-invoices/new" createLabel="Create Invoice" stats={[{label:"Open Invoices",value:"22",foot:"USD 64,820 outstanding"},{label:"Posted Today",value:"8",foot:"USD 21,440 billed"},{label:"Overdue",value:"5",foot:"Requires collection",tone:"warning"},{label:"Paid",value:"47",foot:"Current period",tone:"success"}]} process={[{label:"Delivery",detail:"Completed fulfilment",status:"Complete",tone:"approved"},{label:"Billing",detail:"Create invoice",status:"Complete",tone:"approved"},{label:"Post",detail:"Customer open item",status:"Posted",tone:"ready"},{label:"Collect",detail:"Receive payment",status:"Open",tone:"pending"},{label:"Clear",detail:"Close receivable",status:"Pending",tone:"draft"}]} columns={["Document","Customer","SalesOrder","Amount","Status","Due"]} rows={rows} searchPlaceholder="Search invoice, customer or sales order..." />;
}