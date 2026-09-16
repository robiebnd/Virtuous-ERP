import Link from "next/link";

const activities = [
  ["PR-000124", "Purchase Requisition", "Pending Approval"],
  ["PO-000087", "Purchase Order", "Ready for Receipt"],
  ["GRN-000063", "Goods Receipt", "Approved"],
  ["VINV-000019", "Vendor Invoice", "Matched"],
  ["SO-000241", "Sales Order", "Ready"],
  ["CINV-000184", "Customer Invoice", "Posted"],
];

const statusClass = (status: string) => {
  const value = status.toLowerCase();
  if (value.includes("approved") || value.includes("matched") || value.includes("posted")) return "approved";
  if (value.includes("pending")) return "pending";
  if (value.includes("ready")) return "ready";
  return "draft";
};

export default function Dashboard() {
  return (
    <div className="content">
      <div className="page-head">
        <div>
          <div className="eyebrow">WORKSPACE / OVERVIEW</div>
          <h1>Warehouse, Procurement &amp; Order to Cash</h1>
          <p>Operational overview across purchasing, inventory, customer fulfilment and financial document flow.</p>
        </div>
        <div className="actions"><Link className="btn primary" href="/procurement/purchase-requisitions/new">Create Purchase Requisition</Link><Link className="btn" href="/order-to-cash/sales-orders/new">Create Sales Order</Link></div>
      </div>

      <div className="grid stats">
        <div className="card stat"><div className="stat-label">Open Purchase Requisitions</div><div className="stat-value">24</div><div className="stat-foot">8 awaiting approval</div></div>
        <div className="card stat"><div className="stat-label">Open Procurement Cycles</div><div className="stat-value">19</div><div className="stat-foot">4 invoice exceptions</div></div>
        <div className="card stat"><div className="stat-label">Open Sales Orders</div><div className="stat-value">31</div><div className="stat-foot">9 ready for delivery</div></div>
        <div className="card stat"><div className="stat-label">Customer Receivables</div><div className="stat-value">USD 86K</div><div className="stat-foot">12 overdue items</div></div>
      </div>

      <div className="grid dashboard-grid">
        <section className="card">
          <div className="section-title process-title"><span>Enterprise Process Monitor</span><span className="section-meta">Current Status</span></div>
          {activities.map(([number, type, status]) => <div className="list-row process-row" key={number}><div className="process-document"><div className="number">{number}</div><div className="muted">{type}</div></div><div className="process-status"><span className={`status ${statusClass(status)}`}>{status}</span></div></div>)}
        </section>

        <section className="card">
          <div className="section-title">Process Workspaces</div>
          <div className="list-row"><div><Link className="link" href="/order-to-cash">Order to Cash</Link><div className="muted">Sales Order → Delivery → Invoice → Receivable → Payment</div></div><span className="chevron">›</span></div>
          <div className="list-row"><div><Link className="link" href="/procurement/cycle-closure">Procurement Cycle Closure</Link><div className="muted">GR → Invoice → Payment → GR/IR → Evaluation → Closure</div></div><span className="chevron">›</span></div>
          <div className="list-row"><div><Link className="link" href="/procurement/goods-issues">Goods Issues &amp; Consumption</Link><div className="muted">Post-GR physical inventory movement</div></div><span className="chevron">›</span></div>
          <div className="list-row"><div><Link className="link" href="/procurement/gr-ir-reconciliation">GR/IR Reconciliation</Link><div className="muted">Resolve quantity and value exceptions</div></div><span className="chevron">›</span></div>
        </section>
      </div>
    </div>
  );
}
