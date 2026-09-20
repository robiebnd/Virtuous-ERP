"use client";
import {useEffect,useState} from "react";
import {financeApi} from "@/lib/api";

export default function ProfitabilityPage(){
 const [companies,setCompanies]=useState<any[]>([]),[segments,setSegments]=useState<any[]>([]),[report,setReport]=useState<any[]>([]),[error,setError]=useState("");
 const [company,setCompany]=useState("ZW01"),[year,setYear]=useState(new Date().getFullYear()),[period,setPeriod]=useState(1);
 const [form,setForm]=useState({segmentCode:"",segmentName:"",companyCode:"ZW01",productCode:"",salesChannel:"",marketRegion:"",customerGroup:"",productGroup:""});
 const load=async(c=company)=>{try{const [co,s]=await Promise.all([financeApi.companyCodes(),financeApi.profitabilitySegments(c)]);setCompanies(co);setSegments(s);setForm(x=>({...x,companyCode:c}));setError("")}catch(e){setError(e instanceof Error?e.message:"Unable to load CO-PA.")}};
 useEffect(()=>{load()},[]);
 const create=async()=>{try{await financeApi.createProfitabilitySegment(form);await load(company)}catch(e){setError(e instanceof Error?e.message:"Unable to create segment.")}};
 const run=async()=>{try{setReport(await financeApi.profitabilityReport(company,year,period));setError("")}catch(e){setError(e instanceof Error?e.message:"Unable to load profitability report.")}};
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">FINANCE / CO-PA</div><h1>Profitability Analysis</h1><p>Account-based profitability by product, customer, channel and market dimensions, reconciled from accounting postings.</p></div><div className="actions"><button className="btn primary" onClick={()=>run()}>Refresh Report</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <div className="card" style={{marginBottom:18}}><div className="toolbar"><div className="filter-field"><label>Company Code</label><select className="filter" value={company} onChange={e=>{setCompany(e.target.value);load(e.target.value)}}>{companies.map(x=><option key={x.id}>{x.companyCode}</option>)}</select></div><div className="filter-field"><label>Fiscal Year</label><input className="filter" type="number" value={year} onChange={e=>setYear(Number(e.target.value))}/></div><div className="filter-field"><label>Period</label><select className="filter" value={period} onChange={e=>setPeriod(Number(e.target.value))}>{Array.from({length:12},(_,i)=><option key={i+1}>{i+1}</option>)}</select></div></div></div>
  <div className="grid" style={{gridTemplateColumns:"1fr 2fr",marginBottom:18}}>
   <section className="card form-card"><div className="section-title">Profitability Segment</div><div className="form-grid" style={{gridTemplateColumns:"1fr"}}>
    {Object.entries(form).filter(([k])=>k!=="companyCode").map(([k,v])=><div className="form-field" key={k}><label>{k}</label><input className="form-input" value={v as string} onChange={e=>setForm({...form,[k]:e.target.value})}/></div>)}<button className="btn primary" onClick={create}>Create Segment</button>
   </div></section>
   <section className="card"><div className="table-caption"><strong>Configured Segments</strong><span>{segments.length} segments</span></div><div className="table-wrap"><table className="table"><thead><tr><th>Code</th><th>Name</th><th>Product</th><th>Channel</th><th>Region</th><th>Customer Group</th></tr></thead><tbody>{segments.map(x=><tr key={x.id}><td>{x.segmentCode}</td><td>{x.segmentName}</td><td>{x.productCode||"—"}</td><td>{x.salesChannel||"—"}</td><td>{x.marketRegion||"—"}</td><td>{x.customerGroup||"—"}</td></tr>)}</tbody></table></div></section>
  </div>
  <section className="card"><div className="table-caption"><strong>Contribution Margin Report</strong><span>{company} · FY {year} / P{period}</span></div><div className="table-wrap"><table className="table"><thead><tr><th>Segment</th><th>Product</th><th>Channel</th><th>Region</th><th>Customer Group</th><th>Revenue</th><th>Cost</th><th>Contribution Margin</th><th>Margin %</th></tr></thead><tbody>{report.map(x=>{const margin=x.revenue?Number(x.contributionMargin)/Number(x.revenue)*100:0;return <tr key={x.segmentId}><td><strong>{x.segmentCode}</strong> — {x.segmentName}</td><td>{x.productCode||"—"}</td><td>{x.salesChannel||"—"}</td><td>{x.marketRegion||"—"}</td><td>{x.customerGroup||"—"}</td><td>{Number(x.revenue||0).toFixed(2)}</td><td>{Number(x.cost||0).toFixed(2)}</td><td>{Number(x.contributionMargin||0).toFixed(2)}</td><td>{margin.toFixed(1)}%</td></tr>})}</tbody></table></div></section>
 </main>
}