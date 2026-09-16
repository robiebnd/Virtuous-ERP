"use client";

import Link from "next/link";
import {useState} from "react";
import {procurementApi} from "@/lib/api";

export default function NewVendorPayment(){
  const [form,setForm]=useState({vendorInvoiceId:"",amount:"",currency:"USD",paymentMethod:"BANK_TRANSFER",reference:"",remarks:""});
  const [saving,setSaving]=useState(false); const [message,setMessage]=useState("");
  const set=(key:keyof typeof form,value:string)=>setForm(v=>({...v,[key]:value}));
  const submit=async()=>{setSaving(true);setMessage("");try{const result=await procurementApi.createVendorPayment({...form,amount:Number(form.amount)});setMessage(`Payment ${result.paymentNumber||""} created as DRAFT.`);setForm(v=>({...v,vendorInvoiceId:"",amount:"",reference:"",remarks:""}));}catch(e){setMessage(e instanceof Error?e.message:"Unable to create vendor payment.");}finally{setSaving(false);}};
  return <div className="content"><div className="document-head"><div><div className="eyebrow">PROCUREMENT / ACCOUNTS PAYABLE</div><h1>Create Vendor Payment</h1><p>Create a payment proposal only against a matched or posted vendor invoice.</p></div><div className="actions"><Link className="btn" href="/procurement/vendor-payments">Cancel</Link><button className="btn primary" disabled={saving||!form.vendorInvoiceId||!form.amount} onClick={submit}>{saving?"Creating…":"Create Payment"}</button></div></div>
    {message&&<div className="alert">{message}</div>}
    <section className="card form-card"><div className="section-title">Payment Details</div><div className="form-grid">
      <div className="form-field"><label>Vendor Invoice ID *</label><input className="form-input" value={form.vendorInvoiceId} onChange={e=>set("vendorInvoiceId",e.target.value)} placeholder="UUID of matched vendor invoice" /></div>
      <div className="form-field"><label>Amount *</label><input className="form-input" type="number" min="0.01" step="0.01" value={form.amount} onChange={e=>set("amount",e.target.value)} placeholder="0.00" /></div>
      <div className="form-field"><label>Currency</label><select className="form-input" value={form.currency} onChange={e=>set("currency",e.target.value)}><option>USD</option><option>ZWL</option></select></div>
      <div className="form-field"><label>Payment Method *</label><select className="form-input" value={form.paymentMethod} onChange={e=>set("paymentMethod",e.target.value)}><option>BANK_TRANSFER</option><option>CASH</option><option>CHEQUE</option><option>MOBILE_MONEY</option></select></div>
      <div className="form-field"><label>Reference</label><input className="form-input" value={form.reference} onChange={e=>set("reference",e.target.value)} placeholder="Bank or payment reference" /></div>
    </div></section>
    <section className="card"><div className="section-title">Remarks</div><div style={{padding:22}}><textarea className="form-input" style={{height:100,width:"100%",paddingTop:10}} value={form.remarks} onChange={e=>set("remarks",e.target.value)} placeholder="Payment notes" /></div></section>
  </div>;
}
