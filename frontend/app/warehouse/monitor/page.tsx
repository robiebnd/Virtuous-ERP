"use client";

import { useEffect, useMemo, useState } from "react";
import { financeApi, orderToCashApi, procurementApi, warehouseExecutionApi } from "@/lib/api";

type Section = "Outbound" | "Inbound" | "Physical Inventory" | "Documents" | "Stock and Bin" | "Resource Management" | "Product Master Data" | "Alert" | "Tools";
const sectionGroups = [
  { label: "EXECUTION", items: ["Outbound","Inbound"] as Section[] },
  { label: "INVENTORY", items: ["Physical Inventory","Stock and Bin"] as Section[] },
  { label: "MASTER DATA", items: ["Product Master Data","Resource Management"] as Section[] },
  { label: "CONTROL", items: ["Documents","Alert","Tools"] as Section[] }
];
const sectionIcons: Record<Section,string> = {
  "Outbound":"↗", "Inbound":"↙", "Physical Inventory":"▦", "Documents":"▤",
  "Stock and Bin":"⌗", "Resource Management":"♙", "Product Master Data":"□",
  "Alert":"!", "Tools":"⚙"
};

const value = (x:any, ...keys:string[]) => {
  for (const key of keys) {
    const parts = key.split(".");
    let v=x;
    for(const part of parts) v=v?.[part];
    if(v !== undefined && v !== null && v !== "") return v;
  }
  return "—";
};

export default function WarehouseMonitor() {
  const [selected, setSelected] = useState<Section>("Outbound");
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [data, setData] = useState({ deliveries: [] as any[], salesOrders: [] as any[], receipts: [] as any[], purchaseOrders: [] as any[], inventory: [] as any[], warehouses: [] as any[], documents: [] as any[] });
  const [warehouseId, setWarehouseId] = useState("");
  const [selectedInventoryId, setSelectedInventoryId] = useState("");
  const [quantity, setQuantity] = useState("");
  const [bins, setBins] = useState<any[]>([]);
  const [fromBinId, setFromBinId] = useState("");
  const [toBinId, setToBinId] = useState("");
  const [operationBusy, setOperationBusy] = useState(false);

  const load = async () => {
    setLoading(true); setError("");
    try {
      const [deliveries, salesOrders, receipts, purchaseOrders, inventory, warehouses, documents] = await Promise.all([
        orderToCashApi.deliveries(),
        orderToCashApi.salesOrders(),
        procurementApi.goodsReceipts(),
        procurementApi.orders(),
        warehouseExecutionApi.inventory(),
        warehouseExecutionApi.warehouses(),
        financeApi.accountingDocuments()
      ]);
      setData({ deliveries, salesOrders, receipts, purchaseOrders, inventory, warehouses, documents });
    } catch (e:any) {
      setError(e.message || "Unable to load warehouse execution data.");
    } finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    if (!warehouseId) { setBins([]); return; }
    warehouseExecutionApi.bins(warehouseId).then(setBins).catch(() => setBins([]));
  }, [warehouseId]);

  const run = async (action:()=>Promise<any>, success:string) => {
    setOperationBusy(true); setError(""); setMessage("");
    try { await action(); setMessage(success); await load(); }
    catch(e:any){ setError(e.message || "Warehouse operation failed."); }
    finally { setOperationBusy(false); }
  };

  const inventory = data.inventory;
  const selectedInventory = inventory.find(x => x.id === selectedInventoryId);
  const rows = useMemo(() => {
    const d=data.deliveries, r=data.receipts, i=data.inventory, f=data.documents;
    if(selected==="Outbound") return d.map(x=>({id:x.id,Document:value(x,"deliveryNumber","number"),"Sales Order":value(x,"salesOrderNumber","salesOrder.orderNumber"),Customer:value(x,"customerCode"),Warehouse:value(x,"shippingPoint"),Status:value(x,"status"),Date:value(x,"createdAt")}));
    if(selected==="Inbound") return r.map(x=>({id:x.id,Document:value(x,"receiptNumber","grNumber","number"),"Purchase Order":value(x,"purchaseOrderNumber","purchaseOrder.orderNumber"),Supplier:value(x,"supplierName","supplierCode"),Status:value(x,"status"),Date:value(x,"createdAt")}));
    if(selected==="Physical Inventory") return i.map(x=>({id:x.id,Warehouse:value(x,"warehouse.code","warehouseCode"),Bin:value(x,"bin.code","binCode"),SKU:value(x,"product.sku","sku"),Product:value(x,"product.name","productName"),"Qty On Hand":Number(value(x,"quantityOnHand")||0).toFixed(2),"Reserved":Number(value(x,"quantityReserved")||0).toFixed(2),"Available":(Number(value(x,"quantityOnHand")||0)-Number(value(x,"quantityReserved")||0)).toFixed(2)}));
    if(selected==="Documents") return [...r.map(x=>({id:x.id,Document:value(x,"receiptNumber","grNumber","number"),Type:"Goods Receipt",Status:value(x,"status"),Date:value(x,"createdAt")})),...f.map(x=>({id:x.id,Document:value(x,"documentNumber"),Type:value(x,"documentType","FI"),Status:value(x,"status"),Date:value(x,"postingDate")}))];
    if(selected==="Stock and Bin") return i.map(x=>({id:x.id,Warehouse:value(x,"warehouse.code","warehouseCode"),Bin:value(x,"bin.code","binCode"),SKU:value(x,"product.sku","sku"),Product:value(x,"product.name","productName"),"Qty On Hand":Number(value(x,"quantityOnHand")||0).toFixed(2),"Reserved":Number(value(x,"quantityReserved")||0).toFixed(2),"Available":(Number(value(x,"quantityOnHand")||0)-Number(value(x,"quantityReserved")||0)).toFixed(2)}));
    if(selected==="Product Master Data") return i.map(x=>({id:x.id,SKU:value(x,"product.sku","sku"),Product:value(x,"product.name","productName"),"Standard Cost":Number(value(x,"product.costPrice","unitCost")||0).toFixed(2),"On Hand":Number(value(x,"quantityOnHand")||0).toFixed(2)}));
    if(selected==="Alert") return [...d.filter(x=>["BLOCKED","OVERDUE","EXCEPTION"].includes(String(x.status).toUpperCase())).map(x=>({id:x.id,Type:"Outbound",Reference:value(x,"deliveryNumber","number"),Status:value(x,"status")})),...r.filter(x=>["BLOCKED","EXCEPTION"].includes(String(x.status).toUpperCase())).map(x=>({id:x.id,Type:"Inbound",Reference:value(x,"receiptNumber","number"),Status:value(x,"status")}))];
    if(selected==="Resource Management") return data.warehouses.map(x=>({id:x.id,Resource:"WAREHOUSE",Code:value(x,"code"),Name:value(x,"name"),Status:x.active===false?"INACTIVE":"ACTIVE"}));
    return [{id:"refresh",Tool:"Refresh Monitor",Action:"Reload live warehouse data",Status:loading?"RUNNING":"READY"},{id:"stock",Tool:"Stock Operations",Action:"Select Stock & Bin to adjust, reserve, release or transfer stock",Status:"READY"}];
  },[selected,data,loading]);

  const filtered=rows.filter(row=>Object.values(row).join(" ").toLowerCase().includes(query.toLowerCase()));
  const columns=rows.length?Object.keys(rows[0]).filter(x=>x!=="id"):[];
  const doOutbound=async(id:string,status:string)=>{
    if(status==="CREATED") return run(()=>warehouseExecutionApi.startPicking(id),"Picking started.");
    if(status==="PICKING") return run(()=>warehouseExecutionApi.confirmPicking(id),"Picking confirmed.");
    if(status==="PICKED") return run(()=>warehouseExecutionApi.confirmPacking(id),"Packing confirmed.");
    if(status==="PACKED") return run(()=>warehouseExecutionApi.postGoodsIssue(id),"Goods issue posted.");
  };

  return <main className="content">
    <div className="page-head"><div><div className="eyebrow">WAREHOUSE / EXECUTION</div><h1>Warehouse Management Monitor</h1><p>Execute inbound, outbound, stock, bin and warehouse control processes.</p></div><button className="btn" onClick={load}>Refresh</button></div>
    {error&&<div className="alert error">{error}</div>}{message&&<div className="alert success">{message}</div>}
    <section className="card warehouse-layout">
      <aside className="tree">
  <div className="tree-title">Warehouse Workbench</div>
  <div className="tree-subtitle">Execution &amp; control</div>
  {sectionGroups.map(group => <div className="tree-group" key={group.label}>
    <div className="tree-group-label">{group.label}</div>
    {group.items.map(n => <button key={n} type="button" className={`tree-item ${selected===n?"selected":""}`} onClick={()=>setSelected(n)}>
      <span className="tree-icon">{sectionIcons[n]}</span>
      <span className="tree-label">{n}</span>
      <span className="tree-arrow">›</span>
    </button>)}
  </div>)}
</aside>
      <div className="monitor">
        <div className="monitor-head"><div><h2>{selected}</h2><span className="section-meta">{filtered.length} records</span></div><div className="actions"><input className="filter" value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search"/><button className="icon-btn" onClick={()=>setQuery("")}>⌕</button></div></div>
        {selected==="Stock and Bin"&&<div className="warehouse-operations">
          <select className="form-input" value={warehouseId} onChange={e=>setWarehouseId(e.target.value)}><option value="">Select warehouse</option>{data.warehouses.map(w=><option key={w.id} value={w.id}>{w.code} — {w.name}</option>)}</select>
          <select className="form-input" value={selectedInventoryId} onChange={e=>setSelectedInventoryId(e.target.value)}><option value="">Select stock record</option>{inventory.map(x=><option key={x.id} value={x.id}>{value(x,"warehouse.code","warehouseCode")} / {value(x,"bin.code","binCode")} / {value(x,"product.sku","sku")}</option>)}</select>
          <input className="form-input" type="number" min="0.01" step="0.01" value={quantity} onChange={e=>setQuantity(e.target.value)} placeholder="Quantity"/>
          <button className="btn primary" disabled={!selectedInventoryId||!quantity||operationBusy} onClick={()=>run(()=>warehouseExecutionApi.reserve(selectedInventoryId,Number(quantity)),"Stock reserved.")}>Reserve</button>
          <button className="btn" disabled={!selectedInventoryId||!quantity||operationBusy} onClick={()=>run(()=>warehouseExecutionApi.release(selectedInventoryId,Number(quantity)),"Reservation released.")}>Release</button>
          <button className="btn" disabled={!selectedInventoryId||!quantity||operationBusy} onClick={()=>run(()=>warehouseExecutionApi.adjust(selectedInventoryId,Number(quantity)),"Positive stock adjustment posted.")}>Adjust +</button>
          <button className="btn" disabled={!selectedInventoryId||!quantity||operationBusy} onClick={()=>run(()=>warehouseExecutionApi.adjust(selectedInventoryId,-Number(quantity)),"Negative stock adjustment posted.")}>Adjust −</button>
          <select className="form-input" value={fromBinId} onChange={e=>setFromBinId(e.target.value)}><option value="">From bin</option>{bins.map(b=><option key={b.id} value={b.id}>{b.code} — {b.name}</option>)}</select>
          <select className="form-input" value={toBinId} onChange={e=>setToBinId(e.target.value)}><option value="">To bin</option>{bins.map(b=><option key={b.id} value={b.id}>{b.code} — {b.name}</option>)}</select>
          <button className="btn primary" disabled={!warehouseId||!selectedInventory||!fromBinId||!toBinId||!quantity||operationBusy} onClick={()=>run(()=>warehouseExecutionApi.transfer({warehouseId,fromBinId,toBinId,productId:value(selectedInventory,"product.id"),quantity:Number(quantity),remarks:"Warehouse monitor transfer"}),"Stock transferred.")}>Transfer</button>
        </div>}
        {selected==="Outbound"&&!loading&&<div className="table-wrap"><table className="table"><thead><tr>{columns.map(c=><th key={c}>{c}</th>)}<th>Action</th></tr></thead><tbody>{filtered.map(row=><tr key={String(row.id)}>{columns.map(c=><td key={c}>{String(row[c]??"—")}</td>)}<td>{["CREATED","PICKING","PICKED","PACKED"].includes(String(row.Status))?<button className="btn" disabled={operationBusy} onClick={()=>doOutbound(String(row.id),String(row.Status))}>{row.Status==="CREATED"?"Start Picking":row.Status==="PICKING"?"Confirm Picking":row.Status==="PICKED"?"Confirm Packing":"Post Goods Issue"}</button>:<span>—</span>}</td></tr>)}{!filtered.length&&<tr><td colSpan={columns.length+1} className="empty">No records available.</td></tr>}</tbody></table></div>}
        {selected!=="Outbound"&&(!loading?<div className="table-wrap"><table className="table"><thead><tr>{columns.map(c=><th key={c}>{c}</th>)}</tr></thead><tbody>{filtered.map((row,index)=><tr key={String(row.id)||index}>{columns.map(c=><td key={c}>{String(row[c]??"—")}</td>)}</tr>)}{!filtered.length&&<tr><td colSpan={Math.max(columns.length,1)} className="empty">No records available.</td></tr>}</tbody></table></div>:<div className="empty">Loading warehouse data…</div>)}
      </div>
    </section>
  </main>;
}
