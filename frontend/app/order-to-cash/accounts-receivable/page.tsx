import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function AccountsReceivable() {
  const rows = [
    { Account: "AR-000184", Customer: "Farmgate Foods", Invoice: "CINV-000184", Amount: "USD 5,680.00", Status: "OPEN", Due: "17 Oct 2026" },
    { Account: "AR-000181", Customer: "Mbare Retail", Invoice: "CINV-000181", Amount: "USD 8,340.00", Status: "OVERDUE", Due: "05 Sep 2026" },
    { Account: "AR-000176", Customer: "Sunrise Poultry", Invoice: "CINV-000176", Amount: "USD 4,120.00", Status: "PAID", Due: "01 Sep 2026" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / RECEIVABLES" title="Accounts Receivable" description="Manage customer open items, ageing, collection actions, incoming payments and account clearing." stats={[{label:"Open Receivables",value:"USD 86,420",foot:"31 customer accounts"},{label:"Current",value:"USD 62,140",foot:"Within payment terms"},{label:"Overdue",value:"USD 24,280",foot:"Collection queue",tone:"warning"},{label:"Collected",value:"USD 118K",foot:"Current period",tone:"success"}]} process={[{label:"Invoice",detail:"Posted billing",status:"Posted",tone:"ready"},{label:"Open Item",detail:"Customer balance",status:"Open",tone:"pending"},{label:"Collection",detail:"Follow up",status:"In progress",tone:"pending"},{label:"Payment",detail:"Incoming funds",status:"Pending",tone:"draft"},{label:"Clear",detail:"Match & close",status:"Pending",tone:"draft"}]} columns={["Account","Customer","Invoice","Amount","Status","Due"]} rows={rows} searchPlaceholder="Search customer, invoice or account..." />;
}