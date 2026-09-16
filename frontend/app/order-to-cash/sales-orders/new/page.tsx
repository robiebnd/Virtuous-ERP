"use client";

import Link from "next/link";
import { useState } from "react";

export default function NewSalesOrder() {
  const [saved, setSaved] = useState(false);
  const [form, setForm] = useState({ customer: "", orderDate: "2026-09-16", deliveryDate: "", currency: "USD", paymentTerms: "30 DAYS", warehouse: "MAIN", reference: "", remarks: "" });
  const set = (key: keyof typeof form, value: string) => setForm((current) => ({ ...current, [key]: value }));
  const saveDraft = () => { localStorage.setItem("virtuous_sales_order_draft", JSON.stringify(form)); setSaved(true); };
  return <div className="content">
    <div className="document-head"><div><div className="eyebrow">ORDER TO CASH / SALES ORDER</div><h1>Create Sales Order</h1><p>Enter the customer and commercial requirements before adding order items and releasing the order.</p></div><div className="actions"><Link className="btn" href="/order-to-cash/sales-orders">Cancel</Link><button className="btn primary" onClick={saveDraft}>Save Draft</button></div></div>
    {saved && <div className="alert">Sales order draft saved locally. Add item-entry and backend posting when the sales-order API is enabled.</div>}
    <section className="card form-card"><div className="section-title">General Information</div><div className="form-grid">
      <div className="form-field"><label>Customer *</label><input className="form-input" value={form.customer} onChange={(e)=>set("customer",e.target.value)} placeholder="Select customer" /></div>
      <div className="form-field"><label>Order Date *</label><input className="form-input" type="date" value={form.orderDate} onChange={(e)=>set("orderDate",e.target.value)} /></div>
      <div className="form-field"><label>Requested Delivery Date</label><input className="form-input" type="date" value={form.deliveryDate} onChange={(e)=>set("deliveryDate",e.target.value)} /></div>
      <div className="form-field"><label>Currency</label><select className="form-input" value={form.currency} onChange={(e)=>set("currency",e.target.value)}><option>USD</option><option>ZWL</option></select></div>
      <div className="form-field"><label>Payment Terms</label><select className="form-input" value={form.paymentTerms} onChange={(e)=>set("paymentTerms",e.target.value)}><option>COD</option><option>30 DAYS</option><option>60 DAYS</option><option>90 DAYS</option></select></div>
      <div className="form-field"><label>Delivering Warehouse</label><select className="form-input" value={form.warehouse} onChange={(e)=>set("warehouse",e.target.value)}><option>MAIN</option><option>S001</option></select></div>
      <div className="form-field"><label>Customer Reference</label><input className="form-input" value={form.reference} onChange={(e)=>set("reference",e.target.value)} placeholder="Customer PO / reference" /></div>
    </div></section>
    <section className="card form-card"><div className="section-title">Order Items</div><div className="empty">Item entry will be connected to the product, pricing and inventory APIs in the next integration step.</div></section>
    <section className="card"><div className="section-title">Notes</div><div style={{padding:22}}><textarea className="form-input" style={{height:100,width:"100%",paddingTop:10}} value={form.remarks} onChange={(e)=>set("remarks",e.target.value)} placeholder="Internal notes" /></div></section>
  </div>;
}