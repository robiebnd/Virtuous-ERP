import Link from "next/link";

const activities=[
  ["PR-000124","Purchase Requisition","Pending approval"],
  ["PO-000087","Purchase Order","Ready for receipt"],
  ["GRN-000063","Goods Receipt","Approved"],
  ["VINV-000019","Vendor Invoice","Matched"],
];

export default function Dashboard(){return <div className="content">
  <div className="page-head"><div><h1>Warehouse & Procurement</h1><p>Operational overview of purchasing, inbound logistics and inventory.</p></div><div className="actions"><Link className="btn primary" href="/procurement/purchase-requisitions">Create Purchase Requisition</Link></div></div>
  <div className="grid stats">
    <div className="card stat"><div className="stat-label">Open Purchase Requisitions</div><div className="stat-value">24</div><div className="stat-foot">8 awaiting approval</div></div>
    <div className="card stat"><div className="stat-label">Purchase Orders</div><div className="stat-value">38</div><div className="stat-foot">12 ready for delivery</div></div>
    <div className="card stat"><div className="stat-label">Goods Receipts</div><div className="stat-value">17</div><div className="stat-foot">5 pending approval</div></div>
    <div className="card stat"><div className="stat-label">Invoices to Verify</div><div className="stat-value">9</div><div className="stat-foot">3 blocked by variance</div></div>
  </div>
  <div className="grid dashboard-grid">
    <section className="card"><div className="section-title">Procurement Process Monitor</div>{activities.map(a=><div className="list-row" key={a[0]}><div><div className="number">{a[0]}</div><div className="muted">{a[1]}</div></div><span className={`status ${a[2].toLowerCase().includes("approved")?"approved":a[2].toLowerCase().includes("matched")?"ready":a[2].toLowerCase().includes("pending")?"pending":"draft"}`}>{a[2]}</span></div>)}</section>
    <section className="card"><div className="section-title">Quick Navigation</div><div className="list-row"><Link className="link" href="/procurement/purchase-requisitions">Purchase Requisitions</Link><span>→</span></div><div className="list-row"><Link className="link" href="/procurement/purchase-orders">Purchase Orders</Link><span>→</span></div><div className="list-row"><Link className="link" href="/warehouse/monitor">Warehouse Monitor</Link><span>→</span></div><div className="list-row"><Link className="link" href="/procurement/vendor-invoices">3-Way Match</Link><span>→</span></div></section>
  </div>
</div>}
