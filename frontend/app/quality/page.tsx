"use client";

import { FormEvent, useEffect, useState } from "react";
import { masterDataApi, productsApi, qualityApi, warehouseExecutionApi } from "@/lib/api";

type Tab="overview"|"planning"|"lots"|"notifications"|"controls";
const productLabel=(p:any)=>p?.sku?(p.name?p.sku+" — "+p.name:p.sku):(p?.name||p?.id||"—");
const fmt=(v:any)=>v==null?"—":Number(v).toLocaleString(undefined,{maximumFractionDigits:2});

export default function Quality(){
 const [tab,setTab]=useState<Tab>("overview");
 const [lots,setLots]=useState<any[]>([]),[chars,setChars]=useState<any[]>([]),[plans,setPlans]=useState<any[]>([]);
 const [notifications,setNotifications]=useState<any[]>([]),[infoRecords,setInfoRecords]=useState<any[]>([]),[catalogs,setCatalogs]=useState<any[]>([]);
 const [products,setProducts]=useState<any[]>([]),[suppliers,setSuppliers]=useState<any[]>([]),[warehouses,setWarehouses]=useState<any[]>([]),[bins,setBins]=useState<any[]>([]);
 const [selectedLotId,setSelectedLotId]=useState(""),[selectedNotificationId,setSelectedNotificationId]=useState("");
 const [busy,setBusy]=useState(false),[error,setError]=useState(""),[message,setMessage]=useState("");
 const [charForm,setCharForm]=useState({code:"",name:"",dataType:"QUANTITATIVE",lowerTolerance:"",upperTolerance:"",unitOfMeasure:""});
 const [planForm,setPlanForm]=useState({productId:"",plantCode:"",planVersion:"1",characteristicIds:[] as string[]});
 const [lotForm,setLotForm]=useState({productId:"",plantCode:"",inspectionType:"01",sourceDocumentType:"MANUAL",sourceDocumentId:"",quantity:"1"});
 const [resultForm,setResultForm]=useState({characteristicId:"",measuredValue:"",qualitativeResult:"PASS"});
 const [decisionForm,setDecisionForm]=useState({decisionCode:"ACCEPT",stockAction:"RELEASE",remarks:"",binId:""});
 const [notificationForm,setNotificationForm]=useState({notificationType:"QUALITY_DEFECT",priority:"MEDIUM",shortText:"",referenceObjectType:"",referenceObjectId:"",defectType:"DEFECT",objectPart:"",cause:"",description:"",task:"Corrective action",owner:""});
 const [catalogForm,setCatalogForm]=useState({catalogType:"DEFECT",code:"",description:""});
 const [infoForm,setInfoForm]=useState({supplierId:"",productId:"",inspectionRequired:true,skipLotAfterGoodLots:""});

 const selectedLot=lots.find(x=>x.id===selectedLotId);
 const selectedNotification=notifications.find(x=>x.id===selectedNotificationId);

 async function load(){
   setError("");
   try{
     const [l,c,p,n,i,cat,pr,s,w]=await Promise.all([
       qualityApi.lots(),qualityApi.characteristics(),qualityApi.plans(),qualityApi.notifications(),qualityApi.infoRecords(),qualityApi.catalogs(),
       productsApi.active(),masterDataApi.suppliers(),warehouseExecutionApi.warehouses()
     ]);
     setLots(l);setChars(c);setPlans(p);setNotifications(n);setInfoRecords(i);setCatalogs(cat);setProducts(pr);setSuppliers(s);setWarehouses(w);
     if(!selectedLotId&&l[0]?.id)setSelectedLotId(l[0].id);
     if(!selectedNotificationId&&n[0]?.id)setSelectedNotificationId(n[0].id);
   }catch(e:any){setError(e?.message||"Unable to load quality data.")}
 }
 useEffect(()=>{load()},[]);

 useEffect(()=>{
   if(!selectedLot)return;
   const wh=warehouses.find(w=>w.code===selectedLot.plantCode||w.name===selectedLot.plantCode);
   if(!wh){setBins([]);return;}
   warehouseExecutionApi.bins(wh.id).then(setBins).catch(()=>setBins([]));
 },[selectedLotId,selectedLot?.plantCode,warehouses]);

 const run=async(fn:()=>Promise<any>,ok:string)=>{
   setBusy(true);setError("");setMessage("");
   try{await fn();setMessage(ok);await load()}catch(e:any){setError(e?.message||"Operation failed.")}finally{setBusy(false)}
 };
 const submitChar=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createCharacteristic({
   ...charForm,lowerTolerance:charForm.lowerTolerance===""?null:Number(charForm.lowerTolerance),upperTolerance:charForm.upperTolerance===""?null:Number(charForm.upperTolerance)
 }),"Inspection characteristic created.")};
 const submitPlan=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createPlan({
   plan:{product:{id:planForm.productId},plantCode:planForm.plantCode,planVersion:planForm.planVersion,status:"ACTIVE"},
   characteristicIds:planForm.characteristicIds
 }),"Inspection plan created.")};
 const submitLot=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createLot({
   productId:lotForm.productId,plantCode:lotForm.plantCode,inspectionType:lotForm.inspectionType,sourceDocumentType:lotForm.sourceDocumentType,
   sourceDocumentId:lotForm.sourceDocumentId||null,quantity:Number(lotForm.quantity)
 }),"Inspection lot created.")};
 const submitResult=(e:FormEvent)=>{e.preventDefault();if(!selectedLot){setError("Select an inspection lot.");return}run(()=>qualityApi.result(selectedLot.id,{
   characteristicId:resultForm.characteristicId,measuredValue:resultForm.measuredValue===""?null:Number(resultForm.measuredValue),qualitativeResult:resultForm.qualitativeResult
 }),"Inspection result recorded.")};
 const submitDecision=(e:FormEvent)=>{e.preventDefault();if(!selectedLot){setError("Select an inspection lot.");return}run(()=>qualityApi.decision(selectedLot.id,{...decisionForm,binId:decisionForm.binId||null}),"Usage decision posted.")};
 const submitNotification=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createNotification({
   notificationType:notificationForm.notificationType,priority:notificationForm.priority,shortText:notificationForm.shortText,status:"OPEN",
   referenceObjectType:notificationForm.referenceObjectType||null,referenceObjectId:notificationForm.referenceObjectId||null,
   items:[{defectType:notificationForm.defectType,objectPart:notificationForm.objectPart,cause:notificationForm.cause,description:notificationForm.description}],
   tasks:[{task:notificationForm.task,owner:notificationForm.owner,status:"OPEN"}]
 }),"Quality notification created.")};
 const submitCatalog=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createCatalog(catalogForm),"Catalog code created.")};
 const submitInfo=(e:FormEvent)=>{e.preventDefault();run(()=>qualityApi.createInfoRecord({supplier:{id:infoForm.supplierId},product:{id:infoForm.productId},inspectionRequired:infoForm.inspectionRequired,skipLotAfterGoodLots:infoForm.skipLotAfterGoodLots?Number(infoForm.skipLotAfterGoodLots):null}),"Quality info record created.")};
 const completeTask=(task:any)=>selectedNotification&&run(()=>qualityApi.completeTask(selectedNotification.id,task.id),"Quality task completed.");
 const closeNotification=()=>selectedNotification&&run(()=>qualityApi.closeNotification(selectedNotification.id),"Quality notification closed.");

 return <div className="content quality-page">
  <div className="page-head quality-head"><div><div className="eyebrow">QUALITY MANAGEMENT</div><h1>Quality Management</h1><p>Inspection planning, inspection execution, usage decisions, stock disposition and quality notifications.</p></div><div className="actions"><button className="btn" onClick={load} disabled={busy}>Refresh</button><button className="btn primary" onClick={()=>setTab("lots")}>Inspection execution</button></div></div>
  {(error||message)&&<div className={error?"alert error":"alert"}>{error||message}</div>}
  <div className="quality-stats">
   <div className="stat-card"><span className="stat-label">Open lots</span><strong className="stat-value">{lots.filter(x=>x.status==="OPEN").length}</strong><span className="stat-foot">Awaiting quality disposition</span></div>
   <div className="stat-card"><span className="stat-label">Inspection plans</span><strong className="stat-value">{plans.length}</strong><span className="stat-foot">Active product controls</span></div>
   <div className="stat-card"><span className="stat-label">Characteristics</span><strong className="stat-value">{chars.length}</strong><span className="stat-foot">Inspection measurements</span></div>
   <div className="stat-card"><span className="stat-label">Notifications</span><strong className="stat-value">{notifications.length}</strong><span className="stat-foot">Quality issues & actions</span></div>
  </div>
  <div className="quality-tabs">{([["overview","Overview"],["planning","Inspection planning"],["lots","Inspection lots"],["notifications","Notifications"],["controls","Quality controls"]] as [Tab,string][]).map(([id,label])=><button key={id} className={tab===id?"quality-tab active":"quality-tab"} onClick={()=>setTab(id)}>{label}</button>)}</div>

  {tab==="overview"&&<div className="quality-grid">
   <section className="card quality-wide"><div className="section-title">Quality process</div><div className="quality-process">
    {[["01","Plan","Characteristics & plans",plans.length],["02","Lot","GR / production inspection",lots.length],["03","Results","Measurements & pass/fail",lots.filter(x=>x.status==="OPEN").length],["04","Decision","Accept / reject / conditional",lots.filter(x=>x.status==="OPEN").length],["05","Stock","Release or block",infoRecords.length],["06","CAPA","Notifications & tasks",notifications.length]].map(x=><div className="quality-process-step" key={x[0]}><b>{x[0]}</b><strong>{x[1]}</strong><span>{x[2]}</span><em>{x[3]}</em></div>)}
   </div></section>
   <section className="card"><div className="section-title">Inspection sources</div><div className="detail-grid"><div className="detail-field"><span>Type 01</span><strong>Goods receipt</strong></div><div className="detail-field"><span>Type 03</span><strong>Production</strong></div><div className="detail-field"><span>Quality stock</span><strong>Integrated</strong></div><div className="detail-field"><span>Info records</span><strong>{infoRecords.length}</strong></div></div></section>
   <section className="card quality-wide"><div className="section-title">Open inspection lots</div><div className="table-wrap"><table className="table"><thead><tr><th>Lot</th><th>Product</th><th>Type</th><th>Plant</th><th>Quantity</th><th>Status</th></tr></thead><tbody>{lots.filter(x=>x.status==="OPEN").slice(0,15).map(l=><tr key={l.id} className="quality-click-row" onClick={()=>{setSelectedLotId(l.id);setTab("lots")}}><td><strong>{l.lotNumber}</strong></td><td>{productLabel(l.product)}</td><td>{l.inspectionType}</td><td>{l.plantCode}</td><td>{fmt(l.quantity)}</td><td>{l.status}</td></tr>)}</tbody></table></div></section>
  </div>}

  {tab==="planning"&&<div className="quality-grid">
   <section className="card"><div className="section-title">Inspection characteristic</div><form className="quality-form-grid" onSubmit={submitChar}>
    <div className="form-field"><label>Code</label><input className="form-input" required value={charForm.code} onChange={e=>setCharForm({...charForm,code:e.target.value})}/></div>
    <div className="form-field"><label>Name</label><input className="form-input" required value={charForm.name} onChange={e=>setCharForm({...charForm,name:e.target.value})}/></div>
    <div className="form-field"><label>Data type</label><select className="form-input" value={charForm.dataType} onChange={e=>setCharForm({...charForm,dataType:e.target.value})}><option>QUANTITATIVE</option><option>QUALITATIVE</option></select></div>
    <div className="form-field"><label>Lower tolerance</label><input className="form-input" type="number" value={charForm.lowerTolerance} onChange={e=>setCharForm({...charForm,lowerTolerance:e.target.value})}/></div>
    <div className="form-field"><label>Upper tolerance</label><input className="form-input" type="number" value={charForm.upperTolerance} onChange={e=>setCharForm({...charForm,upperTolerance:e.target.value})}/></div>
    <div className="form-field"><label>Unit of measure</label><input className="form-input" value={charForm.unitOfMeasure} onChange={e=>setCharForm({...charForm,unitOfMeasure:e.target.value})}/></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Create characteristic</button></div>
   </form></section>
   <section className="card"><div className="section-title">Inspection plan</div><form className="quality-form-grid" onSubmit={submitPlan}>
    <div className="form-field"><label>Product</label><select className="form-input" required value={planForm.productId} onChange={e=>setPlanForm({...planForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
    <div className="form-field"><label>Plant</label><input className="form-input" required value={planForm.plantCode} onChange={e=>setPlanForm({...planForm,plantCode:e.target.value})}/></div>
    <div className="form-field"><label>Version</label><input className="form-input" value={planForm.planVersion} onChange={e=>setPlanForm({...planForm,planVersion:e.target.value})}/></div>
    <div className="quality-characteristics"><label>Characteristics</label>{chars.map(c=><label className="quality-check" key={c.id}><input type="checkbox" checked={planForm.characteristicIds.includes(c.id)} onChange={e=>setPlanForm({...planForm,characteristicIds:e.target.checked?[...planForm.characteristicIds,c.id]:planForm.characteristicIds.filter(x=>x!==c.id)})}/><span>{c.code} — {c.name}</span></label>)}</div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy||!planForm.characteristicIds.length}>Create plan</button></div>
   </form></section>
   <section className="card quality-wide"><div className="section-title">Inspection plans</div><div className="table-wrap"><table className="table"><thead><tr><th>Plan</th><th>Product</th><th>Plant</th><th>Version</th><th>Characteristics</th><th>Status</th></tr></thead><tbody>{plans.map(p=><tr key={p.id}><td>{p.planNumber}</td><td>{productLabel(p.product)}</td><td>{p.plantCode}</td><td>{p.planVersion||"—"}</td><td>{p.characteristics?.length||0}</td><td>{p.status}</td></tr>)}</tbody></table></div></section>
  </div>}

  {tab==="lots"&&<div className="quality-grid">
   <section className="card quality-wide"><div className="section-title">Inspection lots</div><div className="table-wrap"><table className="table"><thead><tr><th>Lot</th><th>Product</th><th>Type</th><th>Source</th><th>Plant</th><th>Qty</th><th>Status</th></tr></thead><tbody>{lots.map(l=><tr key={l.id} className={selectedLotId===l.id?"quality-click-row selected-row":"quality-click-row"} onClick={()=>setSelectedLotId(l.id)}><td>{l.lotNumber}</td><td>{productLabel(l.product)}</td><td>{l.inspectionType}</td><td>{l.sourceDocumentType||"—"}</td><td>{l.plantCode}</td><td>{fmt(l.quantity)}</td><td>{l.status}</td></tr>)}</tbody></table></div></section>
   <section className="card"><div className="section-title">Create inspection lot</div><form className="quality-form-grid" onSubmit={submitLot}>
    <div className="form-field"><label>Product</label><select className="form-input" required value={lotForm.productId} onChange={e=>setLotForm({...lotForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
    <div className="form-field"><label>Plant</label><input className="form-input" required value={lotForm.plantCode} onChange={e=>setLotForm({...lotForm,plantCode:e.target.value})}/></div>
    <div className="form-field"><label>Inspection type</label><select className="form-input" value={lotForm.inspectionType} onChange={e=>setLotForm({...lotForm,inspectionType:e.target.value})}><option>01</option><option>03</option><option>MANUAL</option></select></div>
    <div className="form-field"><label>Source document</label><input className="form-input" value={lotForm.sourceDocumentType} onChange={e=>setLotForm({...lotForm,sourceDocumentType:e.target.value})}/></div>
    <div className="form-field"><label>Source document ID</label><input className="form-input" value={lotForm.sourceDocumentId} onChange={e=>setLotForm({...lotForm,sourceDocumentId:e.target.value})}/></div>
    <div className="form-field"><label>Quantity</label><input className="form-input" type="number" min="0.01" value={lotForm.quantity} onChange={e=>setLotForm({...lotForm,quantity:e.target.value})}/></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Create lot</button></div>
   </form></section>
   <section className="card"><div className="section-title">Record result {selectedLot&&<span className="section-meta">{selectedLot.lotNumber}</span>}</div>{!selectedLot?<div className="empty">Select an inspection lot.</div>:<form className="quality-form-grid" onSubmit={submitResult}>
    <div className="form-field"><label>Characteristic</label><select className="form-input" required value={resultForm.characteristicId} onChange={e=>setResultForm({...resultForm,characteristicId:e.target.value})}><option value="">Select characteristic</option>{chars.map(c=><option key={c.id} value={c.id}>{c.code} — {c.name}</option>)}</select></div>
    <div className="form-field"><label>Measured value</label><input className="form-input" type="number" value={resultForm.measuredValue} onChange={e=>setResultForm({...resultForm,measuredValue:e.target.value})}/></div>
    <div className="form-field"><label>Qualitative result</label><select className="form-input" value={resultForm.qualitativeResult} onChange={e=>setResultForm({...resultForm,qualitativeResult:e.target.value})}><option>PASS</option><option>FAIL</option><option>CONDITIONAL</option></select></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Record result</button></div>
   </form>}</section>
   <section className="card"><div className="section-title">Usage decision {selectedLot&&<span className="section-meta">{selectedLot.lotNumber}</span>}</div>{!selectedLot?<div className="empty">Select an inspection lot.</div>:<form className="quality-form-grid" onSubmit={submitDecision}>
    <div className="form-field"><label>Decision</label><select className="form-input" value={decisionForm.decisionCode} onChange={e=>setDecisionForm({...decisionForm,decisionCode:e.target.value})}><option>ACCEPT</option><option>REJECT</option><option>CONDITIONAL</option></select></div>
    <div className="form-field"><label>Stock action</label><select className="form-input" value={decisionForm.stockAction} onChange={e=>setDecisionForm({...decisionForm,stockAction:e.target.value})}><option>RELEASE</option><option>BLOCK</option></select></div>
    <div className="form-field"><label>Bin</label><select className="form-input" value={decisionForm.binId} onChange={e=>setDecisionForm({...decisionForm,binId:e.target.value})}><option value="">Select bin</option>{bins.map((b:any)=><option key={b.id} value={b.id}>{b.code||b.name||b.id}</option>)}</select></div>
    <div className="form-field"><label>Remarks</label><input className="form-input" value={decisionForm.remarks} onChange={e=>setDecisionForm({...decisionForm,remarks:e.target.value})}/></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Post decision</button></div>
   </form>}</section>
  </div>}

  {tab==="notifications"&&<div className="quality-grid">
   <section className="card"><div className="section-title">Create quality notification</div><form className="quality-form-grid" onSubmit={submitNotification}>
    <div className="form-field"><label>Type</label><input className="form-input" value={notificationForm.notificationType} onChange={e=>setNotificationForm({...notificationForm,notificationType:e.target.value})}/></div>
    <div className="form-field"><label>Priority</label><select className="form-input" value={notificationForm.priority} onChange={e=>setNotificationForm({...notificationForm,priority:e.target.value})}><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>URGENT</option></select></div>
    <div className="form-field"><label>Short text</label><input className="form-input" required value={notificationForm.shortText} onChange={e=>setNotificationForm({...notificationForm,shortText:e.target.value})}/></div>
    <div className="form-field"><label>Reference type</label><input className="form-input" value={notificationForm.referenceObjectType} onChange={e=>setNotificationForm({...notificationForm,referenceObjectType:e.target.value})}/></div>
    <div className="form-field"><label>Reference ID</label><input className="form-input" value={notificationForm.referenceObjectId} onChange={e=>setNotificationForm({...notificationForm,referenceObjectId:e.target.value})}/></div>
    <div className="form-field"><label>Defect type</label><input className="form-input" value={notificationForm.defectType} onChange={e=>setNotificationForm({...notificationForm,defectType:e.target.value})}/></div>
    <div className="form-field"><label>Object part</label><input className="form-input" value={notificationForm.objectPart} onChange={e=>setNotificationForm({...notificationForm,objectPart:e.target.value})}/></div>
    <div className="form-field"><label>Cause</label><input className="form-input" value={notificationForm.cause} onChange={e=>setNotificationForm({...notificationForm,cause:e.target.value})}/></div>
    <div className="form-field quality-form-wide"><label>Description</label><textarea className="form-input quality-textarea" value={notificationForm.description} onChange={e=>setNotificationForm({...notificationForm,description:e.target.value})}/></div>
    <div className="form-field"><label>Corrective task</label><input className="form-input" value={notificationForm.task} onChange={e=>setNotificationForm({...notificationForm,task:e.target.value})}/></div>
    <div className="form-field"><label>Owner</label><input className="form-input" value={notificationForm.owner} onChange={e=>setNotificationForm({...notificationForm,owner:e.target.value})}/></div>
    <div className="quality-form-actions quality-form-wide"><button className="btn primary" disabled={busy}>Create notification</button></div>
   </form></section>
   <section className="card quality-wide"><div className="section-title">Notifications</div><div className="table-wrap"><table className="table"><thead><tr><th>Number</th><th>Type</th><th>Priority</th><th>Short text</th><th>Status</th><th>Tasks</th></tr></thead><tbody>{notifications.map(n=><tr key={n.id} className={selectedNotificationId===n.id?"quality-click-row selected-row":"quality-click-row"} onClick={()=>setSelectedNotificationId(n.id)}><td>{n.notificationNumber}</td><td>{n.notificationType}</td><td>{n.priority}</td><td>{n.shortText}</td><td>{n.status}</td><td>{n.tasks?.length||0}</td></tr>)}</tbody></table></div></section>
   <section className="card quality-wide"><div className="section-title">Notification tasks {selectedNotification&&<span className="section-meta">{selectedNotification.notificationNumber}</span>}</div>{!selectedNotification?<div className="empty">Select a notification.</div>:<div className="notification-tasks">{(selectedNotification.tasks||[]).map((t:any)=><div className="notification-task" key={t.id}><div><strong>{t.task}</strong><span>{t.owner||"Unassigned"} · {t.status}</span></div><button className="btn" disabled={busy||t.status==="COMPLETED"} onClick={()=>completeTask(t)}>Complete</button></div>)}<div className="actions notification-close"><button className="btn primary" disabled={busy||selectedNotification.status==="CLOSED"} onClick={closeNotification}>Close notification</button></div></div>}</section>
  </div>}

  {tab==="controls"&&<div className="quality-grid">
   <section className="card"><div className="section-title">Quality info record</div><form className="quality-form-grid" onSubmit={submitInfo}>
    <div className="form-field"><label>Supplier</label><select className="form-input" required value={infoForm.supplierId} onChange={e=>setInfoForm({...infoForm,supplierId:e.target.value})}><option value="">Select supplier</option>{suppliers.map(s=><option key={s.id} value={s.id}>{s.name||s.supplierNumber||s.id}</option>)}</select></div>
    <div className="form-field"><label>Product</label><select className="form-input" required value={infoForm.productId} onChange={e=>setInfoForm({...infoForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
    <label className="quality-check quality-form-wide"><input type="checkbox" checked={infoForm.inspectionRequired} onChange={e=>setInfoForm({...infoForm,inspectionRequired:e.target.checked})}/><span>Inspection required</span></label>
    <div className="form-field"><label>Skip lots after good lots</label><input className="form-input" type="number" min="0" value={infoForm.skipLotAfterGoodLots} onChange={e=>setInfoForm({...infoForm,skipLotAfterGoodLots:e.target.value})}/></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Save info record</button></div>
   </form></section>
   <section className="card"><div className="section-title">Catalog code</div><form className="quality-form-grid" onSubmit={submitCatalog}>
    <div className="form-field"><label>Catalog type</label><input className="form-input" required value={catalogForm.catalogType} onChange={e=>setCatalogForm({...catalogForm,catalogType:e.target.value})}/></div>
    <div className="form-field"><label>Code</label><input className="form-input" required value={catalogForm.code} onChange={e=>setCatalogForm({...catalogForm,code:e.target.value})}/></div>
    <div className="form-field quality-form-wide"><label>Description</label><input className="form-input" required value={catalogForm.description} onChange={e=>setCatalogForm({...catalogForm,description:e.target.value})}/></div>
    <div className="quality-form-actions"><button className="btn primary" disabled={busy}>Create code</button></div>
   </form></section>
   <section className="card quality-wide"><div className="section-title">Quality info records</div><div className="table-wrap"><table className="table"><thead><tr><th>Supplier</th><th>Product</th><th>Inspection required</th><th>Skip after good lots</th></tr></thead><tbody>{infoRecords.map(x=><tr key={x.id}><td>{x.supplier?.name||x.supplier?.supplierNumber||"—"}</td><td>{productLabel(x.product)}</td><td>{x.inspectionRequired?"YES":"NO"}</td><td>{x.skipLotAfterGoodLots??"—"}</td></tr>)}</tbody></table></div></section>
   <section className="card quality-wide"><div className="section-title">Catalog codes</div><div className="table-wrap"><table className="table"><thead><tr><th>Type</th><th>Code</th><th>Description</th></tr></thead><tbody>{catalogs.map(x=><tr key={x.id}><td>{x.catalogType}</td><td>{x.code}</td><td>{x.description}</td></tr>)}</tbody></table></div></section>
  </div>}
 </div>
}