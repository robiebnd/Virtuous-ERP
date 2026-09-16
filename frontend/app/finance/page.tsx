"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { financeApi, procurementApi, orderToCashApi } from "@/lib/api";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function FinancePage() {
  const [accounts, setAccounts] = useState<any[]>([]);
  const [documents, setDocuments] = useState<any[]>([]);
  const [trialBalance, setTrialBalance] = useState<any[]>([]);
  const [vendorInvoices, setVendorInvoices] = useState<any[]>([]);
  const [vendorPayments, setVendorPayments] = useState<any[]>([]);
  const [billing, setBilling] = useState<any[]>([]);
  const [incoming, setIncoming] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    setLoading(true); setError("");
    try {
      const [a, d, tb, vi, vp, b, ip] = await Promise.all([
        financeApi.glAccounts(), financeApi.accountingDocuments(), financeApi.trialBalance(),
        procurementApi.vendorInvoices(), procurementApi.vendorPayments(), orderToCashApi.billingDocuments(), orderToCashApi.incomingPayments()
      ]);
      setAccounts(a); setDocuments(d); setTrialBalance(tb); setVendorInvoices(vi); setVendorPayments(vp); setBilling(b); setIncoming(ip);
    } catch (e) { setError(e instanceof Error ? e.message : "Unable to load finance workspace."); }
    finally { setLoading(false); }
  }
  useEffect(() => { load(); }, []);

  const apOpen = useMemo(() => vendorInvoices.filter(i => ["MATCHED", "POSTED"].includes(i.status)).reduce((s, i) => s + Number(i.totalAmount || 0), 0) - vendorPayments.filter(p => p.status === "PAID").reduce((s, p) => s + Number(p.amount || 0), 0), [vendorInvoices, vendorPayments]);
  const arOpen = useMemo(() => billing.filter(i => i.status === "POSTED").reduce((s, i) => s + Number(i.totalAmount || 0), 0) - incoming.reduce((s, p) => s + Number(p.appliedAmount || p.amount || 0), 0), [billing, incoming]);
  const posted = documents.filter(d => d.status === "POSTED");
  const debitTotal = trialBalance.reduce((s, x) => s + Number(x.debitBalance || 0), 0);
  const creditTotal = trialBalance.reduce((s, x) => s + Number(x.creditBalance || 0), 0);

  return <main className="page">
    <div className="page-head"><div><div className="eyebrow">FINANCE / FI</div><h1>Finance & Accounting</h1><p>AP, AR, GR/IR, clearing and accounting documents.</p></div><button className="btn" onClick={load}>Refresh</button></div>
    {error && <div className="alert error">{error}</div>}
    <div className="stats-grid">
      <div className="stat-card"><span>Accounts Payable</span><strong>{apOpen.toFixed(2)}</strong><small>Open vendor exposure</small></div>
      <div className="stat-card"><span>Accounts Receivable</span><strong>{arOpen.toFixed(2)}</strong><small>Open customer exposure</small></div>
      <div className="stat-card"><span>Posted FI Documents</span><strong>{posted.length}</strong><small>Accounting documents</small></div>
      <div className="stat-card"><span>Trial Balance</span><strong>{debitTotal.toFixed(2)}</strong><small>Debit = {debitTotal.toFixed(2)} / Credit = {creditTotal.toFixed(2)}</small></div>
    </div>

    <div className="module-grid">
      <Link className="module-card" href="/procurement/vendor-invoices"><b>Accounts Payable</b><span>Invoice verification, matching and blocked invoices</span></Link>
      <Link className="module-card" href="/order-to-cash/accounts-receivable"><b>Accounts Receivable</b><span>Customer open items, incoming payments and clearing</span></Link>
      <Link className="module-card" href="/procurement/gr-ir-reconciliation"><b>GR/IR Reconciliation</b><span>Review received, invoiced and outstanding procurement value</span></Link>
      <div className="module-card"><b>General Ledger</b><span>Chart of accounts, journal documents and trial balance</span></div>
    </div>

    <ModuleWorkspace title="Chart of Accounts" subtitle={`${accounts.length} configured GL accounts`} searchPlaceholder="Search accounts..." columns={[{key:"accountCode",label:"Account"},{key:"accountName",label:"Name"},{key:"accountType",label:"Type"},{key:"controlAccount",label:"Control"}]} rows={accounts} loading={loading} />
    <ModuleWorkspace title="Accounting Documents" subtitle="Posted finance document flow" searchPlaceholder="Search FI documents..." columns={[{key:"documentNumber",label:"Document"},{key:"documentType",label:"Type"},{key:"referenceNumber",label:"Reference"},{key:"postingDate",label:"Posting Date"},{key:"currency",label:"Currency"},{key:"totalDebit",label:"Debit"},{key:"totalCredit",label:"Credit"},{key:"status",label:"Status"}]} rows={documents} loading={loading} />
    <ModuleWorkspace title="Trial Balance" subtitle="Posted ledger balances" searchPlaceholder="Search GL account..." columns={[{key:"accountCode",label:"Account"},{key:"accountName",label:"Name"},{key:"debitBalance",label:"Debit"},{key:"creditBalance",label:"Credit"}]} rows={trialBalance} loading={loading} />
  </main>;
}
