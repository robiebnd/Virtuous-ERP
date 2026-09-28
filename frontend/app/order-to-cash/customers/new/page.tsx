"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { customerApi } from "@/lib/api";

export default function NewCustomer() {
  const router = useRouter();
  const [saving,setSaving] = useState(false);
  const [error,setError] = useState("");
  const [form,setForm] = useState({
    customerNumber:"", name:"", email:"", phone:"", billingAddress:"", shippingAddress:"",
    paymentTerms:"30 DAYS", creditLimit:"", creditBlocked:false
  });

  const set = (key:string,value:any) => setForm(v=>({...v,[key]:value}));

  async function submit(e:FormEvent) {
    e.preventDefault(); setSaving(true); setError("");
    try {
      await customerApi.create({...form, creditLimit:form.creditLimit ? Number(form.creditLimit) : null});
      router.push("/order-to-cash/customers");
    } catch (err:any) {
      setError(err?.message || "Unable to create customer.");
    } finally { setSaving(false); }
  }

  return <div className="content">
    <div className="page-head">
      <div><div className="eyebrow">ORDER TO CASH / MASTER DATA</div><h1>Create Customer</h1><p>Create a customer master record for sales, delivery, billing and receivables.</p></div>
    </div>
    <form className="card" onSubmit={submit}>
      {error && <div className="status blocked" style={{marginBottom:16}}>{error}</div>}
      <div className="form-grid">
        <label>Customer Number<input className="filter" value={form.customerNumber} onChange={e=>set("customerNumber",e.target.value)} placeholder="Auto-generated if blank" /></label>
        <label>Customer Name<input className="filter" required value={form.name} onChange={e=>set("name",e.target.value)} /></label>
        <label>Email<input className="filter" type="email" value={form.email} onChange={e=>set("email",e.target.value)} /></label>
        <label>Phone<input className="filter" value={form.phone} onChange={e=>set("phone",e.target.value)} /></label>
        <label>Payment Terms<select className="filter" value={form.paymentTerms} onChange={e=>set("paymentTerms",e.target.value)}><option>COD</option><option>30 DAYS</option><option>60 DAYS</option><option>90 DAYS</option></select></label>
        <label>Credit Limit<input className="filter" type="number" min="0" step="0.01" value={form.creditLimit} onChange={e=>set("creditLimit",e.target.value)} /></label>
        <label className="full">Billing Address<textarea className="filter" value={form.billingAddress} onChange={e=>set("billingAddress",e.target.value)} rows={3}/></label>
        <label className="full">Shipping Address<textarea className="filter" value={form.shippingAddress} onChange={e=>set("shippingAddress",e.target.value)} rows={3}/></label>
        <label className="checkbox"><input type="checkbox" checked={form.creditBlocked} onChange={e=>set("creditBlocked",e.target.checked)} /> Credit blocked</label>
      </div>
      <div className="actions" style={{marginTop:20}}>
        <button type="button" className="btn" onClick={()=>router.back()}>Cancel</button>
        <button type="submit" className="btn primary" disabled={saving}>{saving ? "Saving…" : "Create Customer"}</button>
      </div>
    </form>
  </div>;
}