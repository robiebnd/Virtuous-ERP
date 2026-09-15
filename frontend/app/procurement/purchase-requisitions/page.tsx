"use client";

import { useEffect, useMemo, useState } from "react";
import { procurementApi, PurchaseRequisition } from "@/lib/api";

const demo:PurchaseRequisition[]=[
 {id:"1",requisitionNumber:"PR-000124",status:"PENDING_APPROVAL",documentType:"NB",purchasingGroup:"PURCHASING",plantCode:"MAIN",storageLocation:"MAIN",currency:"USD",totalValue:2500,requestedDeliveryDate:"2026-09-20T00:00:00"},
 {id:"2",requisitionNumber:"PR-000123",status:"APPROVED",documentType:"NB",purchasingGroup:"PURCHASING",plantCode:"MAIN",storageLocation:"MAIN",currency:"USD",totalValue:840,requestedDeliveryDate:"2026-09-18T00:00:00"},
 {id:"3",requisitionNumber:"PR-000122",status:"DRAFT",documentType:"NB",purchasingGroup:"OPERATIONS",plantCode:"MAIN",storageLocation:"S001",currency:"USD",totalValue:1250,requestedDeliveryDate:"2026-09-25T00:00:00"}
];

function labelStatus(status?:string){return (status||"DRAFT").replaceAll("_"," ")}
function statusClass(status?:string){const s=(status||"").toLowerCase();if(s.includes("approved"))return "approved";if(s.includes("pending"))return "pending";if(s.includes("blocked"))return "blocked";if(s.includes("ready"))return "ready";return "draft"}

export default function PurchaseRequisitions(){
 const [rows,setRows]=useState<PurchaseRequisition[]>(demo); const [loading,setLoading]=useState(true); const [query,setQuery]=useState(""); const [status,setStatus]=useState("ALL");
 useEffect(()=>{procurementApi.requisitions().then(setRows).catch(()=>{}).finally(()=>setLoading(false))},[]);
 const filtered=useMemo(()=>rows.filter(r=>{const q=query.toLowerCase();const text=JSON.stringify(r).toLowerCase();return (!q||text.includes(q))&&(status==="ALL"||r.status===status)}),[rows,query,status]);
 return <div className="content">
  <div className="page-head"><div><h1>Purchase Requisitions</h1><p>Create, review and approve procurement requirements before purchase order creation.</p></div><div className="actions"><button className="btn" onClick={()=>setRows(demo)}>Refresh</button><button className="btn primary">New Purchase Requisition</button></div></div>
  <section className="card"><div className="toolbar"><input className="filter" style={{flex:1,minWidth:220}} placeholder="Search requisition, plant, purchasing group..." value={query} onChange={e=>setQuery(e.target.value)}/><select className="filter" value={status} onChange={e=>setStatus(e.target.value)}><option value="ALL">All statuses</option><option value="DRAFT">Draft</option><option value="PENDING_APPROVAL">Pending approval</option><option value="APPROVED">Approved</option></select><button className="btn">Filter</button></div>
   <div className="table-wrap"><table className="table"><thead><tr><th>Requisition</th><th>Document Type</th><th>Purchasing Group</th><th>Plant</th><th>Storage Location</th><th>Delivery Date</th><th>Value</th><th>Status</th><th></th></tr></thead><tbody>{loading?<tr><td colSpan={9} className="empty">Loading requisitions…</td></tr>:filtered.map(r=><tr key={r.id}><td><a className="link" href={`/procurement/purchase-requisitions/${r.id}`}>{r.requisitionNumber||r.id}</a></td><td>{r.documentType||"NB"}</td><td>{r.purchasingGroup||"—"}</td><td>{r.plantCode||"—"}</td><td>{r.storageLocation||"—"}</td><td>{r.requestedDeliveryDate?new Date(r.requestedDeliveryDate).toLocaleDateString():"—"}</td><td>{r.currency||"USD"} {Number(r.totalValue||r.valuationPrice||0).toFixed(2)}</td><td><span className={`status ${statusClass(r.status)}`}>{labelStatus(r.status)}</span></td><td>⋮</td></tr>)}{!loading&&filtered.length===0&&<tr><td colSpan={9} className="empty">No requisitions found.</td></tr>}</tbody></table></div>
  </section>
 </div>
}
