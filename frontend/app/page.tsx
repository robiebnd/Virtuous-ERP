import Link from "next/link";

const activities = [
  ["PR-000124", "Purchase Requisition", "Pending Approval"],
  ["PO-000087", "Purchase Order", "Ready for Receipt"],
  ["GRN-000063", "Goods Receipt", "Approved"],
  ["VINV-000019", "Vendor Invoice", "Matched"],
];

const statusClass = (status: string) => {
  const value = status.toLowerCase();
  if (value.includes("approved")) return "approved";
  if (value.includes("matched")) return "ready";
  if (value.includes("pending")) return "pending";
  return "draft";
};

export default function Dashboard() {
  return (
    <div className="content">
      <div className="page-head">
        <div>
          <div className="eyebrow">WORKSPACE / OVERVIEW</div>
          <h1>Warehouse &amp; Procurement</h1>
          <p>Operational overview of purchasing, inbound logistics and inventory.</p>
        </div>
        <div className="actions">
          <Link className="btn primary" href="/procurement/purchase-requisitions/new">Create Purchase Requisition</Link>
        </div>
      </div>

      <div className="grid stats">
        <div className="card stat"><div className="stat-label">Open Purchase Requisitions</div><div className="stat-value">24</div><div className="stat-foot">8 awaiting approval</div></div>
        <div className="card stat"><div className="stat-label">Purchase Orders</div><div className="stat-value">38</div><div className="stat-foot">12 ready for delivery</div></div>
        <div className="card stat"><div className="stat-label">Goods Receipts</div><div className="stat-value">17</div><div className="stat-foot">5 pending approval</div></div>
        <div className="card stat"><div className="stat-label">Invoices to Verify</div><div className="stat-value">9</div><div className="stat-foot">3 blocked by variance</div></div>
      </div>

      <div className="grid dashboard-grid">
        <section className="card">
          <div className="section-title process-title"><span>Procurement Process Monitor</span><span className="section-meta">Current Status</span></div>
          {activities.map(([number, type, status]) => (
            <div className="list-row process-row" key={number}>
              <div className="process-document">
                <div className="number">{number}</div>
                <div className="muted">{type}</div>
              </div>
              <div className="process-status"><span className={`status ${statusClass(status)}`}>{status}</span></div>
            </div>
          ))}
        </section>

        <section className="card">
          <div className="section-title">Quick Navigation</div>
          <div className="list-row"><Link className="link" href="/procurement/purchase-requisitions">Purchase Requisitions</Link><span className="chevron">›</span></div>
          <div className="list-row"><Link className="link" href="/procurement/purchase-orders">Purchase Orders</Link><span className="chevron">›</span></div>
          <div className="list-row"><Link className="link" href="/warehouse/monitor">Warehouse Monitor</Link><span className="chevron">›</span></div>
          <div className="list-row"><Link className="link" href="/procurement/vendor-invoices">3-Way Match</Link><span className="chevron">›</span></div>
        </section>
      </div>
    </div>
  );
}
