"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { financeApi, procurementApi } from "@/lib/api";

type Check={id:string;name:string;description:string;href?:string;status:"READY"|"REVIEW"|"NOT_AUTOMATED";detail:string};

export default function PeriodEndClosePage(){
 const [checks,setChecks]=useState<Check[]>([]);
 const [loading,setLoading]=useState(true);
 const [error,setError]=useState("");
 const [fiscalYear,setFiscalYear]=useState(new Date().getFullYear());
 const [periods,setPeriods]=useState<any[]>([]);
 const [periodBusy,setPeriodBusy]=useState(false);
 const [yearCloseResult,setYearCloseResult]=useState<any>(null);
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
    {id:"accruals",name:"Accruals / deferrals",description:"Execute approved recurring accrual templates before close.",href:"/finance/advanced",status:"REVIEW",detail:"Controlled recurring accrual templates and GL-integrated posting are available."},
    {id:"fx",name:"Foreign currency valuation",description:"Value foreign-currency balances at the period-end rate.",href:"/finance/advanced",status:"REVIEW",detail:"Controlled FX rates and GL-integrated valuation posting are available."},
    {id:"depreciation",name:"Depreciation run",description:"Post depreciation for active fixed assets before closing the period.",href:"/finance/controls",status:assets.some((x:any)=>x.status==="ACTIVE")?"REVIEW":"READY",detail:assets.length+" fixed assets loaded; monthly posting is controlled and GL-integrated"},
    {id:"allocations",name:"Cost allocations",description:"Review and post controlled cost-centre allocations before close.",href:"/finance/management-accounting",status:"REVIEW",detail:"Controlled allocation posting is available; review source, target and expense GL before posting."},
    {id:"settlement",name:"Internal order / WBS settlement",description:"Settle eligible internal orders to their receiving cost centre.",href:"/finance/advanced",status:"REVIEW",detail:"Controlled internal order settlement is available; WBS settlement remains dependent on project accounting."},
    {id:"carryforward",name:"Balance carryforward",description:"Verify fiscal periods and execute controlled year-end carryforward when the year is fully closed.",href:"/finance/period-end-close",status:"REVIEW",detail:"Fiscal period controls and system-generated closing/opening journals are now available."},
    {id:"inventory",name:"Inventory to GL control",description:"Confirm inventory valuation agrees with the inventory control account.",href:"/finance/inventory-accounting",status:ir?.balanced?"READY":"REVIEW",detail:"Variance "+Number(ir?.variance||0).toFixed(2)},
    {id:"treasury",name:"Bank reconciliation and cash",description:"Review bank statement exceptions and cash position before close.",href:"/finance/treasury",status:"REVIEW",detail:"Treasury workspace provides statement capture, reconciliation and cash position"},
    {id:"reporting",name:"Financial statements",description:"Review Balance Sheet and Profit & Loss after close adjustments.",href:"/finance/financial-statements",status:"READY",detail:"Reporting workspace available"}
   ]);
  }catch(e){setError(e instanceof Error?e.message:"Unable to load close checklist.");}
  finally{setLoading(false);}
 };
 useEffect(()=>{load();loadPeriods(new Date().getFullYear());},[]);
 const loadPeriods=async(year:number)=>{try{setPeriods(await financeApi.fiscalPeriods(year));}catch(e){setError(e instanceof Error?e.message:"Unable to load fiscal periods.");}};
 const createYear=async()=>{setPeriodBusy(true);setError("");try{await financeApi.createFiscalYear(fiscalYear);await loadPeriods(fiscalYear);}catch(e){setError(e instanceof Error?e.message:"Unable to create fiscal year.");}finally{setPeriodBusy(false);}};
 const togglePeriod=async(p:any)=>{setPeriodBusy(true);setError("");try{if(p.status==="OPEN") await financeApi.closeFiscalPeriod(p.fiscalYear,p.periodNumber); else if(p.status==="CLOSED") await financeApi.reopenFiscalPeriod(p.fiscalYear,p.periodNumber); await loadPeriods(fiscalYear);}catch(e){setError(e instanceof Error?e.message:"Unable to update fiscal period.");}finally{setPeriodBusy(false);}};
 const carryForward=async()=>{setPeriodBusy(true);setError("");setYearCloseResult(null);try{setYearCloseResult(await financeApi.closeFiscalYear(fiscalYear));await loadPeriods(fiscalYear);}catch(e){setError(e instanceof Error?e.message:"Unable to complete fiscal year close.");}finally{setPeriodBusy(false);}};
 const ready=checks.filter(x=>x.status==="READY").length, review=checks.filter(x=>x.status==="REVIEW").length, not=checks.filter(x=>x.status==="NOT_AUTOMATED").length;
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">FINANCE / PERIOD-END</div><h1>Period-End Close</h1><p>Control checklist based on the Record-to-Report close sequence in the Finance reference.</p></div><div className="actions"><Link className="btn" href="/finance">Back to Finance</Link><button className="btn" onClick={load}>{loading?"Checking…":"Refresh"}</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <div className="grid stats"><div className="card stat"><div className="stat-label">Ready</div><div className="stat-value">{ready}</div><div className="stat-foot">Checks with no current exception</div></div><div className="card stat"><div className="stat-label">Review</div><div className="stat-value">{review}</div><div className="stat-foot">Requires finance review</div></div><div className="card stat"><div className="stat-label">Not Automated</div><div className="stat-value">{not}</div><div className="stat-foot">Requires manual process</div></div><div className="card stat"><div className="stat-label">Checklist</div><div className="stat-value">{checks.length}</div><div className="stat-foot">Record-to-Report controls</div></div></div>
  <section className="card fiscal-control-card">
   <div className="section-title">Fiscal Period Control</div>
   <div className="fiscal-toolbar">
    <div className="form-field"><label>Fiscal Year</label><input className="form-input" type="number" min="2000" value={fiscalYear} onChange={e=>{const y=Number(e.target.value);setFiscalYear(y);loadPeriods(y)}} /></div>
    <div className="fiscal-actions"><button className="btn" onClick={createYear} disabled={periodBusy}>Create Year</button><button className="btn" onClick={()=>loadPeriods(fiscalYear)} disabled={periodBusy}>Refresh Periods</button><button className="btn primary" onClick={carryForward} disabled={periodBusy||periods.length!==12||periods.some(p=>p.status==="OPEN")}>Close & Carry Forward</button></div>
   </div>
   <div className="period-grid">{periods.map(p=><div className={`period-item ${p.status.toLowerCase()}`} key={p.id}><div><strong>{p.periodName}</strong><span>{p.startDate} → {p.endDate}</span></div><div className="period-item-actions"><span className={`status ${p.status==="OPEN"?"approved":"pending"}`}>{p.status.replaceAll("_"," ")}</span>{p.status!=="PERMANENTLY_CLOSED"&&<button className="btn" onClick={()=>togglePeriod(p)} disabled={periodBusy}>{p.status==="OPEN"?"Close":"Reopen"}</button>}</div></div>)}</div>
   {yearCloseResult&&<div className="alert success fiscal-result">Year {yearCloseResult.fiscalYear} closed. Closing journal <b>{yearCloseResult.closingDocumentNumber}</b>; opening journal <b>{yearCloseResult.openingDocumentNumber}</b>; net income transferred {Number(yearCloseResult.netIncomeTransferred||0).toFixed(2)}.</div>}
  </section>
  <section className="card"><div className="section-title">Close Checklist</div>{loading?<div className="empty">Loading close controls...</div>:checks.map(x=><div className="list-row" key={x.id}><div style={{flex:1}}><b>{x.name}</b><div className="muted">{x.description}</div><div className="muted">{x.detail}</div></div><div style={{display:"flex",alignItems:"center",gap:10}}><span className={`status ${x.status==="READY"?"approved":x.status==="REVIEW"?"pending":"draft"}`}>{x.status.replaceAll("_"," ")}</span>{x.href&&<Link className="btn" href={x.href}>Open</Link>}</div></div>)}</section>
 </main>;
}
