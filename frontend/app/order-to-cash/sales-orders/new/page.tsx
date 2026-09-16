"use client";

import Link from "next/link";
import {useState} from "react";
import {orderToCashApi} from "@/lib/api";

type Item={materialCode:string;quantity:string;unitPrice:string};

export default function NewSalesOrder(){
  const [form,setForm]=useState({customerCode:"",salesOrganization:"1000",distributionChannel:"10",division:"00",remarks:""});
  const [items,setItems]=useState<Item[]>([{materialCode:"",quantity:"1",unitPrice:"0"}]);
  const [saving,setSaving]=useState(false); const [message,setMessage]=useState("");
  const set=(key:keyof typeof form,value:string)=>setForm(v=>({...v,[key]:value}));
  const setItem=(index:number,key:keyof Item,value:string)=>setItems(v=>v.map((item,i)=>i===index?{...item,[key]:value}:item));
  const addItem=()=>setItems(v=>[...v,{materialCode:"",quantity:"1",unitPrice:"0"}]);
  const removeItem=(index:number)=>setItems(v=>v.length===1?v:v.filter((_,i)=>i!==index));
  const submit=async()=>{setSaving(true);setMessage("");try{const result=await orderToCashApi.createSalesOrder({customerCode:form.customerCode,salesOrganization:form.salesOrganization,distributionChannel:form.distributionChannel,division:form.division,remarks:form.remarks,items:items.map(i=>({materialCode:i.materialCode,quantity:Number(i.quantity),unitPrice:Number(i.unitPrice)}))});setMessage(`Sales order ${result.orderNumber||""} created with status ${result.status||"DRAFT"}.`);}catch(e){setMessage(e instanceof Error?e.message:"Unable to create sales order.");}finally{setSaving(false);}};
  const total=items.reduce((sum,i)=>sum+(Number(i.quantity)||0)*(Number(i.unitPrice)||0),0);
  return <div className="content"><div className="document-head"><div><div className="eyebrow">ORDER TO CASH / SALES ORDER</div><h1>Create Sales Order</h1><p>Create a customer order with commercial area data and order items. The backend can optionally hand the order to SAP.</p></div><div className="actions"><Link className="btn" href="/order-to-cash/sales-orders">Cancel</Link><button className="btn primary" disabled={saving||!form.customerCode||items.some(i=>!i.materialCode||Number(i.quantity)<=0)} onClick={submit}>{saving?"Creating…":"Create Sales Order"}</button></div></div>
    {message&&<div className="alert">{message}</div>}
    <section className="card form-card"><div className="section-title">Sales Area & Customer</div><div className="form-grid">
      <div className="form-field"><label>Customer Code *</label><input className="form-input" value={form.customerCode} onChange={e=>set("customerCode",e.target.value)} placeholder="Customer code" /></div>
      <div className="form-field"><label>Sales Organization *</label><input className="form-input" value={form.salesOrganization} onChange={e=>set("salesOrganization",e.target.value)} /></div>
      <div className="form-field"><label>Distribution Channel *</label><input className="form-input" value={form.distributionChannel} onChange={e=>set("distributionChannel",e.target.value)} /></div>
      <div className="form-field"><label>Division *</label><input className="form-input" value={form.division} onChange={e=>set("division",e.target.value)} /></div>
    </div></section>
    <section className="card form-card"><div className="section-title"><span>Order Items</span><button className="btn" onClick={addItem}>Add Item</button></div><div className="table-wrap"><table className="table"><thead><tr><th>Item</th><th>Material Code *</th><th>Quantity *</th><th>Unit Price</th><th>Net Value</th><th></th></tr></thead><tbody>{items.map((item,index)=><tr key={index}><td>{(index+1)*10}</td><td><input className="form-input" value={item.materialCode} onChange={e=>setItem(index,"materialCode",e.target.value)} placeholder="Material / SKU" /></td><td><input className="form-input" type="number" min="0.001" step="0.001" value={item.quantity} onChange={e=>setItem(index,"quantity",e.target.value)} /></td><td><input className="form-input" type="number" min="0" step="0.01" value={item.unitPrice} onChange={e=>setItem(index,"unitPrice",e.target.value)} /></td><td>{((Number(item.quantity)||0)*(Number(item.unitPrice)||0)).toFixed(2)}</td><td><button className="btn" onClick={()=>removeItem(index)} disabled={items.length===1}>Remove</button></td></tr>)}</tbody></table></div><div style={{padding:"14px 18px",textAlign:"right",fontWeight:650}}>Order Total: USD {total.toFixed(2)}</div></section>
    <section className="card"><div className="section-title">Notes</div><div style={{padding:22}}><textarea className="form-input" style={{height:100,width:"100%",paddingTop:10}} value={form.remarks} onChange={e=>set("remarks",e.target.value)} placeholder="Internal notes" /></div></section>
  </div>;
}
