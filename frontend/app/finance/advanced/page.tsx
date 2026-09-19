"use client";
import {useEffect,useState} from "react";
import Link from "next/link";
import {financeApi} from "@/lib/api";

type ModuleItem = {title:string; description:string; key:string};
const sections = [
  {title:"Receivables Management",eyebrow:"AR / CREDIT & COLLECTIONS",description:"Manage customer credit, collections and disputes from one controlled workspace.",items:[
    {title:"Credit Management",description:"Credit profiles, limits, scores and blocking.",key:"c"},
    {title:"Collections",description:"Overdue worklist and promise-to-pay tracking.",key:"collections"},
    {title:"Disputes",description:"Invoice-linked dispute cases and resolution ownership.",key:"d"}]},
  {title:"Revenue Accounting",eyebrow:"REVENUE / RECOGNITION",description:"Manage contracts, performance obligations and controlled revenue recognition.",items:[
    {title:"Contracts",description:"Revenue contracts and transaction prices.",key:"a"},
    {title:"Performance Obligations",description:"Standalone selling prices, allocation and fulfillment.",key:"obligations"},
    {title:"Revenue Recognition",description:"Controlled recognition events linked to accounting documents.",key:"recognition"}]},
  {title:"Group Reporting",eyebrow:"CONSOLIDATION",description:"Maintain consolidation structures, reporting units and period adjustments.",items:[
    {title:"Consolidation Groups",description:"Group reporting currencies and consolidation structure.",key:"g"},
    {title:"Consolidation Units",description:"Legal entities and ownership percentages.",key:"u"},
    {title:"Adjustments",description:"Period-specific consolidation adjustments.",key:"adjustments"}]},
  {title:"Funds & Tax",eyebrow:"CONTROL / COMPLIANCE",description:"Control funds, commitments, tax configuration and foreign exchange valuation.",items:[
    {title:"Funds Management",description:"Funds, budget controls and commitment availability.",key:"f"},
    {title:"Tax",description:"Tax codes, rates and tax account determination.",key:"t"},
    {title:"FX",description:"Rates and controlled valuation postings.",key:"fx"}]},
  {title:"CO Product Costing",eyebrow:"CONTROLLING / COST",description:"Support product costing, material valuation and recurring period-end controls.",items:[
    {title:"Product Costing",description:"Standard cost estimates and release controls.",key:"costing"},
    {title:"Material Ledger",description:"Periodic standard versus actual valuation and actual unit cost.",key:"ledger"},
    {title:"Accruals",description:"Recurring accrual templates and controlled posting.",key:"ac"}]}
];

export default function AdvancedFinance(){
  const [data,setData]=useState<any>({});
  const [error,setError]=useState("");
  const [busy,setBusy]=useState(true);
  const load=async()=>{
    setBusy(true);
    try{
      const [c,d,t,f,g,u,a,ac]=await Promise.all([
        financeApi.advancedCredits(),financeApi.disputes(),financeApi.taxCodes(),financeApi.funds(),
        financeApi.consolidationGroups(),financeApi.consolidationUnits(),financeApi.revenueContracts(),
        financeApi.accrualTemplates()
      ]);
      setData({c,d,t,f,g,u,a,ac}); setError("");
    }catch(e){setError(e instanceof Error?e.message:"Unable to load advanced finance.")}
    finally{setBusy(false)}
  };
  useEffect(()=>{load()},[]);
  const countFor=(key:string)=>Array.isArray(data[key])?data[key].length:undefined;
  return <main className="content advanced-finance">
    <div className="page-head advanced-finance-head">
      <div><div className="eyebrow">FINANCE / ADVANCED</div><h1>Advanced Finance</h1><p>Credit, collections, disputes, revenue recognition, consolidation, funds, tax, FX, costing and close controls.</p></div>
      <div className="actions"><Link className="btn" href="/finance">Finance</Link><button className="btn primary" onClick={load}>{busy?"Loading…":"Refresh"}</button></div>
    </div>
    {error&&<div className="alert error">{error}</div>}
    <div className="grid advanced-summary">
      <div className="card advanced-summary-card"><span>Credit Profiles</span><strong>{data.c?.length||0}</strong><small>Active records</small></div>
      <div className="card advanced-summary-card"><span>Open Disputes</span><strong>{data.d?.length||0}</strong><small>Cases requiring control</small></div>
      <div className="card advanced-summary-card"><span>Revenue Contracts</span><strong>{data.a?.length||0}</strong><small>Contract records</small></div>
      <div className="card advanced-summary-card"><span>Funds</span><strong>{data.f?.length||0}</strong><small>Configured funds</small></div>
    </div>
    <div className="advanced-sections">
      {sections.map(section=><section className="card advanced-section" key={section.title}>
        <div className="advanced-section-head"><div><div className="eyebrow">{section.eyebrow}</div><h2>{section.title}</h2><p>{section.description}</p></div></div>
        <div className="advanced-module-grid">
          {section.items.map(item=>{const count=countFor(item.key);return <div className="advanced-module" key={item.title}>
            <div className="advanced-module-copy"><h3>{item.title}</h3><p>{item.description}</p></div>
            <div className="advanced-module-footer"><span className={count!==undefined?"advanced-status":"advanced-status workflow"}>{count!==undefined?count+" "+(count===1?"record":"records"):"Workflow available"}</span><span className="advanced-arrow">→</span></div>
          </div>})}
        </div>
      </section>)}
    </div>
  </main>
}