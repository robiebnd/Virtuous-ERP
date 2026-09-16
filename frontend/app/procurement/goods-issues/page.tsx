import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function GoodsIssues() {
  const rows = [
    { Document: "GI-000142", Reference: "GRN-000063", Movement: "201", Destination: "Feed Production", Quantity: "10 EA", Status: "POSTED", Date: "16 Sep 2026" },
    { Document: "GI-000141", Reference: "GRN-000061", Movement: "261", Destination: "Production Order", Quantity: "24 EA", Status: "POSTED", Date: "15 Sep 2026" },
    { Document: "GI-000140", Reference: "GRN-000060", Movement: "301", Destination: "S001 Warehouse", Quantity: "50 EA", Status: "READY", Date: "16 Sep 2026" },
  ];
  return <ModuleWorkspace eyebrow="PROCUREMENT / INVENTORY MOVEMENT" title="Goods Issues & Consumption" description="Manage post-receipt stock consumption, production issues and warehouse transfers while preserving the existing goods receipt flow." createHref="/procurement/goods-issues/new" createLabel="Create Goods Issue" stats={[{label:"Open Issues",value:"7",foot:"Awaiting posting"},{label:"Posted Today",value:"18",foot:"Inventory movements"},{label:"Production Issues",value:"11",foot:"Movement 261"},{label:"Exceptions",value:"1",foot:"Requires review",tone:"warning"}]} process={[{label:"Reference",detail:"GR / stock",status:"Selected",tone:"ready"},{label:"Issue",detail:"Destination",status:"Ready",tone:"ready"},{label:"Post",detail:"Stock update",status:"Pending",tone:"draft"},{label:"Document Flow",detail:"Movement history",status:"Pending",tone:"draft"}]} columns={["Document","Reference","Movement","Destination","Quantity","Status","Date"]} rows={rows} searchPlaceholder="Search movement, GRN or destination..." />;
}