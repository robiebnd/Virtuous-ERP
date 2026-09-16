import Link from "next/link";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function OrderToCash() {
  const rows = [
    { Document: "SO-000241", Customer: "Farmgate Foods", Value: "USD 12,450.00", Status: "READY", Next: "Delivery" },
    { Document: "SO-000240", Customer: "Mbare Retail", Value: "USD 8,920.00", Status: "IN_PROGRESS", Next: "Picking" },
    { Document: "SO-000238", Customer: "AgriGrow Zimbabwe", Value: "USD 5,680.00", Status: "INVOICED", Next: "Receivable" },
    { Document: "SO-000235", Customer: "Sunrise Poultry", Value: "USD 14,210.00", Status: "PAID", Next: "Closed" },
  ];
  return <ModuleWorkspace eyebrow="ORDER TO CASH / OVERVIEW" title="Order to Cash" description="Manage the complete customer cycle from sales order through delivery, billing, receivables and payment." createHref="/order-to-cash/sales-orders/new" createLabel="Create Sales Order" stats={[
    { label: "Open Sales Orders", value: "31", foot: "9 ready for delivery" },
    { label: "Deliveries Due", value: "14", foot: "4 require picking", tone: "warning" },
    { label: "Customer Invoices", value: "22", foot: "7 awaiting payment" },
    { label: "Receivables", value: "USD 86,420", foot: "12 overdue", tone: "warning" },
  ]} process={[
    { label: "Sales Order", detail: "Create & approve", status: "Ready", tone: "ready" },
    { label: "Delivery", detail: "Pick & dispatch", status: "In progress", tone: "pending" },
    { label: "Customer Invoice", detail: "Bill customer", status: "Pending", tone: "pending" },
    { label: "Receivable", detail: "Collect payment", status: "Open", tone: "pending" },
    { label: "Closure", detail: "Clear account", status: "Open", tone: "draft" },
  ]} columns={["Document","Customer","Value","Status","Next"]} rows={rows} searchPlaceholder="Search sales order, customer, invoice...">
    <div className="grid dashboard-grid" style={{ marginBottom: 18 }}>
      <section className="card"><div className="section-title">Order to Cash Modules</div>
        {[["Sales Orders","/order-to-cash/sales-orders","Order entry, pricing and approval"],["Deliveries","/order-to-cash/deliveries","Picking, dispatch and proof of delivery"],["Customer Invoices","/order-to-cash/customer-invoices","Billing and invoice posting"],["Accounts Receivable","/order-to-cash/accounts-receivable","Open items, ageing and collection"]].map(([label,href,detail]) => <div className="list-row" key={href}><div><Link className="link" href={href}>{label}</Link><div className="muted">{detail}</div></div><span className="chevron">›</span></div>)}
      </section>
      <section className="card"><div className="section-title">Customer Master Data</div><div className="list-row"><div><Link className="link" href="/order-to-cash/customers">Customers</Link><div className="muted">Customer accounts, addresses and payment terms</div></div><span className="chevron">›</span></div><div className="list-row"><div><strong>Document Flow</strong><div className="muted">Sales Order → Delivery → Invoice → Payment</div></div></div></section>
    </div>
  </ModuleWorkspace>;
}
