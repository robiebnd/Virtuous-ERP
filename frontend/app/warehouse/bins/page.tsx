"use client";

import { FormEvent, useEffect, useState } from "react";
import { masterDataApi, warehouseExecutionApi } from "@/lib/api";

const types=["STORAGE","RECEIVING","SHIPPING","QUARANTINE","DAMAGE","PRODUCTION","PICKING","STAGING"];

export default function BinsPage(){
 const [warehouses,setWarehouses]=useState<any[]>([]),[bins,setBins]=useState<any[]>([]);
 const [warehouseId,setWarehouseId]=useState(""),[loading,setLoading]=useState(true),[saving,setSaving]=useState(false);
 const [error,setError]=useState(""),[message,setMessage]=useState(""),[editing,setEditing]=useState<any|null>(null);
 const [form,setForm]=useState({code:"",name:"",type:"STORAGE",capacity:"",description:""});
 const load=async()=>{
  setLoading(true);setError("");
  try{const w=await masterDataApi.warehouses();setWarehouses(w);const b=await warehouseExecutionApi.allBins();setBins(b);if(!warehouseId&&w[0]?.id)setWarehouseId(w[0].id)}
  catch(e:any){setError(e?.message||"Unable to load bins.")}finally{setLoading(false)}
 };
 useEffect(()=>{load()},[]);
 const submit=async(e:FormEvent)=>{
  e.preventDefault();setSaving(true);setError("");setMessage("");
  try{if(editing){await warehouseExecutionApi.updateBin(editing.id,{name:form.name,type:form.type,capacity:form.capacity?Number(form.capacity):0,active:editing.active!==false,status:editing.status||"AVAILABLE",description:form.description||null});setMessage("Bin updated successfully.");}else{await warehouseExecutionApi.createBin({warehouseId,code:form.code,name:form.name,type:form.type,capacity:form.capacity?Number(form.capacity):0});setMessage("Bin created successfully.");}setEditing(null);setForm({code:"",name:"",type:"STORAGE",capacity:"",description:""});await load()}
  catch(e:any){setError(e?.message||"Unable to create bin.")}finally{setSaving(false)}
 };
 const editBin=(b:any)=>{setEditing(b);setWarehouseId(b.warehouseId||warehouseId);setForm({code:b.code||"",name:b.name||"",type:b.type||"STORAGE",capacity:String(b.capacity??""),description:b.description||""})};
 const remove=async(id:string)=>{
  if(!window.confirm("Delete this bin?"))return;
  try{await warehouseExecutionApi.deleteBin(id);setMessage("Bin deleted.");await load()}catch(e:any){setError(e?.message||"Unable to delete bin.")}
 };
 const rows=warehouseId?bins.filter(b=>b.warehouseId===warehouseId||b.warehouse?.id===warehouseId):bins;
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">WAREHOUSE / BIN MANAGEMENT</div><h1>Bins</h1><p>Create and maintain warehouse storage, receiving, staging and other bin locations.</p></div><button className="btn" onClick={load}>Refresh</button></div>
  {(error||message)&&<div className={error?"alert error":"alert success"}>{error||message}</div>}
  <div className="warehouse-bin-layout">
   <section className="card"><div className="section-title">{editing?"Edit Bin":"Create Bin"}</div><form className="warehouse-form-grid" onSubmit={submit}>
    <div className="form-field"><label>Warehouse</label><select className="form-input" required value={warehouseId} onChange={e=>setWarehouseId(e.target.value)}><option value="">Select warehouse</option>{warehouses.map(w=><option key={w.id} value={w.id}>{w.code} — {w.name}</option>)}</select></div>
    <div className="form-field"><label>Bin Code</label><input className="form-input" required disabled={!!editing} value={form.code} onChange={e=>setForm({...form,code:e.target.value})} placeholder="e.g. A-01-01"/></div>
    <div className="form-field"><label>Bin Name</label><input className="form-input" required value={form.name} onChange={e=>setForm({...form,name:e.target.value})} placeholder="Aisle 01 Rack 01"/></div>
    <div className="form-field"><label>Bin Type</label><select className="form-input" value={form.type} onChange={e=>setForm({...form,type:e.target.value})}>{types.map(t=><option key={t}>{t}</option>)}</select></div>
    <div className="form-field"><label>Capacity</label><input className="form-input" type="number" min="0" step="0.01" value={form.capacity} onChange={e=>setForm({...form,capacity:e.target.value})} placeholder="0 = unlimited"/></div>
    <div className="form-field warehouse-form-wide"><label>Description</label><textarea className="form-input warehouse-textarea" value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></div>
    <div className="warehouse-form-actions"><button className="btn primary" disabled={saving||!warehouseId}>{saving?(editing?"Saving…":"Creating…"):(editing?"Save Changes":"Create Bin")}</button></div>
   </form></section>
   <section className="card"><div className="section-title">Bin Locations <span className="section-meta">{rows.length} bins</span></div>
    <div className="table-wrap"><table className="table"><thead><tr><th>Code</th><th>Name</th><th>Warehouse</th><th>Type</th><th>Receiving</th><th>Capacity</th><th>Status</th><th></th></tr></thead><tbody>{!loading&&rows.map(b=><tr key={b.id}><td><strong>{b.code}</strong></td><td>{b.name}</td><td>{b.warehouseCode||b.warehouse?.code||"—"}</td><td>{b.type}</td><td>{b.receivingBin?"YES":"NO"}</td><td>{b.capacity??0}</td><td>{b.active===false?"INACTIVE":"ACTIVE"}</td><td className="actions"><button className="btn" onClick={()=>editBin(b)}>Edit</button><button className="btn" onClick={()=>remove(b.id)}>Delete</button></td></tr>)}{!loading&&!rows.length&&<tr><td colSpan={8} className="empty">No bins configured for this warehouse.</td></tr>}{loading&&<tr><td colSpan={8} className="empty">Loading bins…</td></tr>}</tbody></table></div>
   </section>
  </div>
 </main>;
}