"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { financeApi, procurementApi, orderToCashApi, masterDataApi } from "@/lib/api";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function FinancePage() {
  const [accounts, setAccounts] = useState<any[]>([]);
  const [documents, setDocuments] = useState<any[]>([]);
  const [trialBalance, setTrialBalance] = useState<any[]>([]);
  const [vendorInvoices, setVendorInvoices] = useState<any[]>([]);
  const [vendorPayments, setVendorPayments] = useState<any[]>([]);
  const [billing, setBilling] = useState<any[]>([]);
  const [incoming, setIncoming] = useState<any[]>([]);
  const [inventoryValuation, setInventoryValuation] = useState<any[]>([]);
  const [inventoryReconciliation, setInventoryReconciliation] = useState<any | null>(null);
  const [apItems, setApItems] = useState<any[]>([]);
  const [arItems, setArItems] = useState<any[]>([]);
  const [apAgeing, setApAgeing] = useState<any | null>(null);
  const [arAgeing, setArAgeing] = useState<any | null>(null);
  const [warehouses, setWarehouses] = useState<any[]>([]);
  const [warehouseId, setWarehouseId] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    setLoading(true); setError("");
    try {
      const [a, d, tb, vi, vp, b, ip, iv, ir, ap, ar, apa, ara] = await Promise.all([
        financeApi.glAccounts(), financeApi.accountingDocuments(), financeApi.trialBalance(),
        procurementApi.vendorInvoices(), procurementApi.vendorPayments(), orderToCashApi.billingDocuments(), orderToCashApi.incomingPayments(),
        financeApi.inventoryValuation(warehouseId || undefined), financeApi.inventoryReconciliation(),
        financeApi.openItemsAp(), financeApi.openItemsAr(), financeApi.ageing("AP"), financeApi.ageing("AR")
      ]);
      setAccounts(a); setDocuments(d); setTrialBalance(tb); setVendorInvoices(vi); setVendorPayments(vp); setBilling(b); setIncoming(ip); setInventoryValuation(iv); setInventoryReconciliation(ir);
      setApItems(ap); setArItems(ar); setApAgeing(apa); setArAgeing(ara);
    } catch (e) { setError(e instanceof Error ? e.message : "Unable to load finance workspace."); }
    finally { setLoading(false); }
  }
  useEffect(() => { masterDataApi.warehouses().then(setWarehouses).catch(()=>setWarehouses([])); }, []);
  useEffect(() => { load(); }, [warehouseId]);

  const apOpen = useMemo(() => vendorInvoices.filter(i => ["MATCHED", "POSTED"].includes(i.status)).reduce((s, i) => s + Number(i.totalAmount || 0), 0) - vendorPayments.filter(p => p.status === "PAID").reduce((s, p) => s + Number(p.amount || 0), 0), [vendorInvoices, vendorPayments]);
  const arOpen = useMemo(() => billing.filter(i => i.status === "POSTED").reduce((s, i) => s + Number(i.totalAmount || 0), 0) - incoming.reduce((s, p) => s + Number(p.appliedAmount || p.amount || 0), 0), [billing, incoming]);
  const inventoryValue = useMemo(() => inventoryValuation.reduce((s, i) => s + Number(i.inventoryValue || 0), 0), [inventoryValuation]);
  const posted = documents.filter(d => d.status === "POSTED");
  const debitTotal = trialBalance.reduce((s, x) => s + Number(x.debitBalance || 0), 0);
  const creditTotal = trialBalance.reduce((s, x) => s + Number(x.creditBalance || 0), 0);

  return <main className="page">
    <div className="page-head"><div><div className="eyebrow">FINANCE / FI</div><h1>Finance & Accounting</h1><p>AP, AR, GR/IR, clearing, inventory valuation and accounting documents.</p></div><button className="btn" onClick={load}>Refresh</button></div>
    {error && <div className="alert error">{error}</div>}
    <div className="stats-grid">
      <div className="stat-card"><span>Accounts Payable</span><strong>{apOpen.toFixed(2)}</strong><small>Open vendor exposure</small></div>
      <div className="stat-card"><span>Accounts Receivable</span><strong>{arOpen.toFixed(2)}</strong><small>Open customer exposure</small></div>
      <div className="stat-card"><span>Inventory Value</span><strong>{inventoryValue.toFixed(2)}</strong><small>On-hand quantity × standard cost</small></div>
      <div className="stat-card"><span>Posted FI Documents</span><strong>{posted.length}</strong><small>Accounting documents</small></div>
    </div>

    <section className="card form-card" style={{ marginBottom: 24 }}>
      <div className="section-title">Accounting Context</div>
      <div className="form-grid">
        <div className="form-field"><label>Company Code</label><input className="form-input" value="ZW01" readOnly /></div>
        <div className="form-field"><label>Valuation Warehouse</label><select className="form-input" value={warehouseId} onChange={e=>setWarehouseId(e.target.value)}><option value="">All warehouses</option>{warehouses.filter(x=>x.active!==false).map(x=><option key={x.id} value={x.id}>{x.code} — {x.name}</option>)}</select></div>
        <div className="form-field"><label>Ledger</label><input className="form-input" value="0L — Leading Ledger" readOnly /></div>
      </div>
    </section>

    <div className="module-grid">
      <Link className="module-card" href="/procurement/vendor-invoices"><b>Accounts Payable</b><span>Invoice verification, matching and blocked invoices</span></Link>
      <Link className="module-card" href="/order-to-cash/accounts-receivable"><b>Accounts Receivable</b><span>Customer open items, incoming payments and clearing</span></Link>
      <Link className="module-card" href="/procurement/gr-ir-reconciliation"><b>GR/IR Reconciliation</b><span>Review received, invoiced and outstanding procurement value</span></Link>
      <div className="module-card"><b>Inventory Accounting</b><span>PGI posts COGS and inventory consumption; billing posts AR and revenue.</span></div>
    </div>

    {inventoryReconciliation && <div className="module-card" style={{ marginBottom: 24 }}>
      <b>Inventory to GL Reconciliation</b>
      <span>Inventory valuation: {Number(inventoryReconciliation.inventoryValuation || 0).toFixed(2)} · GL 110000: {Number(inventoryReconciliation.inventoryGlBalance || 0).toFixed(2)} · Variance: {Number(inventoryReconciliation.variance || 0).toFixed(2)} · {inventoryReconciliation.balanced ? "BALANCED" : "VARIANCE REQUIRES REVIEW"}</span>
    </div>}

    <div className="module-grid">
      <div className="module-card"><b>AP Ageing</b><span>Current {Number(apAgeing?.current||0).toFixed(2)} · 1–30 {Number(apAgeing?.days1To30||0).toFixed(2)} · 31–60 {Number(apAgeing?.days31To60||0).toFixed(2)} · 61–90 {Number(apAgeing?.days61To90||0).toFixed(2)} · 91+ {Number(apAgeing?.days91Plus||0).toFixed(2)}</span></div>
      <div className="module-card"><b>AR Ageing</b><span>Current {Number(arAgeing?.current||0).toFixed(2)} · 1–30 {Number(arAgeing?.days1To30||0).toFixed(2)} · 31–60 {Number(arAgeing?.days31To60||0).toFixed(2)} · 61–90 {Number(arAgeing?.days61To90||0).toFixed(2)} · 91+ {Number(arAgeing?.days91Plus||0).toFixed(2)}</span></div>
    </div>

    <ModuleWorkspace title="Accounts Payable — Open Items" subtitle={`${apItems.length} supplier open items`} searchPlaceholder="Search supplier or invoice..." columns={[{key:"referenceNumber",label:"Invoice"},{key:"partyName",label:"Supplier"},{key:"documentDate",label:"Document Date"},{key:"dueDate",label:"Due Date"},{key:"originalAmount",label:"Original"},{key:"clearedAmount",label:"Cleared"},{key:"openAmount",label:"Open"},{key:"status",label:"Status"}]} rows={apItems} loading={loading} />
    <ModuleWorkspace title="Accounts Receivable — Open Items" subtitle={`${arItems.length} customer open items`} searchPlaceholder="Search customer or billing..." columns={[{key:"referenceNumber",label:"Billing"},{key:"partyName",label:"Customer"},{key:"documentDate",label:"Document Date"},{key:"dueDate",label:"Due Date"},{key:"originalAmount",label:"Original"},{key:"clearedAmount",label:"Cleared"},{key:"openAmount",label:"Open"},{key:"status",label:"Status"}]} rows={arItems} loading={loading} />

    <ModuleWorkspace title="Chart of Accounts" subtitle={`${accounts.length} configured GL accounts`} searchPlaceholder="Search accounts..." columns={[{key:"accountCode",label:"Account"},{key:"accountName",label:"Name"},{key:"accountType",label:"Type"},{key:"controlAccount",label:"Control"}]} rows={accounts} loading={loading} />
    <ModuleWorkspace title="Inventory Valuation" subtitle={`${inventoryValuation.length} warehouse/product balances`} searchPlaceholder="Search product or warehouse..." columns={[{key:"warehouseCode",label:"Warehouse"},{key:"sku",label:"SKU"},{key:"productName",label:"Product"},{key:"quantityOnHand",label:"Qty On Hand"},{key:"unitCost",label:"Unit Cost"},{key:"inventoryValue",label:"Inventory Value"}]} rows={inventoryValuation} loading={loading} />
    <ModuleWorkspace title="Accounting Documents" subtitle="Posted finance document flow" searchPlaceholder="Search FI documents..." columns={[{key:"documentNumber",label:"Document"},{key:"documentType",label:"Type"},{key:"referenceNumber",label:"Reference"},{key:"postingDate",label:"Posting Date"},{key:"currency",label:"Currency"},{key:"totalDebit",label:"Debit"},{key:"totalCredit",label:"Credit"},{key:"status",label:"Status"}]} rows={documents} loading={loading} />
    <ModuleWorkspace title="Trial Balance" subtitle={`Posted ledger balances · Debit ${debitTotal.toFixed(2)} / Credit ${creditTotal.toFixed(2)}`} searchPlaceholder="Search GL account..." columns={[{key:"accountCode",label:"Account"},{key:"accountName",label:"Name"},{key:"debitBalance",label:"Debit"},{key:"creditBalance",label:"Credit"}]} rows={trialBalance} loading={loading} />
  </main>;
}
