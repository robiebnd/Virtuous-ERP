"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { financeApi, procurementApi } from "@/lib/api";

type Check={id:string;name:string;description:string;href?:string;status:"READY"|"REVIEW"|"NOT_AUTOMATED";detail:string};

export default function PeriodEndClosePage(){
 const [checks,setChecks]=useState<Check[]>([]);
 const [loading,setLoading]=useState(true);
 const [error,setError]=useState("");
 const load=async()=>{
  setLoading(true);setError("");
  try{
   const [ap,ar,ageAp,ageAr,ir,assets,grirs]=await Promise.all([
    financeApi.openItemsAp(),financeApi.openItemsAr(),financeApi.ageing("AP"),financeApi.ageing("AR"),
    financeApi.inventoryReconciliation(),financeApi.fixedAssets(),procurementApi.orders()
   ]);
   const poCount=Array.isArray(grirs)?grirs.length:0;
   setChecks([
    {id:"subledger",name:"Sub-ledger closing",description:"Review open Accounts Payable and Accounts Receivable items before close.",href:"/finance",status:ap.length+ar.length>0?"REVIEW":"READY",detail:ap.length+" AP and "+ar.length+" AR open items"},
    {id:"grir",name:"GR/IR reconciliation",description:"Review procurement receipt/invoice differences and close reconciled balances.",href:"/procurement/gr-ir-reconciliation",status:poCount>0?"REVIEW":"READY",detail:poCount+" purchase orders available for reconciliation"},
    {id:"accruals",name:"Accruals / deferrals",description:"Recurring entries or accrual engine postings required by the period close.",href:"/finance/journal-entry",status:"NOT_AUTOMATED",detail:"Journal entry posting is available; recurring/accrual automation is not yet implemented."},
    {id:"fx",name:"Foreign currency valuation",description:"Value open foreign-currency balances at the period-end rate.",status:"NOT_AUTOMATED",detail:"FX valuation workflow is not yet automated."},
    {id:"depreciation",name:"Depreciation run",description:"Post depreciation for active fixed assets before closing the period.",href:"/finance/controls",status:assets.some((x:any)=>x.status==="ACTIVE")?"REVIEW":"READY",detail:assets.length+" fixed assets loaded"},
    {id:"allocations",name:"Cost allocations",description:"Execute distribution and assessment cycles for cost centres.",href:"/finance/controls",status:"NOT_AUTOMATED",detail:"Cost-centre master data exists; allocation cycles are not yet automated."},
    {id:"settlement",name:"Internal order / WBS settlement",description:"Settle eligible internal orders or WBS balances.",status:"NOT_AUTOMATED",detail:"Internal order/WBS settlement is not yet implemented."},
    {id:"carryforward",name:"Balance carryforward",description:"Carry forward eligible balances into the next fiscal year.",status:"NOT_AUTOMATED",detail:"Year-end carryforward is not yet automated."},
    {id:"inventory",name:"Inventory to GL control",description:"Confirm inventory valuation agrees with the inventory control account.",href:"/finance/inventory-accounting",status:ir?.balanced?"READY":"REVIEW",detail:"Variance "+Number(ir?.variance||0).toFixed(2)},
    {id:"reporting",name:"Financial statements",description:"Review Balance Sheet and Profit & Loss after close adjustments.",href:"/finance/financial-statements",status:"READY",detail:"Reporting workspace available"}
   ]);
  }catch(e){setError(e instanceof Error?e.message:"Unable to load close checklist.");}
  finally{setLoading(false);}
 };
 useEffect(()=>{load();},[]);
 const ready=checks.filter(x=>x.status==="READY").length, review=checks.filter(x=>x.status==="REVIEW").length, not=checks.filter(x=>x.status==="NOT_AUTOMATED").length;
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">FINANCE / PERIOD-END</div><h1>Period-End Close</h1><p>Control checklist based on the Record-to-Report close sequence in the Finance reference.</p></div><div className="actions"><Link className="btn" href="/finance">Back to Finance</Link><button className="btn" onClick={load}>{loading?"Checking…":"Refresh"}</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <div className="grid stats"><div className="card stat"><div className="stat-label">Ready</div><div className="stat-value">{ready}</div><div className="stat-foot">Checks with no current exception</div></div><div className="card stat"><div className="stat-label">Review</div><div className="stat-value">{review}</div><div className="stat-foot">Requires finance review</div></div><div className="card stat"><div className="stat-label">Not Automated</div><div className="stat-value">{not}</div><div className="stat-foot">Requires manual process</div></div><div className="card stat"><div className="stat-label">Checklist</div><div className="stat-value">{checks.length}</div><div className="stat-foot">Record-to-Report controls</div></div></div>
  <section className="card"><div className="section-title">Close Checklist</div>{loading?<div className="empty">Loading close controls...</div>:checks.map(x=><div className="list-row" key={x.id}><div style={{flex:1}}><b>{x.name}</b><div className="muted">{x.description}</div><div className="muted">{x.detail}</div></div><div style={{display:"flex",alignItems:"center",gap:10}}><span className={`status ${x.status==="READY"?"approved":x.status==="REVIEW"?"pending":"draft"}`}>{x.status.replaceAll("_"," ")}</span>{x.href&&<Link className="btn" href={x.href}>Open</Link>}</div></div>)}</section>
 </main>;
}
