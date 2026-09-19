"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { financeApi, masterDataApi, orderToCashApi } from "@/lib/api";

export default function InventoryAccountingPage() {
  const [valuation, setValuation] = useState<any[]>([]);
  const [reconciliation, setReconciliation] = useState<any | null>(null);
  const [deliveries, setDeliveries] = useState<any[]>([]);
  const [billing, setBilling] = useState<any[]>([]);
  const [warehouses, setWarehouses] = useState<any[]>([]);
  const [warehouseId, setWarehouseId] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    setLoading(true);
    setError("");
    try {
      const [v, r, d, b] = await Promise.all([
        financeApi.inventoryValuation(warehouseId || undefined),
        financeApi.inventoryReconciliation(),
        orderToCashApi.deliveries(),
        orderToCashApi.billingDocuments()
      ]);
      setValuation(v);
      setReconciliation(r);
      setDeliveries(d);
      setBilling(b);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load inventory accounting.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    masterDataApi.warehouses().then(setWarehouses).catch(() => setWarehouses([]));
  }, []);

  useEffect(() => { load(); }, [warehouseId]);

  const inventoryValue = useMemo(() => valuation.reduce((s, x) => s + Number(x.inventoryValue || 0), 0), [valuation]);
  const postedPgi = deliveries.filter(x => ["GOODS_ISSUED", "PGI_POSTED", "COMPLETED"].includes(String(x.status || "").toUpperCase())).length;
  const postedBilling = billing.filter(x => String(x.status || "").toUpperCase() === "POSTED").length;

  return <main className="content">
    <div className="page-head">
      <div>
        <div className="eyebrow">FINANCE / INVENTORY ACCOUNTING</div>
        <h1>Inventory Accounting</h1>
        <p>Monitor inventory valuation and the accounting flow created by goods issue and customer billing.</p>
      </div>
      <div className="actions">
        <Link className="btn" href="/finance">Back to Finance</Link>
        <button className="btn" onClick={load}>Refresh</button>
      </div>
    </div>

    {error && <div className="alert error">{error}</div>}

    <section className="card form-card">
      <div className="section-title">Accounting Context</div>
      <div className="form-grid">
        <div className="form-field"><label>Company Code</label><input className="form-input" value="ZW01" readOnly /></div>
        <div className="form-field"><label>Valuation Warehouse</label><select className="form-input" value={warehouseId} onChange={e => setWarehouseId(e.target.value)}><option value="">All warehouses</option>{warehouses.filter(x => x.active !== false).map(x => <option key={x.id} value={x.id}>{x.code} — {x.name}</option>)}</select></div>
        <div className="form-field"><label>Ledger</label><input className="form-input" value="0L — Leading Ledger" readOnly /></div>
      </div>
    </section>

    <div className="grid stats">
      <div className="card stat"><div className="stat-label">Inventory Value</div><div className="stat-value">{inventoryValue.toFixed(2)}</div><div className="stat-foot">Current valuation</div></div>
      <div className="card stat"><div className="stat-label">Valuation Lines</div><div className="stat-value">{valuation.length}</div><div className="stat-foot">Warehouse/product balances</div></div>
      <div className="card stat"><div className="stat-label">Posted Goods Issues</div><div className="stat-value">{postedPgi}</div><div className="stat-foot">PGI flow records</div></div>
      <div className="card stat"><div className="stat-label">Posted Billing</div><div className="stat-value">{postedBilling}</div><div className="stat-foot">AR and revenue documents</div></div>
    </div>

    {reconciliation && <section className="card" style={{marginBottom:24,padding:18}}>
      <div className="section-title" style={{margin:"-18px -18px 14px"}}>Inventory to GL Reconciliation</div>
      <div className="detail-grid">
        <div className="detail-field"><span>Inventory Valuation</span><strong>{Number(reconciliation.inventoryValuation || 0).toFixed(2)}</strong></div>
        <div className="detail-field"><span>GL 110000</span><strong>{Number(reconciliation.inventoryGlBalance || 0).toFixed(2)}</strong></div>
        <div className="detail-field"><span>Variance</span><strong>{Number(reconciliation.variance || 0).toFixed(2)}</strong></div>
      </div>
      <div className={reconciliation.balanced ? "alert" : "alert error"}>{reconciliation.balanced ? "BALANCED — inventory valuation agrees with GL." : "VARIANCE REQUIRES REVIEW — inventory valuation does not agree with GL."}</div>
    </section>}

    <section className="card" style={{marginBottom:24}}>
      <div className="section-title">Inventory Valuation</div>
      {loading ? <div className="empty">Loading inventory valuation...</div> : valuation.length === 0 ? <div className="empty">No inventory valuation records found.</div> :
      <div className="table-wrap"><table className="table"><thead><tr><th>Warehouse</th><th>SKU</th><th>Product</th><th>Qty On Hand</th><th>Unit Cost</th><th>Inventory Value</th></tr></thead><tbody>{valuation.map((x,i)=><tr key={x.id || i}><td>{x.warehouseCode || "—"}</td><td>{x.sku || "—"}</td><td>{x.productName || "—"}</td><td>{Number(x.quantityOnHand || 0).toFixed(2)}</td><td>{Number(x.unitCost || 0).toFixed(2)}</td><td>{Number(x.inventoryValue || 0).toFixed(2)}</td></tr>)}</tbody></table></div>}
    </section>

    <div className="grid dashboard-grid">
      <section className="card">
        <div className="section-title">Post Goods Issue</div>
        <div className="list-row"><div><b>Outbound delivery execution</b><div className="muted">Picking → packing → PGI → COGS/inventory consumption</div></div><Link className="btn primary" href="/order-to-cash/deliveries">Open Deliveries</Link></div>
      </section>
      <section className="card">
        <div className="section-title">Customer Billing</div>
        <div className="list-row"><div><b>Billing document posting</b><div className="muted">Billing → accounts receivable → revenue</div></div><Link className="btn primary" href="/order-to-cash/customer-invoices">Open Billing</Link></div>
      </section>
    </div>
  </main>;
}
