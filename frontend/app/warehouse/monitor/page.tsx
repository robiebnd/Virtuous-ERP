"use client";

import { useEffect, useMemo, useState } from "react";
import { financeApi, masterDataApi, orderToCashApi, procurementApi } from "@/lib/api";

type Section = "Outbound" | "Inbound" | "Physical Inventory" | "Documents" | "Stock and Bin" | "Resource Management" | "Product Master Data" | "Alert" | "Tools";
const sections: Section[] = ["Outbound","Inbound","Physical Inventory","Documents","Stock and Bin","Resource Management","Product Master Data","Alert","Tools"];

export default function WarehouseMonitor() {
  const [selected, setSelected] = useState<Section>("Outbound");
  const [query, setQuery] = useState("");
  const [expanded, setExpanded] = useState(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [data, setData] = useState({ deliveries: [] as any[], salesOrders: [] as any[], receipts: [] as any[], purchaseOrders: [] as any[], inventory: [] as any[], warehouses: [] as any[], documents: [] as any[] });

  const load = async () => {
    setLoading(true); setError("");
    const [deliveries, salesOrders, receipts, purchaseOrders, inventory, warehouses, documents] = await Promise.all([
      orderToCashApi.deliveries().catch(() => []),
      orderToCashApi.salesOrders().catch(() => []),
      procurementApi.goodsReceipts().catch(() => []),
      procurementApi.orders().catch(() => []),
      financeApi.inventoryValuation().catch(() => []),
      masterDataApi.warehouses().catch(() => []),
      financeApi.accountingDocuments().catch(() => [])
    ]);
    setData({ deliveries, salesOrders, receipts, purchaseOrders, inventory, warehouses, documents });
    setLoading(false);
  };

  useEffect(() => { load(); }, []);

  const rows = useMemo(() => {
    const d = data.deliveries, r = data.receipts, i = data.inventory, f = data.documents;
    if (selected === "Outbound") return d.map(x => ({ Document: x.deliveryNumber || x.number || "—", "Sales Order": x.salesOrderNumber || x.salesOrder?.orderNumber || "—", Customer: x.customerCode || "—", Warehouse: x.shippingPoint || "—", Status: x.status || "—", Date: x.createdAt || "—" }));
    if (selected === "Inbound") return r.map(x => ({ Document: x.receiptNumber || x.grNumber || x.number || "—", "Purchase Order": x.purchaseOrderNumber || x.purchaseOrder?.orderNumber || "—", Supplier: x.supplierName || x.supplierCode || "—", Status: x.status || "—", Date: x.createdAt || "—" }));
    if (selected === "Physical Inventory") return i.map(x => ({ Warehouse: x.warehouseCode || "—", SKU: x.sku || "—", Product: x.productName || "—", "Qty On Hand": Number(x.quantityOnHand || 0).toFixed(2), "Unit Cost": Number(x.unitCost || 0).toFixed(2), "Inventory Value": Number(x.inventoryValue || 0).toFixed(2) }));
    if (selected === "Documents") return [...r.map(x => ({ Document: x.receiptNumber || x.grNumber || x.number || "—", Type: "Goods Receipt", Status: x.status || "—", Date: x.createdAt || "—" })), ...f.map(x => ({ Document: x.documentNumber || "—", Type: x.documentType || "FI", Status: x.status || "—", Date: x.postingDate || "—" }))];
    if (selected === "Stock and Bin") return i.map(x => ({ Warehouse: x.warehouseCode || "—", SKU: x.sku || "—", Product: x.productName || "—", "Qty On Hand": Number(x.quantityOnHand || 0).toFixed(2), Value: Number(x.inventoryValue || 0).toFixed(2) }));
    if (selected === "Product Master Data") return i.map(x => ({ SKU: x.sku || "—", Product: x.productName || "—", "Standard Cost": Number(x.unitCost || 0).toFixed(2), "On Hand": Number(x.quantityOnHand || 0).toFixed(2) }));
    if (selected === "Alert") return [
      ...d.filter(x => ["BLOCKED","OVERDUE","EXCEPTION"].includes(String(x.status).toUpperCase())).map(x => ({ Type:"Outbound", Reference:x.deliveryNumber || x.number, Status:x.status })),
      ...r.filter(x => ["BLOCKED","EXCEPTION"].includes(String(x.status).toUpperCase())).map(x => ({ Type:"Inbound", Reference:x.receiptNumber || x.number, Status:x.status }))
    ];
    if (selected === "Resource Management") return data.warehouses.map(x => ({ Resource:"WAREHOUSE", Code:x.code || "—", Name:x.name || "—", Status:x.active === false ? "INACTIVE" : "ACTIVE" }));
    return [
      { Tool: "Refresh Monitor", Action: "Reload live warehouse data", Status: loading ? "RUNNING" : "READY" },
      { Tool: "Warehouse Selector", Action: "Use Stock & Bins and Finance valuation for warehouse-level views", Status: "READY" }
    ];
  }, [selected, data, loading]);

  const filtered = rows.filter(row => Object.values(row).join(" ").toLowerCase().includes(query.toLowerCase()));
  const columns = rows.length ? Object.keys(rows[0]) : [];

  return <main className="content">
    <div className="page-head">
      <div><div className="eyebrow">WAREHOUSE / EXECUTION</div><h1>Warehouse Management Monitor</h1><p>Live monitor for inbound, outbound, stock, inventory documents and warehouse execution.</p></div>
      <div className="actions"><button className="btn" onClick={load}>Refresh</button><button className="btn" onClick={() => { setExpanded(!expanded); if (!expanded) setSelected("Outbound"); }}> {expanded ? "Collapse All" : "Expand All"} </button></div>
    </div>
    {error && <div className="alert error">{error}</div>}
    <section className="card warehouse-layout">
      <div className="tree">
        {sections.map(n => <button key={n} type="button" className={`tree-item ${selected === n ? "selected" : ""}`} onClick={() => setSelected(n)}><span>▸</span><span>{n}</span></button>)}
      </div>
      <div className="monitor">
        <div className="monitor-head"><div><h2>{selected}</h2><span className="section-meta">{filtered.length} records</span></div><div className="actions"><input className="filter" value={query} onChange={e => setQuery(e.target.value)} placeholder="Search" /><button className="icon-btn" onClick={() => setQuery("")}>⌕</button></div></div>
        {loading ? <div className="empty">Loading warehouse data…</div> : <div className="table-wrap"><table className="table"><thead><tr>{columns.map(c => <th key={c}>{c}</th>)}</tr></thead><tbody>{filtered.map((row,index)=><tr key={index}>{columns.map(c=><td key={c}>{String(row[c] ?? "—")}</td>)}</tr>)}{!filtered.length && <tr><td colSpan={Math.max(columns.length,1)} className="empty">No records available for this section.</td></tr>}</tbody></table></div>}
      </div>
    </section>
  </main>;
}