"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { customerApi } from "@/lib/api";

type CustomerForm = {
  customerNumber:string; name:string; email:string; phone:string;
  billingAddress:string; shippingAddress:string; paymentTerms:string;
  creditLimit:string; creditBlocked:boolean;
};

export default function NewCustomer() {
  const router = useRouter();
  const [saving,setSaving] = useState(false);
  const [error,setError] = useState("");
  const [form,setForm] = useState<CustomerForm>({
    customerNumber:"", name:"", email:"", phone:"", billingAddress:"",
    shippingAddress:"", paymentTerms:"30 DAYS", creditLimit:"", creditBlocked:false
  });

  const set = <K extends keyof CustomerForm>(key:K,value:CustomerForm[K]) =>
    setForm(v=>({...v,[key]:value}));

  async function submit(e:FormEvent) {
    e.preventDefault();
    setSaving(true); setError("");
    try {
      await customerApi.create({...form, creditLimit:form.creditLimit ? Number(form.creditLimit) : null});
      router.push("/order-to-cash/customers");
    } catch (err:any) {
      setError(err?.message || "Unable to create customer.");
    } finally { setSaving(false); }
  }

  return (
    <div className="content customer-form-page">
      <div className="page-head">
        <div>
          <div className="eyebrow">ORDER TO CASH / MASTER DATA</div>
          <h1>Create Customer</h1>
          <p>Create a customer master record for sales, delivery, billing and receivables.</p>
        </div>
      </div>

      <form className="card customer-form-card" onSubmit={submit}>
        <div className="customer-form-section">
          <div className="section-title">Customer account</div>
          <div className="customer-form-grid">
            <div className="form-field">
              <label htmlFor="customerNumber">Customer Number</label>
              <input id="customerNumber" className="form-input" value={form.customerNumber}
                onChange={e=>set("customerNumber",e.target.value)} placeholder="Auto-generated if blank" />
            </div>
            <div className="form-field">
              <label htmlFor="customerName">Customer Name</label>
              <input id="customerName" className="form-input" required value={form.name}
                onChange={e=>set("name",e.target.value)} />
            </div>
            <div className="form-field">
              <label htmlFor="customerEmail">Email</label>
              <input id="customerEmail" className="form-input" type="email" value={form.email}
                onChange={e=>set("email",e.target.value)} />
            </div>
            <div className="form-field">
              <label htmlFor="customerPhone">Phone</label>
              <input id="customerPhone" className="form-input" value={form.phone}
                onChange={e=>set("phone",e.target.value)} />
            </div>
            <div className="form-field">
              <label htmlFor="paymentTerms">Payment Terms</label>
              <select id="paymentTerms" className="form-input" value={form.paymentTerms}
                onChange={e=>set("paymentTerms",e.target.value)}>
                <option>COD</option><option>30 DAYS</option><option>60 DAYS</option><option>90 DAYS</option>
              </select>
            </div>
            <div className="form-field">
              <label htmlFor="creditLimit">Credit Limit</label>
              <input id="creditLimit" className="form-input" type="number" min="0" step="0.01"
                value={form.creditLimit} onChange={e=>set("creditLimit",e.target.value)} placeholder="0.00" />
            </div>
          </div>
        </div>

        <div className="customer-form-section">
          <div className="section-title">Addresses & credit control</div>
          <div className="customer-form-grid customer-address-grid">
            <div className="form-field">
              <label htmlFor="billingAddress">Billing Address</label>
              <textarea id="billingAddress" className="form-input customer-textarea"
                value={form.billingAddress} onChange={e=>set("billingAddress",e.target.value)} rows={4}/>
            </div>
            <div className="form-field">
              <label htmlFor="shippingAddress">Shipping Address</label>
              <textarea id="shippingAddress" className="form-input customer-textarea"
                value={form.shippingAddress} onChange={e=>set("shippingAddress",e.target.value)} rows={4}/>
            </div>
            <label className="customer-checkbox">
              <input type="checkbox" checked={form.creditBlocked}
                onChange={e=>set("creditBlocked",e.target.checked)} />
              <span>Credit blocked</span>
            </label>
          </div>
        </div>

        {error && <div className="customer-form-error">{error}</div>}

        <div className="customer-form-actions">
          <button type="button" className="btn" onClick={()=>router.back()}>Cancel</button>
          <button type="submit" className="btn primary" disabled={saving}>
            {saving ? "Saving…" : "Create Customer"}
          </button>
        </div>
      </form>
    </div>
  );
}