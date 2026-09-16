"use client";
import {useEffect,useState} from "react";
import Link from "next/link";
import {orderToCashApi} from "@/lib/api";

const statusClass=(s:string)=>{const v=s.toLowerCase();if(v.includes("paid"))return"approved";if(v.includes("posted"))return"ready";if(v.includes("cancel"))return"blocked";return"pending"};
export default function CustomerInvoices(){
 const [rows,setRows]=useState<any[]>([]);const [loading,setLoading]=useState(true);const [busy,setBusy]=useState("");const [message,setMessage]=useState("");
 const load=()=>{setLoading(true);orderToCashApi.billingDocuments().then(setRows).catch(()=>setRows([])).finally(()=>setLoading(false));};useEffect(load,[]);
 const post=async(id:string)=>{setBusy(id);try{await orderToCashApi.postBillingDocument(id);load();}catch(e){setMessage(e instanceof Error?e.message:"Unable to post invoice.");}finally{setBusy("");}};
 return <div className="content"><div className="page-head"><div><div className="eyebrow">ORDER TO CASH / BILLING</div><h1>Customer Invoices</h1><p>Generate billing from outbound deliveries and post customer receivables.</p></div><Link className="btn primary" href="/order-to-cash/customer-invoices/new">Create Invoice</Link></div>{message&&<div className="alert error">{message}</div>}
 <section className="card"><div className="table-caption"><strong>Billing Documents ({rows.length})</strong><span>{loading?"Loading live billing documents":"Live backend records"}</span></div><div className="table-wrap"><table className="table"><thead><tr><th>Invoice</th><th>Customer</th><th>Delivery</th><th>Type</th><th>Amount</th><th>Date</th><th>Status</th><th>Action</th></tr></thead><tbody>{rows.map(r=>{const s=String(r.status||"");return <tr key={r.id}><td className="link">{r.billingNumber||r.id}</td><td>{r.customerCode||"—"}</td><td>{r.outboundDeliveryId||"—"}</td><td>{r.billingType||"—"}</td><td>{r.currency||"USD"} {Number(r.totalAmount||0).toFixed(2)}</td><td>{r.billingDate?new Date(r.billingDate).toLocaleDateString():"—"}</td><td><span className={`status ${statusClass(s)}`}>{s.replaceAll("_"," ")}</span></td><td>{s==="DRAFT"?<button className="btn" disabled={!!busy} onClick={()=>post(r.id)}>{busy===r.id?"Posting…":"Post"}</button>:<span className="muted">—</span>}</td></tr>})}{!rows.length&&!loading&&<tr><td colSpan={8} className="empty">No customer invoices found.</td></tr>}</tbody></table></div></section></div>;
}
