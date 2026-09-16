import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function Customers() {
  const rows = [
    { Customer: "CUST-00041", Name: "Farmgate Foods", Currency: "USD", Terms: "30 Days", Credit: "USD 50,000", Status: "ACTIVE" },
    { Customer: "CUST-00040", Name: "Mbare Retail", Currency: "USD", Terms: "30 Days", Credit: "USD 25,000", Status: "ACTIVE" },
    { Customer: "CUST-00038", Name: "Sunrise Poultry", Currency: "USD", Terms: "COD", Credit: "USD 15,000", Status: "ACTIVE" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / MASTER DATA" title="Customers" description="Maintain customer accounts used by sales orders, deliveries, billing and receivables." createHref="/order-to-cash/customers/new" createLabel="Create Customer" stats={[{label:"Active Customers",value:"128",foot:"Sales accounts"},{label:"Credit Exposure",value:"USD 412K",foot:"Open customer balances"},{label:"On Hold",value:"4",foot:"Credit review",tone:"warning"},{label:"New This Month",value:"9",foot:"Customer onboarding",tone:"success"}]} process={[{label:"Account",detail:"Customer master",status:"Active",tone:"approved"},{label:"Credit",detail:"Limit & terms",status:"Configured",tone:"ready"},{label:"Order",detail:"Sales processing",status:"Available",tone:"ready"}]} columns={["Customer","Name","Currency","Terms","Credit","Status"]} rows={rows} searchPlaceholder="Search customer account or name..." />;
}