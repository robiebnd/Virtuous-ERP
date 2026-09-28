"use client";

import {useEffect,useState} from "react";
import {ModuleWorkspace} from "@/components/ModuleWorkspace";
import {qualityApi} from "@/lib/api";

export default function Quality(){
 const [lots,setLots]=useState<any[]>([]); const [loading,setLoading]=useState(true);
 useEffect(()=>{qualityApi.openLots().then(setLots).catch(()=>setLots([])).finally(()=>setLoading(false))},[]);
 const rows=lots.map(l=>({Lot:l.lotNumber||l.id,Product:l.product?.sku||l.product?.name||"—",Type:l.inspectionType||"—",Source:l.sourceDocumentType||"—",Quantity:Number(l.quantity||0),Status:l.status||"OPEN"}));
 return <ModuleWorkspace eyebrow="QUALITY MANAGEMENT" title="Quality Management" description="Manage inspection lots, results, usage decisions, quality stock and corrective notifications." stats={[
  {label:"Open Inspection Lots",value:String(lots.length),foot:"Awaiting results or decision",tone:lots.length?"warning":"success"},
  {label:"Goods Receipt QM",value:String(lots.filter(l=>l.inspectionType==="01").length),foot:"Incoming inspection"},
  {label:"Production QM",value:String(lots.filter(l=>l.inspectionType==="03").length),foot:"In-process / production"},
  {label:"Awaiting Decision",value:String(lots.filter(l=>!String(l.status).includes("CLOSED")).length),foot:"Quality disposition"}
 ]} process={[
  {label:"Inspection Lot",detail:"GR / production trigger",status:"Live",tone:"approved"},
  {label:"Results",detail:"Characteristics & measurements",status:"Available",tone:"ready"},
  {label:"Usage Decision",detail:"Accept / reject / conditional",status:"Available",tone:"ready"},
  {label:"Stock Disposition",detail:"Release or block",status:"Integrated",tone:"approved"}
 ]} columns={["Lot","Product","Type","Source","Quantity","Status"]} rows={rows} loading={loading} searchPlaceholder="Search inspection lot or product...">
 <div className="grid dashboard-grid" style={{marginBottom:18}}>
  <section className="card"><div className="section-title">Quality Controls</div>
   <div className="list-row"><div><strong>Inspection Planning</strong><div className="muted">Characteristics and inspection plans</div></div></div>
   <div className="list-row"><div><strong>Inspection Execution</strong><div className="muted">Record quantitative and qualitative results</div></div></div>
   <div className="list-row"><div><strong>Usage Decision</strong><div className="muted">Release, reject or conditionally accept stock</div></div></div>
  </section>
  <section className="card"><div className="section-title">Inspection Sources</div><div className="detail-grid">
   <div className="detail-field"><span>Goods Receipt</span><strong>Type 01</strong></div>
   <div className="detail-field"><span>Production</span><strong>Type 03</strong></div>
   <div className="detail-field"><span>Quality Stock</span><strong>Integrated</strong></div>
   <div className="detail-field"><span>Notifications</span><strong>Task workflow</strong></div>
  </div></section>
 </div>
 </ModuleWorkspace>;
}