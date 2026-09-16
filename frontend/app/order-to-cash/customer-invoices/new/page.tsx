"use client";

import Link from "next/link";
import {useState} from "react";
import {orderToCashApi} from "@/lib/api";

export default function NewCustomerInvoice(){
 const [deliveryId,setDeliveryId]=useState("");const [currency,setCurrency]=useState("USD");const [dueDate,setDueDate]=useState("");const [message,setMessage]=useState("");const [saving,setSaving]=useState(false);
 const submit=async()=>{setSaving(true);setMessage("");try{const r=await orderToCashApi.createBillingDocument({outboundDeliveryId:deliveryId,currency,dueDate:dueDate?`${dueDate}T00:00:00`:null});setMessage(`Billing document ${r.billingNumber||""} created as ${r.status||"DRAFT"}.`);}catch(e){setMessage(e instanceof Error?e.message:"Unable to create customer invoice.");}finally{setSaving(false);}};
 return <div className="content"><div className="document-head"><div><div className="eyebrow">ORDER TO CASH / BILLING</div><h1>Create Customer Invoice</h1><p>Create a billing document from an outbound delivery. Posting creates the customer billing document in the backend.</p></div><div className="actions"><Link className="btn" href="/order-to-cash/customer-invoices">Cancel</Link><button className="btn primary" disabled={saving||!deliveryId} onClick={submit}>{saving?"Creating…":"Create Invoice"}</button></div></div>{message&&<div className="alert">{message}</div>}
 <section className="card form-card"><div className="section-title">Billing Reference</div><div className="form-grid"><div className="form-field"><label>Outbound Delivery ID *</label><input className="form-input" value={deliveryId} onChange={e=>setDeliveryId(e.target.value)} placeholder="UUID of completed delivery" /></div><div className="form-field"><label>Currency *</label><select className="form-input" value={currency} onChange={e=>setCurrency(e.target.value)}><option>USD</option><option>ZWL</option></select></div><div className="form-field"><label>Due Date</label><input className="form-input" type="date" value={dueDate} onChange={e=>setDueDate(e.target.value)} /></div></div></section>
 <section className="card"><div className="section-title">Document Flow</div><div className="detail-grid"><div className="detail-field"><span>Source</span><strong>Outbound Delivery</strong></div><div className="detail-field"><span>Result</span><strong>Customer Billing Document</strong></div><div className="detail-field"><span>Financial hand-off</span><strong>Customer Receivable</strong></div></div></section></div>;
}
