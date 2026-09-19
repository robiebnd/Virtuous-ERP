"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { financeApi } from "@/lib/api";

type Row = { accountCode:string; accountName:string; accountType:string; debitBalance?:number; creditBalance?:number };

const normalise=(v:string)=>String(v||"").toUpperCase().replace(/[^A-Z]/g,"");
const amount=(x:Row)=>{
  const d=Number(x.debitBalance||0), c=Number(x.creditBalance||0);
  const t=normalise(x.accountType);
  return ["ASSET","EXPENSE"].some(k=>t.includes(k)) ? d-c : c-d;
};

export default function FinancialStatementsPage(){
 const [accounts,setAccounts]=useState<any[]>([]);
 const [tb,setTb]=useState<Row[]>([]);
 const [error,setError]=useState("");
 const [loading,setLoading]=useState(true);
 const load=async()=>{setLoading(true);setError("");try{const [a,t]=await Promise.all([financeApi.glAccounts(),financeApi.trialBalance()]);setAccounts(a);setTb(t);}catch(e){setError(e instanceof Error?e.message:"Unable to load financial statements.");}finally{setLoading(false);}};
 useEffect(()=>{load();},[]);
 const rows=useMemo(()=>tb.map(x=>{const master=accounts.find(a=>a.accountCode===x.accountCode);return {...x,accountType:x.accountType||master?.accountType||"UNCLASSIFIED",accountName:x.accountName||master?.accountName||""};}),[tb,accounts]);
 const groups=useMemo(()=>({
  assets:rows.filter(x=>normalise(x.accountType).includes("ASSET")),
  liabilities:rows.filter(x=>normalise(x.accountType).includes("LIABILITY")),
  equity:rows.filter(x=>normalise(x.accountType).includes("EQUITY")),
  revenue:rows.filter(x=>normalise(x.accountType).includes("REVENUE")||normalise(x.accountType).includes("INCOME")),
  expenses:rows.filter(x=>normalise(x.accountType).includes("EXPENSE")||normalise(x.accountType).includes("COST")),
  unclassified:rows.filter(x=>!["ASSET","LIABILITY","EQUITY","REVENUE","INCOME","EXPENSE","COST"].some(k=>normalise(x.accountType).includes(k)))
 }),[rows]);
 const total=(xs:Row[])=>xs.reduce((s,x)=>s+amount(x),0);
 const netIncome=total(groups.revenue)-total(groups.expenses);
 const balanceDifference=total(groups.assets)-(total(groups.liabilities)+total(groups.equity)+netIncome);
 const Statement=({title,items}:{title:string;items:Row[]})=><section className="card" style={{marginBottom:18}}><div className="section-title"><span>{title}</span><span>{total(items).toFixed(2)}</span></div>{items.length===0?<div className="empty">No classified accounts in this statement.</div>:<div className="table-wrap"><table className="table"><thead><tr><th>Account</th><th>Name</th><th>Type</th><th style={{textAlign:"right"}}>Balance</th></tr></thead><tbody>{items.map(x=><tr key={x.accountCode}><td>{x.accountCode}</td><td>{x.accountName}</td><td>{x.accountType}</td><td style={{textAlign:"right"}}>{amount(x).toFixed(2)}</td></tr>)}</tbody></table></div>}</section>;
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">FINANCE / FINANCIAL REPORTING</div><h1>Financial Statements</h1><p>Balance Sheet and Profit & Loss derived from posted general ledger balances.</p></div><div className="actions"><Link className="btn" href="/finance">Back to Finance</Link><button className="btn" onClick={load}>{loading?"Loading…":"Refresh"}</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <div className="grid stats">
   <div className="card stat"><div className="stat-label">Assets</div><div className="stat-value">{total(groups.assets).toFixed(2)}</div><div className="stat-foot">Balance Sheet</div></div>
   <div className="card stat"><div className="stat-label">Liabilities</div><div className="stat-value">{total(groups.liabilities).toFixed(2)}</div><div className="stat-foot">Balance Sheet</div></div>
   <div className="card stat"><div className="stat-label">Equity</div><div className="stat-value">{total(groups.equity).toFixed(2)}</div><div className="stat-foot">Balance Sheet</div></div>
   <div className="card stat"><div className="stat-label">Net Result</div><div className="stat-value">{netIncome.toFixed(2)}</div><div className="stat-foot">Revenue less expenses</div></div>
  </div>
  <div className={Math.abs(balanceDifference)<0.005?"alert":"alert error"} style={{marginBottom:18}}>Statement control: {Math.abs(balanceDifference)<0.005?"BALANCED":"VARIANCE"} · Assets − (Liabilities + Equity + Net Result) = {balanceDifference.toFixed(2)}</div>
  <Statement title="Balance Sheet — Assets" items={groups.assets}/>
  <Statement title="Balance Sheet — Liabilities" items={groups.liabilities}/>
  <Statement title="Balance Sheet — Equity" items={groups.equity}/>
  <Statement title="Profit & Loss — Revenue" items={groups.revenue}/>
  <Statement title="Profit & Loss — Expenses" items={groups.expenses}/>
  {groups.unclassified.length>0&&<Statement title="Unclassified Accounts — Review Required" items={groups.unclassified}/>}
 </main>;
}
