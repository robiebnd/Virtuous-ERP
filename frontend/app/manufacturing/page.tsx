"use client";

import {useEffect,useState} from "react";
import {ModuleWorkspace} from "@/components/ModuleWorkspace";
import {manufacturingApi} from "@/lib/api";

export default function Manufacturing(){
 const [orders,setOrders]=useState<any[]>([]); const [boms,setBoms]=useState<any[]>([]); const [loading,setLoading]=useState(true);
 const load=()=>{setLoading(true);Promise.all([manufacturingApi.orders(),manufacturingApi.boms()]).then(([o,b])=>{setOrders(o);setBoms(b)}).catch(()=>{}).finally(()=>setLoading(false))};
 useEffect(load,[]);
 const rows=orders.map(o=>({Order:o.orderNumber||o.id,Product:o.product?.sku||o.product?.name||"—",Planned:Number(o.plannedQuantity||0),Confirmed:Number(o.confirmedQuantity||0),Received:Number(o.receivedQuantity||0),Status:o.status||"CREATED"}));
 return <ModuleWorkspace eyebrow="MANUFACTURING / EXECUTION" title="Manufacturing" description="Plan, release, execute and close production orders using BOMs, routings, MRP and capacity." stats={[
  {label:"Production Orders",value:String(orders.length),foot:"Live manufacturing orders"},
  {label:"Active BOMs",value:String(boms.length),foot:"Configured product structures"},
  {label:"In Process",value:String(orders.filter(o=>o.status==="IN_PROCESS").length),foot:"Shop-floor execution",tone:"warning"},
  {label:"Closed",value:String(orders.filter(o=>o.status==="CLOSED").length),foot:"Completed production",tone:"success"}
 ]} process={[
  {label:"BOM / MRP",detail:"Material planning",status:boms.length?"Ready":"Setup required",tone:boms.length?"approved":"draft"},
  {label:"Production",detail:"Orders & components",status:String(orders.length),tone:"ready"},
  {label:"Confirmation",detail:"Output & scrap",status:String(orders.filter(o=>o.confirmedQuantity>0).length),tone:"ready"},
  {label:"Receipt",detail:"Finished goods",status:String(orders.filter(o=>o.receivedQuantity>0).length),tone:"ready"}
 ]} columns={["Order","Product","Planned","Confirmed","Received","Status"]} rows={rows} loading={loading} searchPlaceholder="Search production order or product...">
 <div className="grid dashboard-grid" style={{marginBottom:18}}>
  <section className="card"><div className="section-title">Manufacturing Workspaces</div>
   <div className="list-row"><div><strong>BOMs</strong><div className="muted">Multi-level product structures used by MRP</div></div></div>
   <div className="list-row"><div><strong>Production Orders</strong><div className="muted">Release, issue components, confirm and receive</div></div></div>
   <div className="list-row"><div><strong>MRP</strong><div className="muted">Demand, stock, open supply and dependent requirements</div></div></div>
  </section>
  <section className="card"><div className="section-title">Execution Status</div><div className="detail-grid">
   <div className="detail-field"><span>Created</span><strong>{orders.filter(o=>o.status==="CREATED").length}</strong></div>
   <div className="detail-field"><span>Released</span><strong>{orders.filter(o=>o.status==="RELEASED").length}</strong></div>
   <div className="detail-field"><span>Confirmed</span><strong>{orders.filter(o=>o.status==="CONFIRMED").length}</strong></div>
   <div className="detail-field"><span>Closed</span><strong>{orders.filter(o=>o.status==="CLOSED").length}</strong></div>
  </div></section>
 </div>
 </ModuleWorkspace>;
}