"use client";

import{useEffect,useState}from"react";
import{masterDataApi,warehouseExecutionApi}from"@/lib/api";

export default function StockCounts(){
 const[c,setC]=useState<any[]>([]),[w,setW]=useState<any[]>([]),[lines,setLines]=useState<any[]>([]),[warehouseId,setWarehouseId]=useState(""),[remarks,setRemarks]=useState(""),[selected,setSelected]=useState<any|null>(null),[busy,setBusy]=useState(false),[error,setError]=useState(""),[message,setMessage]=useState("");
 const load=()=>Promise.all([warehouseExecutionApi.stockCounts(),masterDataApi.warehouses()]).then(([a,b])=>{setC(a);setW(b);if(!warehouseId&&b[0]?.id)setWarehouseId(b[0].id)}).catch(e=>setError(e.message||"Unable to load stock counts."));
 useEffect(()=>{load()},[]);
 const act=async(fn:any,msg:string)=>{setBusy(true);setError("");setMessage("");try{await fn();setMessage(msg);await load()}catch(e:any){setError(e.message||"Operation failed.")}finally{setBusy(false)}};
 const open=async(x:any)=>{setSelected(x);setError("");try{const l=await warehouseExecutionApi.stockCountLines(x.id);setLines(l)}catch(e:any){setError(e.message||"Unable to load count lines.")}};
 const setCount=(id:string,value:string)=>setLines(lines.map(l=>l.id===id?{...l,countedQuantity:value}:l));
 const saveLine=async(l:any)=>{await warehouseExecutionApi.updateStockCountLine(l.id,{stockCountId:l.stockCountId||selected.id,productId:l.productId||l.product?.id,binId:l.binId||l.bin?.id,countedQuantity:Number(l.countedQuantity),reason:l.reason||"Physical count"});};
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">WAREHOUSE / PHYSICAL INVENTORY</div><h1>Stock Counts</h1><p>Create a count, load system balances, enter physical quantities, complete the count and generate the resulting adjustment.</p></div></div>
  {(error||message)&&<div className={error?"alert error":"alert success"}>{error||message}</div>}
  <section className="card"><div className="section-title">Create Stock Count</div><div className="warehouse-form-grid">
   <div className="form-field"><label>Warehouse</label><select className="form-input" value={warehouseId} onChange={e=>setWarehouseId(e.target.value)}><option value="">Select warehouse</option>{w.map(x=><option key={x.id} value={x.id}>{x.code} — {x.name}</option>)}</select></div>
   <div className="form-field"><label>Remarks</label><input className="form-input" value={remarks} onChange={e=>setRemarks(e.target.value)}/></div>
   <div className="warehouse-form-actions"><button className="btn primary" disabled={busy||!warehouseId} onClick={()=>act(()=>warehouseExecutionApi.createStockCount({warehouseId,remarks}),"Stock count created.")}>Create Count</button></div>
  </div></section>
  <section className="card" style={{marginTop:18}}><div className="table-wrap"><table className="table"><thead><tr><th>Count</th><th>Warehouse</th><th>Status</th><th>Created</th><th>Actions</th></tr></thead><tbody>
   {c.map(x=><tr key={x.id}><td><strong>{x.countNumber||x.stockCountNumber||x.id}</strong></td><td>{x.warehouseCode||x.warehouse?.code||"—"}</td><td>{x.status}</td><td>{x.createdAt||"—"}</td><td className="actions"><button className="btn" disabled={busy} onClick={()=>act(()=>warehouseExecutionApi.loadStockCount(x.id),"Inventory loaded.")}>Load Inventory</button><button className="btn" onClick={()=>open(x)}>Enter Count</button><button className="btn" disabled={busy} onClick={()=>act(()=>warehouseExecutionApi.completeStockCount(x.id),"Count completed.")}>Complete</button><button className="btn primary" disabled={busy} onClick={()=>act(()=>warehouseExecutionApi.generateStockAdjustment(x.id),"Adjustment generated.")}>Generate Adjustment</button></td></tr>)}
   {!c.length&&<tr><td colSpan={5} className="empty">No stock counts.</td></tr>}</tbody></table></div></section>
  {selected&&<section className="card" style={{marginTop:18}}><div className="section-title">Count Lines — {selected.countNumber||selected.id}</div><div className="table-wrap"><table className="table"><thead><tr><th>Product</th><th>Bin</th><th>System Qty</th><th>Counted Qty</th><th>Variance</th><th>Reason</th><th></th></tr></thead><tbody>
   {lines.map(l=>{const system=Number(l.systemQuantity||0),counted=l.countedQuantity==null?"":Number(l.countedQuantity);return <tr key={l.id}><td>{l.productSku||l.product?.sku||l.product?.name||"—"}</td><td>{l.binCode||l.bin?.code||"—"}</td><td>{system}</td><td><input className="form-input" style={{maxWidth:130}} type="number" min="0" step="0.01" value={counted} onChange={e=>setCount(l.id,e.target.value)}/></td><td>{counted===""?"—":(Number(counted)-system).toFixed(2)}</td><td>{l.reason||"Physical count"}</td><td><button className="btn" disabled={busy||counted===""} onClick={()=>act(()=>saveLine(l),"Count line saved.")}>Save</button></td></tr>})}
   {!lines.length&&<tr><td colSpan={7} className="empty">No lines loaded. Click Load Inventory first.</td></tr>}</tbody></table></div></section>}
 </main>;
}