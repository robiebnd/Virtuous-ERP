"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import { manufacturingApi, productsApi, warehouseExecutionApi } from "@/lib/api";

type Tab = "overview"|"production"|"structures"|"planning"|"kanban";

const today = () => new Date().toISOString().slice(0,10);
const productLabel = (p:any) => p?.sku ? (p.name ? p.sku+" — "+p.name : p.sku) : (p?.name || p?.id || "—");
const fmt = (v:any) => v == null ? "—" : Number(v).toLocaleString(undefined,{maximumFractionDigits:2});
const blankBomItem = () => ({componentProductId:"",quantity:"1",uom:"EA"});
const blankRoutingOp = () => ({description:"",workCenter:"",setupMinutes:"0",runMinutes:"0"});
const blankRecipeOp = () => ({instruction:"",resourceName:"",standardMinutes:"0"});

export default function Manufacturing() {
  const [tab,setTab] = useState<Tab>("overview");
  const [orders,setOrders] = useState<any[]>([]);
  const [boms,setBoms] = useState<any[]>([]);
  const [routings,setRoutings] = useState<any[]>([]);
  const [recipes,setRecipes] = useState<any[]>([]);
  const [capacity,setCapacity] = useState<any[]>([]);
  const [cycles,setCycles] = useState<any[]>([]);
  const [signals,setSignals] = useState<any[]>([]);
  const [mrpPlans,setMrpPlans] = useState<any[]>([]);
  const [mrpComponents,setMrpComponents] = useState<any[]>([]);
  const [products,setProducts] = useState<any[]>([]);
  const [warehouses,setWarehouses] = useState<any[]>([]);
  const [bins,setBins] = useState<any[]>([]);
  const [selectedOrderId,setSelectedOrderId] = useState("");
  const [busy,setBusy] = useState(false);
  const [error,setError] = useState("");
  const [message,setMessage] = useState("");

  const [orderForm,setOrderForm] = useState({productId:"",plantCode:"",orderType:"DISCRETE",quantity:"1",plannedStartDate:today(),plannedFinishDate:today()});
  const [bomForm,setBomForm] = useState({productId:"",plantCode:"",bomVersion:"1",items:[blankBomItem()]});
  const [routingForm,setRoutingForm] = useState({productId:"",plantCode:"",routingVersion:"1",operations:[blankRoutingOp()]});
  const [recipeForm,setRecipeForm] = useState({productId:"",plantCode:"",operations:[blankRecipeOp()]});
  const [mrpForm,setMrpForm] = useState({productId:"",plantCode:"",strategyGroup:"STANDARD",grossDemand:"",currentStock:"",safetyStock:"0",lotSize:"1"});
  const [capacityForm,setCapacityForm] = useState({workCenter:"",capacityDate:today(),availableMinutes:"480",plannedMinutes:"0"});
  const [actionForm,setActionForm] = useState({binId:"",issueMultiplier:"1",confirmQuantity:"1",scrapQuantity:"0",operation:"10",remarks:"",receiptQuantity:"1",scheduleDate:today()});
  const [signalTrigger,setSignalTrigger] = useState("MANUAL");

  const selectedOrder=orders.find(o=>o.id===selectedOrderId);
  const plantBins=useMemo(()=>bins,[bins]);

  async function load() {
    setError("");
    try {
      const [o,b,r,rec,c,k,s,m,mc,p,w] = await Promise.all([
        manufacturingApi.orders(), manufacturingApi.boms(), manufacturingApi.routings(), manufacturingApi.recipes(),
        manufacturingApi.capacity(), manufacturingApi.kanbanCycles(), manufacturingApi.kanbanSignals(),
        manufacturingApi.mrpPlans(), manufacturingApi.mrpComponents(), productsApi.active(), warehouseExecutionApi.warehouses()
      ]);
      setOrders(o); setBoms(b); setRoutings(r); setRecipes(rec); setCapacity(c); setCycles(k); setSignals(s);
      setMrpPlans(m); setMrpComponents(mc); setProducts(p); setWarehouses(w);
      if(!selectedOrderId && o[0]?.id) setSelectedOrderId(o[0].id);
    } catch(e:any) { setError(e?.message || "Unable to load manufacturing data."); }
  }
  useEffect(()=>{load()},[]);

  useEffect(()=>{
    if(!selectedOrder) return;
    const wh=warehouses.find(w=>w.code===selectedOrder.plantCode || w.name===selectedOrder.plantCode);
    if(!wh){setBins([]);return;}
    warehouseExecutionApi.bins(wh.id).then(setBins).catch(()=>setBins([]));
  },[selectedOrderId,selectedOrder?.plantCode,warehouses]);

  const run = async(fn:()=>Promise<any>, success:string) => {
    setBusy(true); setError(""); setMessage("");
    try { await fn(); setMessage(success); await load(); }
    catch(e:any){setError(e?.message || "Operation failed.");}
    finally{setBusy(false);}
  };

  const submitOrder=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.createOrder({...orderForm,quantity:Number(orderForm.quantity)}),"Production order created.");};
  const submitBom=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.createBom({
    product:{id:bomForm.productId},plantCode:bomForm.plantCode,bomVersion:bomForm.bomVersion,status:"ACTIVE",
    items:bomForm.items.map((x:any,i:number)=>({componentProduct:{id:x.componentProductId},quantity:Number(x.quantity),uom:x.uom,sequenceNo:i+1}))
  }), "BOM created.");};
  const submitRouting=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.createRouting({
    product:{id:routingForm.productId},plantCode:routingForm.plantCode,routingVersion:routingForm.routingVersion,status:"ACTIVE",
    operations:routingForm.operations.map((x:any,i:number)=>({operationNo:(i+1)*10,description:x.description,workCenter:x.workCenter,setupMinutes:Number(x.setupMinutes),runMinutes:Number(x.runMinutes)}))
  }), "Routing created.");};
  const submitRecipe=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.createRecipe({
    product:{id:recipeForm.productId},plantCode:recipeForm.plantCode,status:"ACTIVE",
    operations:recipeForm.operations.map((x:any,i:number)=>({operationNo:(i+1)*10,instruction:x.instruction,resourceName:x.resourceName,standardMinutes:Number(x.standardMinutes)}))
  }), "Master recipe created.");};
  const submitMrp=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.runMrp({
    productId:mrpForm.productId,plantCode:mrpForm.plantCode,strategyGroup:mrpForm.strategyGroup,
    grossDemand:mrpForm.grossDemand===""?null:Number(mrpForm.grossDemand),
    currentStock:mrpForm.currentStock===""?null:Number(mrpForm.currentStock),
    safetyStock:Number(mrpForm.safetyStock),lotSize:Number(mrpForm.lotSize)
  }), "MRP run completed.");};
  const submitCapacity=(e:FormEvent)=>{e.preventDefault();run(()=>manufacturingApi.saveCapacity({
    workCenter:capacityForm.workCenter,capacityDate:capacityForm.capacityDate,
    availableMinutes:Number(capacityForm.availableMinutes),plannedMinutes:Number(capacityForm.plannedMinutes)
  }),"Capacity saved.");};

  const executeOrder=async(kind:"release"|"issue"|"confirm"|"receipt"|"schedule"|"close")=>{
    if(!selectedOrder){setError("Select a production order first.");return;}
    if(kind==="release") return run(()=>manufacturingApi.release(selectedOrder.id),"Production order released.");
    if(kind==="close") return run(()=>manufacturingApi.close(selectedOrder.id),"Production order closed.");
    if(!actionForm.binId && (kind==="issue"||kind==="receipt")) {setError("Select a bin before inventory execution.");return;}
    if(kind==="issue") return run(()=>manufacturingApi.issue(selectedOrder.id,{binId:actionForm.binId,quantityMultiplier:Number(actionForm.issueMultiplier)}),"Components issued.");
    if(kind==="confirm") return run(()=>manufacturingApi.confirm(selectedOrder.id,{quantity:Number(actionForm.confirmQuantity),scrapQuantity:Number(actionForm.scrapQuantity),operation:actionForm.operation,remarks:actionForm.remarks}),"Production confirmed.");
    if(kind==="receipt") return run(()=>manufacturingApi.receipt(selectedOrder.id,{binId:actionForm.binId,quantity:Number(actionForm.receiptQuantity)}),"Finished goods received.");
    return run(()=>manufacturingApi.schedule(selectedOrder.id,actionForm.scheduleDate),"Production capacity scheduled.");
  };

  const signal=async(cycleId:string)=>run(()=>manufacturingApi.signalKanban(cycleId,signalTrigger),"Kanban replenishment signal posted.");

  const orderStatus=(s:string)=>s==="CLOSED"?"approved":s==="IN_PROCESS"||s==="RELEASED"?"ready":s==="CONFIRMED"?"approved":"draft";

  return <div className="content manufacturing-page">
    <div className="page-head manufacturing-head">
      <div>
        <div className="eyebrow">MANUFACTURING / PRODUCTION</div>
        <h1>Manufacturing</h1>
        <p>End-to-end manufacturing control: product structures, planning, production execution, capacity and Kanban replenishment.</p>
      </div>
      <div className="actions">
        <button className="btn" onClick={()=>load()} disabled={busy}>Refresh</button>
        <button className="btn primary" onClick={()=>setTab("production")}>Production execution</button>
      </div>
    </div>

    {(error||message) && <div className={error?"alert error":"alert"}>{error||message}</div>}

    <div className="manufacturing-stats">
      <div className="stat-card"><span className="stat-label">Production Orders</span><strong className="stat-value">{orders.length}</strong><span className="stat-foot">Live orders</span></div>
      <div className="stat-card"><span className="stat-label">Active BOMs</span><strong className="stat-value">{boms.length}</strong><span className="stat-foot">Product structures</span></div>
      <div className="stat-card"><span className="stat-label">Open MRP Plans</span><strong className="stat-value">{mrpPlans.length}</strong><span className="stat-foot">Planning runs</span></div>
      <div className="stat-card"><span className="stat-label">Kanban Signals</span><strong className="stat-value">{signals.length}</strong><span className="stat-foot">Replenishment history</span></div>
    </div>

    <div className="manufacturing-tabs">
      {([["overview","Overview"],["production","Production Orders"],["structures","BOM / Routing / Recipe"],["planning","MRP & Capacity"],["kanban","Kanban"]] as [Tab,string][]).map(([id,label])=>
        <button key={id} className={tab===id?"manufacturing-tab active":"manufacturing-tab"} onClick={()=>setTab(id)}>{label}</button>
      )}
    </div>

    {tab==="overview" && <div className="manufacturing-grid">
      <section className="card">
        <div className="section-title">Manufacturing process</div>
        <div className="manufacturing-process">
          {[
            ["01","BOM","Multi-level product structure",boms.length],
            ["02","MRP","Demand, stock and dependent requirements",mrpPlans.length],
            ["03","Order","Release and issue components",orders.filter(o=>["RELEASED","IN_PROCESS"].includes(o.status)).length],
            ["04","Confirm","Output and scrap confirmation",orders.filter(o=>Number(o.confirmedQuantity||0)>0).length],
            ["05","Receipt","Finished goods into stock/QM",orders.filter(o=>Number(o.receivedQuantity||0)>0).length],
            ["06","Close","Complete production order",orders.filter(o=>o.status==="CLOSED").length]
          ].map(x=><div className="manufacturing-process-step" key={x[0]}><b>{x[0]}</b><strong>{x[1]}</strong><span>{x[2]}</span><em>{x[3]}</em></div>)}
        </div>
      </section>
      <section className="card">
        <div className="section-title">Execution readiness</div>
        <div className="detail-grid">
          <div className="detail-field"><span>Routings</span><strong>{routings.length}</strong></div>
          <div className="detail-field"><span>Master recipes</span><strong>{recipes.length}</strong></div>
          <div className="detail-field"><span>Capacity records</span><strong>{capacity.length}</strong></div>
          <div className="detail-field"><span>Kanban cycles</span><strong>{cycles.length}</strong></div>
          <div className="detail-field"><span>Dependent MRP requirements</span><strong>{mrpComponents.length}</strong></div>
          <div className="detail-field"><span>Plants</span><strong>{new Set(orders.map(o=>o.plantCode).filter(Boolean)).size}</strong></div>
        </div>
      </section>
      <section className="card manufacturing-wide">
        <div className="section-title">Production order monitor <span className="section-meta">{orders.length} orders</span></div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Order</th><th>Product</th><th>Plant</th><th>Planned</th><th>Confirmed</th><th>Received</th><th>Status</th></tr></thead>
          <tbody>{orders.length?orders.slice(0,12).map(o=><tr key={o.id} onClick={()=>{setSelectedOrderId(o.id);setTab("production")}} className="manufacturing-click-row">
            <td><strong>{o.orderNumber||o.id}</strong></td><td>{productLabel(o.product)}</td><td>{o.plantCode||"—"}</td><td>{fmt(o.plannedQuantity)}</td><td>{fmt(o.confirmedQuantity)}</td><td>{fmt(o.receivedQuantity)}</td><td><span className={"status "+orderStatus(o.status)}>{o.status}</span></td>
          </tr>):<tr><td colSpan={7} className="empty">No production orders yet.</td></tr>}</tbody></table></div>
      </section>
    </div>}

    {tab==="production" && <div className="manufacturing-grid">
      <section className="card manufacturing-wide">
        <div className="section-title">Production orders <span className="section-meta">{orders.length} total</span></div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Order</th><th>Product</th><th>Plant</th><th>Type</th><th>Planned</th><th>Confirmed</th><th>Received</th><th>Status</th></tr></thead>
        <tbody>{orders.map(o=><tr key={o.id} onClick={()=>setSelectedOrderId(o.id)} className={selectedOrderId===o.id?"manufacturing-click-row selected-row":"manufacturing-click-row"}>
          <td><strong>{o.orderNumber||o.id}</strong></td><td>{productLabel(o.product)}</td><td>{o.plantCode}</td><td>{o.orderType}</td><td>{fmt(o.plannedQuantity)}</td><td>{fmt(o.confirmedQuantity)}</td><td>{fmt(o.receivedQuantity)}</td><td><span className={"status "+orderStatus(o.status)}>{o.status}</span></td>
        </tr>)}</tbody></table></div>
      </section>

      <section className="card">
        <div className="section-title">Create production order</div>
        <form className="manufacturing-form-grid" onSubmit={submitOrder}>
          <div className="form-field"><label>Product</label><select className="form-input" required value={orderForm.productId} onChange={e=>setOrderForm({...orderForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
          <div className="form-field"><label>Plant / warehouse code</label><input className="form-input" required value={orderForm.plantCode} onChange={e=>setOrderForm({...orderForm,plantCode:e.target.value})} placeholder="e.g. WH01"/></div>
          <div className="form-field"><label>Order type</label><select className="form-input" value={orderForm.orderType} onChange={e=>setOrderForm({...orderForm,orderType:e.target.value})}><option>DISCRETE</option><option>PROCESS</option><option>REPETITIVE</option><option>MRP</option><option>MRP_DEPENDENT</option><option>KANBAN</option></select></div>
          <div className="form-field"><label>Quantity</label><input className="form-input" type="number" min="0.0001" step="0.01" value={orderForm.quantity} onChange={e=>setOrderForm({...orderForm,quantity:e.target.value})}/></div>
          <div className="form-field"><label>Planned start</label><input className="form-input" type="date" value={orderForm.plannedStartDate} onChange={e=>setOrderForm({...orderForm,plannedStartDate:e.target.value})}/></div>
          <div className="form-field"><label>Planned finish</label><input className="form-input" type="date" value={orderForm.plannedFinishDate} onChange={e=>setOrderForm({...orderForm,plannedFinishDate:e.target.value})}/></div>
          <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Create order</button></div>
        </form>
      </section>

      <section className="card">
        <div className="section-title">Execution control {selectedOrder && <span className="section-meta">{selectedOrder.orderNumber}</span>}</div>
        {!selectedOrder?<div className="empty">Select a production order above.</div>:<>
          <div className="manufacturing-order-summary">
            <div><span>Product</span><strong>{productLabel(selectedOrder.product)}</strong></div><div><span>Status</span><strong>{selectedOrder.status}</strong></div>
            <div><span>Planned</span><strong>{fmt(selectedOrder.plannedQuantity)}</strong></div><div><span>Confirmed</span><strong>{fmt(selectedOrder.confirmedQuantity)}</strong></div>
            <div><span>Received</span><strong>{fmt(selectedOrder.receivedQuantity)}</strong></div><div><span>BOM</span><strong>{selectedOrder.bom?.bomNumber||"—"}</strong></div>
          </div>
          <div className="manufacturing-materials">
            <strong>Component requirements</strong>
            {(selectedOrder.materials||[]).length?<div className="table-wrap"><table className="table"><thead><tr><th>Component</th><th>Required</th><th>Issued</th><th>Remaining</th></tr></thead><tbody>{selectedOrder.materials.map((m:any)=><tr key={m.id}><td>{productLabel(m.componentProduct)}</td><td>{fmt(m.requiredQuantity)}</td><td>{fmt(m.issuedQuantity)}</td><td>{fmt(Number(m.requiredQuantity||0)-Number(m.issuedQuantity||0))}</td></tr>)}</tbody></table></div>:<span className="muted">No BOM components attached.</span>}
          </div>
          <div className="manufacturing-action-form">
            <div className="form-field"><label>Inventory bin</label><select className="form-input" value={actionForm.binId} onChange={e=>setActionForm({...actionForm,binId:e.target.value})}><option value="">Select bin</option>{plantBins.map((b:any)=><option key={b.id} value={b.id}>{b.code||b.name||b.id}</option>)}</select></div>
            <div className="form-field"><label>Issue multiplier</label><input className="form-input" type="number" min="0.01" step="0.01" value={actionForm.issueMultiplier} onChange={e=>setActionForm({...actionForm,issueMultiplier:e.target.value})}/></div>
            <div className="form-field"><label>Confirm quantity</label><input className="form-input" type="number" min="0.01" step="0.01" value={actionForm.confirmQuantity} onChange={e=>setActionForm({...actionForm,confirmQuantity:e.target.value})}/></div>
            <div className="form-field"><label>Scrap</label><input className="form-input" type="number" min="0" step="0.01" value={actionForm.scrapQuantity} onChange={e=>setActionForm({...actionForm,scrapQuantity:e.target.value})}/></div>
            <div className="form-field"><label>Operation</label><input className="form-input" value={actionForm.operation} onChange={e=>setActionForm({...actionForm,operation:e.target.value})}/></div>
            <div className="form-field"><label>Receipt quantity</label><input className="form-input" type="number" min="0.01" step="0.01" value={actionForm.receiptQuantity} onChange={e=>setActionForm({...actionForm,receiptQuantity:e.target.value})}/></div>
            <div className="form-field"><label>Schedule date</label><input className="form-input" type="date" value={actionForm.scheduleDate} onChange={e=>setActionForm({...actionForm,scheduleDate:e.target.value})}/></div>
            <div className="form-field manufacturing-action-wide"><label>Remarks</label><input className="form-input" value={actionForm.remarks} onChange={e=>setActionForm({...actionForm,remarks:e.target.value})}/></div>
          </div>
          <div className="actions manufacturing-order-actions">
            <button className="btn" disabled={busy||selectedOrder.status!=="CREATED"} onClick={()=>executeOrder("release")}>Release</button>
            <button className="btn" disabled={busy||!["RELEASED","IN_PROCESS"].includes(selectedOrder.status)} onClick={()=>executeOrder("issue")}>Issue components</button>
            <button className="btn" disabled={busy||!["RELEASED","IN_PROCESS"].includes(selectedOrder.status)} onClick={()=>executeOrder("confirm")}>Confirm production</button>
            <button className="btn" disabled={busy||!["IN_PROCESS","CONFIRMED"].includes(selectedOrder.status)} onClick={()=>executeOrder("receipt")}>Receive FG</button>
            <button className="btn" disabled={busy||!["IN_PROCESS","CONFIRMED"].includes(selectedOrder.status)} onClick={()=>executeOrder("schedule")}>Schedule capacity</button>
            <button className="btn primary" disabled={busy||selectedOrder.status!=="CONFIRMED"} onClick={()=>executeOrder("close")}>Close order</button>
          </div>
        </>}
      </section>
    </div>}

    {tab==="structures" && <div className="manufacturing-grid">
      <section className="card manufacturing-wide">
        <div className="section-title">BOM master data <span className="section-meta">{boms.length} records</span></div>
        <div className="manufacturing-master-layout">
          <form className="manufacturing-form-grid" onSubmit={submitBom}>
            <div className="form-field"><label>Product</label><select className="form-input" required value={bomForm.productId} onChange={e=>setBomForm({...bomForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
            <div className="form-field"><label>Plant</label><input className="form-input" required value={bomForm.plantCode} onChange={e=>setBomForm({...bomForm,plantCode:e.target.value})}/></div>
            <div className="form-field"><label>BOM version</label><input className="form-input" value={bomForm.bomVersion} onChange={e=>setBomForm({...bomForm,bomVersion:e.target.value})}/></div>
            <div className="manufacturing-nested-list"><div className="nested-head"><strong>Components</strong><button type="button" className="btn" onClick={()=>setBomForm({...bomForm,items:[...bomForm.items,blankBomItem()]})}>Add component</button></div>
              {bomForm.items.map((x:any,i:number)=><div className="nested-row" key={i}><select className="form-input" required value={x.componentProductId} onChange={e=>{const a=[...bomForm.items];a[i]={...a[i],componentProductId:e.target.value};setBomForm({...bomForm,items:a})}}><option value="">Component</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select><input className="form-input" type="number" min="0.0001" step="0.0001" value={x.quantity} onChange={e=>{const a=[...bomForm.items];a[i]={...a[i],quantity:e.target.value};setBomForm({...bomForm,items:a})}}/><input className="form-input" value={x.uom} onChange={e=>{const a=[...bomForm.items];a[i]={...a[i],uom:e.target.value};setBomForm({...bomForm,items:a})}} placeholder="UOM"/><button type="button" className="btn" disabled={bomForm.items.length===1} onClick={()=>setBomForm({...bomForm,items:bomForm.items.filter((_,n)=>n!==i)})}>Remove</button></div>)}
            </div>
            <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Create BOM</button></div>
          </form>
          <div className="table-wrap"><table className="table"><thead><tr><th>BOM</th><th>Product</th><th>Plant</th><th>Version</th><th>Components</th><th>Status</th></tr></thead><tbody>{boms.map(b=><tr key={b.id}><td>{b.bomNumber}</td><td>{productLabel(b.product)}</td><td>{b.plantCode}</td><td>{b.bomVersion||"—"}</td><td>{b.items?.length||0}</td><td>{b.status}</td></tr>)}</tbody></table></div>
        </div>
      </section>

      <section className="card">
        <div className="section-title">Routing</div>
        <form className="manufacturing-form-grid" onSubmit={submitRouting}>
          <div className="form-field"><label>Product</label><select className="form-input" required value={routingForm.productId} onChange={e=>setRoutingForm({...routingForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
          <div className="form-field"><label>Plant</label><input className="form-input" required value={routingForm.plantCode} onChange={e=>setRoutingForm({...routingForm,plantCode:e.target.value})}/></div>
          <div className="form-field"><label>Version</label><input className="form-input" value={routingForm.routingVersion} onChange={e=>setRoutingForm({...routingForm,routingVersion:e.target.value})}/></div>
          <div className="manufacturing-nested-list"><div className="nested-head"><strong>Operations</strong><button type="button" className="btn" onClick={()=>setRoutingForm({...routingForm,operations:[...routingForm.operations,blankRoutingOp()]})}>Add operation</button></div>
          {routingForm.operations.map((x:any,i:number)=><div className="nested-row routing-row" key={i}><input className="form-input" required placeholder="Description" value={x.description} onChange={e=>{const a=[...routingForm.operations];a[i]={...a[i],description:e.target.value};setRoutingForm({...routingForm,operations:a})}}/><input className="form-input" placeholder="Work center" value={x.workCenter} onChange={e=>{const a=[...routingForm.operations];a[i]={...a[i],workCenter:e.target.value};setRoutingForm({...routingForm,operations:a})}}/><input className="form-input" type="number" min="0" step="0.01" placeholder="Setup min" value={x.setupMinutes} onChange={e=>{const a=[...routingForm.operations];a[i]={...a[i],setupMinutes:e.target.value};setRoutingForm({...routingForm,operations:a})}}/><input className="form-input" type="number" min="0" step="0.01" placeholder="Run min/unit" value={x.runMinutes} onChange={e=>{const a=[...routingForm.operations];a[i]={...a[i],runMinutes:e.target.value};setRoutingForm({...routingForm,operations:a})}}/><button type="button" className="btn" disabled={routingForm.operations.length===1} onClick={()=>setRoutingForm({...routingForm,operations:routingForm.operations.filter((_,n)=>n!==i)})}>Remove</button></div>)}</div>
          <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Create routing</button></div>
        </form>
        <div className="table-wrap"><table className="table"><thead><tr><th>Routing</th><th>Product</th><th>Plant</th><th>Operations</th><th>Status</th></tr></thead><tbody>{routings.map(x=><tr key={x.id}><td>{x.routingNumber}</td><td>{productLabel(x.product)}</td><td>{x.plantCode}</td><td>{x.operations?.length||0}</td><td>{x.status}</td></tr>)}</tbody></table></div>
      </section>

      <section className="card">
        <div className="section-title">Master recipe</div>
        <form className="manufacturing-form-grid" onSubmit={submitRecipe}>
          <div className="form-field"><label>Product</label><select className="form-input" required value={recipeForm.productId} onChange={e=>setRecipeForm({...recipeForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
          <div className="form-field"><label>Plant</label><input className="form-input" required value={recipeForm.plantCode} onChange={e=>setRecipeForm({...recipeForm,plantCode:e.target.value})}/></div>
          <div className="manufacturing-nested-list"><div className="nested-head"><strong>Recipe operations</strong><button type="button" className="btn" onClick={()=>setRecipeForm({...recipeForm,operations:[...recipeForm.operations,blankRecipeOp()]})}>Add operation</button></div>
          {recipeForm.operations.map((x:any,i:number)=><div className="nested-row" key={i}><input className="form-input" required placeholder="Instruction" value={x.instruction} onChange={e=>{const a=[...recipeForm.operations];a[i]={...a[i],instruction:e.target.value};setRecipeForm({...recipeForm,operations:a})}}/><input className="form-input" placeholder="Resource" value={x.resourceName} onChange={e=>{const a=[...recipeForm.operations];a[i]={...a[i],resourceName:e.target.value};setRecipeForm({...recipeForm,operations:a})}}/><input className="form-input" type="number" min="0" step="0.01" value={x.standardMinutes} onChange={e=>{const a=[...recipeForm.operations];a[i]={...a[i],standardMinutes:e.target.value};setRecipeForm({...recipeForm,operations:a})}}/><button type="button" className="btn" disabled={recipeForm.operations.length===1} onClick={()=>setRecipeForm({...recipeForm,operations:recipeForm.operations.filter((_,n)=>n!==i)})}>Remove</button></div>)}</div>
          <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Create recipe</button></div>
        </form>
        <div className="table-wrap"><table className="table"><thead><tr><th>Recipe</th><th>Product</th><th>Plant</th><th>Operations</th><th>Status</th></tr></thead><tbody>{recipes.map(x=><tr key={x.id}><td>{x.recipeNumber}</td><td>{productLabel(x.product)}</td><td>{x.plantCode}</td><td>{x.operations?.length||0}</td><td>{x.status}</td></tr>)}</tbody></table></div>
      </section>
    </div>}

    {tab==="planning" && <div className="manufacturing-grid">
      <section className="card">
        <div className="section-title">Run MRP</div>
        <form className="manufacturing-form-grid" onSubmit={submitMrp}>
          <div className="form-field"><label>Product</label><select className="form-input" required value={mrpForm.productId} onChange={e=>setMrpForm({...mrpForm,productId:e.target.value})}><option value="">Select product</option>{products.map(p=><option key={p.id} value={p.id}>{productLabel(p)}</option>)}</select></div>
          <div className="form-field"><label>Plant</label><input className="form-input" required value={mrpForm.plantCode} onChange={e=>setMrpForm({...mrpForm,plantCode:e.target.value})}/></div>
          <div className="form-field"><label>Strategy group</label><input className="form-input" value={mrpForm.strategyGroup} onChange={e=>setMrpForm({...mrpForm,strategyGroup:e.target.value})}/></div>
          <div className="form-field"><label>Gross demand <small>(blank = open sales demand)</small></label><input className="form-input" type="number" min="0" step="0.01" value={mrpForm.grossDemand} onChange={e=>setMrpForm({...mrpForm,grossDemand:e.target.value})}/></div>
          <div className="form-field"><label>Current stock <small>(blank = plant stock)</small></label><input className="form-input" type="number" min="0" step="0.01" value={mrpForm.currentStock} onChange={e=>setMrpForm({...mrpForm,currentStock:e.target.value})}/></div>
          <div className="form-field"><label>Safety stock</label><input className="form-input" type="number" min="0" step="0.01" value={mrpForm.safetyStock} onChange={e=>setMrpForm({...mrpForm,safetyStock:e.target.value})}/></div>
          <div className="form-field"><label>Lot size</label><input className="form-input" type="number" min="0.01" step="0.01" value={mrpForm.lotSize} onChange={e=>setMrpForm({...mrpForm,lotSize:e.target.value})}/></div>
          <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Run MRP</button></div>
        </form>
      </section>
      <section className="card">
        <div className="section-title">MRP plans</div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Run</th><th>Product</th><th>Plant</th><th>Gross</th><th>Stock</th><th>Net</th><th>Planned</th><th>Generated order</th></tr></thead><tbody>{mrpPlans.map(x=><tr key={x.id}><td>{x.runTime?new Date(x.runTime).toLocaleString():"—"}</td><td>{productLabel(x.product)}</td><td>{x.plantCode}</td><td>{fmt(x.grossRequirement)}</td><td>{fmt(x.availableStock)}</td><td>{fmt(x.netRequirement)}</td><td>{fmt(x.plannedOrderQuantity)}</td><td>{x.generatedProductionOrderId||"—"}</td></tr>)}</tbody></table></div>
      </section>
      <section className="card manufacturing-wide">
        <div className="section-title">Dependent component requirements</div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Level</th><th>Component</th><th>Parent</th><th>Gross</th><th>Stock</th><th>Open supply</th><th>Net</th><th>Planned</th><th>Generated order</th></tr></thead><tbody>{mrpComponents.map(x=><tr key={x.id}><td>{x.bomLevel}</td><td>{productLabel(x.componentProduct)}</td><td>{x.parentProductId}</td><td>{fmt(x.grossRequirement)}</td><td>{fmt(x.availableStock)}</td><td>{fmt(x.openSupply)}</td><td>{fmt(x.netRequirement)}</td><td>{fmt(x.plannedOrderQuantity)}</td><td>{x.generatedProductionOrderId||"—"}</td></tr>)}</tbody></table></div>
      </section>
      <section className="card manufacturing-wide">
        <div className="section-title">Capacity master data & scheduling</div>
        <form className="manufacturing-form-grid" onSubmit={submitCapacity}>
          <div className="form-field"><label>Work center</label><input className="form-input" required value={capacityForm.workCenter} onChange={e=>setCapacityForm({...capacityForm,workCenter:e.target.value})}/></div>
          <div className="form-field"><label>Date</label><input className="form-input" type="date" required value={capacityForm.capacityDate} onChange={e=>setCapacityForm({...capacityForm,capacityDate:e.target.value})}/></div>
          <div className="form-field"><label>Available minutes</label><input className="form-input" type="number" min="0" value={capacityForm.availableMinutes} onChange={e=>setCapacityForm({...capacityForm,availableMinutes:e.target.value})}/></div>
          <div className="form-field"><label>Planned minutes</label><input className="form-input" type="number" min="0" value={capacityForm.plannedMinutes} onChange={e=>setCapacityForm({...capacityForm,plannedMinutes:e.target.value})}/></div>
          <div className="manufacturing-form-actions"><button className="btn primary" disabled={busy}>Save capacity</button></div>
        </form>
        <div className="table-wrap"><table className="table"><thead><tr><th>Work center</th><th>Date</th><th>Available min</th><th>Planned min</th><th>Remaining</th></tr></thead><tbody>{capacity.map(x=><tr key={x.id}><td>{x.workCenter}</td><td>{x.capacityDate}</td><td>{fmt(x.availableMinutes)}</td><td>{fmt(x.plannedMinutes)}</td><td>{fmt(Number(x.availableMinutes||0)-Number(x.plannedMinutes||0))}</td></tr>)}</tbody></table></div>
      </section>
    </div>}

    {tab==="kanban" && <div className="manufacturing-grid">
      <section className="card manufacturing-wide">
        <div className="section-title">Kanban control cycles</div>
        <div className="manufacturing-signal-toolbar">
          <div className="form-field"><label>Signal source</label><select className="form-input" value={signalTrigger} onChange={e=>setSignalTrigger(e.target.value)}><option>MANUAL</option><option>SHOP_FLOOR</option><option>REPLENISHMENT</option><option>SCAN</option></select></div>
        </div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Cycle</th><th>Product</th><th>Plant</th><th>Containers</th><th>Qty/container</th><th>Strategy</th><th>Action</th></tr></thead><tbody>{cycles.map(c=><tr key={c.id}><td><strong>{c.cycleCode}</strong></td><td>{productLabel(c.product)}</td><td>{c.plantCode}</td><td>{c.containers}</td><td>{fmt(c.containerQuantity)}</td><td>{c.replenishmentStrategy}</td><td><button className="btn mustard" disabled={busy} onClick={()=>signal(c.id)}>Signal replenishment</button></td></tr>)}</tbody></table></div>
      </section>
      <section className="card manufacturing-wide">
        <div className="section-title">Kanban signal history</div>
        <div className="table-wrap"><table className="table"><thead><tr><th>Cycle</th><th>Status</th><th>Trigger</th><th>Generated production order</th><th>Created</th></tr></thead><tbody>{signals.map(s=><tr key={s.id}><td>{s.controlCycle?.cycleCode||s.controlCycleId||"—"}</td><td>{s.status}</td><td>{s.triggerSource||"—"}</td><td>{s.generatedProductionOrderId||"—"}</td><td>{s.createdAt?new Date(s.createdAt).toLocaleString():"—"}</td></tr>)}</tbody></table></div>
      </section>
    </div>}
  </div>;
}