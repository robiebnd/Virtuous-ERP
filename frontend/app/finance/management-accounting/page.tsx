"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { financeApi } from "@/lib/api";

const money=(v:any)=>Number(v||0).toFixed(2);

export default function ManagementAccountingPage(){
 const [year,setYear]=useState(new Date().getFullYear());
 const [centers,setCenters]=useState<any[]>([]);
 const [orders,setOrders]=useState<any[]>([]);
 const [loading,setLoading]=useState(true);
 const [error,setError]=useState("");
 const [budget,setBudget]=useState({costCenterCode:"",fiscalYear:new Date().getFullYear(),budgetAmount:"",currency:"USD",description:""});
 const [order,setOrder]=useState({orderCode:"",name:"",costCenterCode:"",budgetAmount:"0",currency:"USD"});
 const [allocation,setAllocation]=useState({sourceCostCenter:"",targetCostCenter:"",expenseAccountCode:"610000",amount:"",referenceNumber:"",description:"Cost centre allocation"});
 const load=async()=>{
  setLoading(true);setError("");
  try{const [c,o]=await Promise.all([financeApi.managementCostCenterActuals(year),financeApi.internalOrderActuals()]);setCenters(c);setOrders(o);}
  catch(e){setError(e instanceof Error?e.message:"Unable to load management accounting.");}
  finally{setLoading(false);}
 };
 useEffect(()=>{load()},[year]);
 async function saveBudget(e:React.FormEvent){e.preventDefault();try{await financeApi.saveCostCenterBudget({...budget,fiscalYear:year,budgetAmount:Number(budget.budgetAmount)});setBudget(x=>({...x,budgetAmount:""}));await load()}catch(e){setError(e instanceof Error?e.message:"Unable to save budget.")}}
 async function createOrder(e:React.FormEvent){e.preventDefault();try{await financeApi.createInternalOrder({...order,budgetAmount:Number(order.budgetAmount)});setOrder({orderCode:"",name:"",costCenterCode:"",budgetAmount:"0",currency:"USD"});await load()}catch(e){setError(e instanceof Error?e.message:"Unable to create internal order.")}}
 async function allocate(e:React.FormEvent){e.preventDefault();try{await financeApi.allocateCostCenter({...allocation,amount:Number(allocation.amount)});setAllocation(x=>({...x,amount:"",referenceNumber:""}));await load()}catch(e){setError(e instanceof Error?e.message:"Unable to post allocation.")}}
 return <main className="content management-accounting">
  <div className="page-head"><div><div className="eyebrow">FINANCE / CONTROLLING</div><h1>Management Accounting</h1><p>Cost centre actuals, budgets, internal orders and controlled allocations.</p></div><div className="actions"><Link className="btn" href="/finance">Finance</Link><button className="btn" onClick={load}>Refresh</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <section className="card form-card"><div className="section-title">Reporting Period</div><div className="form-grid reporting-period-grid"><div className="form-field"><label>Fiscal Year</label><input className="form-input" type="number" value={year} onChange={e=>setYear(Number(e.target.value))}/></div></div></section>
  <section className="card"><div className="section-title">Cost Centre Actual vs Budget</div>{loading?<div className="empty">Loading…</div>:<div className="table-wrap"><table className="table"><thead><tr><th>Cost Centre</th><th>Actual</th><th>Budget</th><th>Variance</th><th>Status</th></tr></thead><tbody>{centers.map(x=><tr key={x.costCenterCode}><td><b>{x.costCenterCode}</b></td><td>{money(x.actualAmount)}</td><td>{money(x.budgetAmount)}</td><td>{money(x.variance)}</td><td><span className={Number(x.variance)<0?"status pending":"status approved"}>{Number(x.variance)<0?"OVER BUDGET":"WITHIN BUDGET"}</span></td></tr>)}</tbody></table></div>}</section>
  <div className="grid stats management-form-grid">
   <section className="card form-card"><div className="section-title">Set Cost Centre Budget</div><form onSubmit={saveBudget}><div className="form-grid"><div className="form-field"><label>Cost Centre</label><input className="form-input" required value={budget.costCenterCode} onChange={e=>setBudget({...budget,costCenterCode:e.target.value})}/></div><div className="form-field"><label>Budget Amount</label><input className="form-input" type="number" min="0" step="0.01" required value={budget.budgetAmount} onChange={e=>setBudget({...budget,budgetAmount:e.target.value})}/></div><div className="form-field"><label>Currency</label><input className="form-input" value={budget.currency} onChange={e=>setBudget({...budget,currency:e.target.value})}/></div></div><button className="btn primary">Save Budget</button></form></section>
   <section className="card form-card"><div className="section-title">Create Internal Order</div><form onSubmit={createOrder}><div className="form-grid"><div className="form-field"><label>Order Code</label><input className="form-input" required value={order.orderCode} onChange={e=>setOrder({...order,orderCode:e.target.value})}/></div><div className="form-field"><label>Name</label><input className="form-input" required value={order.name} onChange={e=>setOrder({...order,name:e.target.value})}/></div><div className="form-field"><label>Cost Centre</label><input className="form-input" value={order.costCenterCode} onChange={e=>setOrder({...order,costCenterCode:e.target.value})}/></div><div className="form-field"><label>Budget</label><input className="form-input" type="number" min="0" step="0.01" value={order.budgetAmount} onChange={e=>setOrder({...order,budgetAmount:e.target.value})}/></div></div><button className="btn primary">Create Order</button></form></section>
  </div>
  <section className="card form-card allocation-card"><div className="section-title">Cost Centre Allocation</div><p className="muted">Reclassifies an expense between cost centres while preserving the selected expense GL.</p><form onSubmit={allocate}><div className="form-grid allocation-grid"><div className="form-field"><label>Source</label><input className="form-input" required value={allocation.sourceCostCenter} onChange={e=>setAllocation({...allocation,sourceCostCenter:e.target.value})}/></div><div className="form-field"><label>Target</label><input className="form-input" required value={allocation.targetCostCenter} onChange={e=>setAllocation({...allocation,targetCostCenter:e.target.value})}/></div><div className="form-field"><label>Expense GL</label><input className="form-input" required value={allocation.expenseAccountCode} onChange={e=>setAllocation({...allocation,expenseAccountCode:e.target.value})}/></div><div className="form-field"><label>Amount</label><input className="form-input" type="number" min="0.01" step="0.01" required value={allocation.amount} onChange={e=>setAllocation({...allocation,amount:e.target.value})}/></div><div className="form-field"><label>Reference</label><input className="form-input" required value={allocation.referenceNumber} onChange={e=>setAllocation({...allocation,referenceNumber:e.target.value})}/></div></div><button className="btn primary">Post Allocation</button></form></section>
  <section className="card"><div className="section-title">Internal Order Actuals</div><div className="table-wrap"><table className="table"><thead><tr><th>Order</th><th>Actual</th><th>Budget</th><th>Variance</th></tr></thead><tbody>{orders.map(x=><tr key={x.orderCode}><td><b>{x.orderCode}</b></td><td>{money(x.actualAmount)}</td><td>{money(x.budgetAmount)}</td><td>{money(x.variance)}</td></tr>)}</tbody></table></div></section>
 </main>
}