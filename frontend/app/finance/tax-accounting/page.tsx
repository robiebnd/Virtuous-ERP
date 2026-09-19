"use client";
import {useEffect,useState} from "react";
import {financeApi} from "@/lib/api";

export default function TaxAccountingPage(){
 const [companies,setCompanies]=useState<any[]>([]),[codes,setCodes]=useState<any[]>([]),[postings,setPostings]=useState<any[]>([]),[error,setError]=useState("");
 const [company,setCompany]=useState("ZW01"),[busy,setBusy]=useState(false);
 const [codeForm,setCodeForm]=useState({companyCode:"ZW01",taxCode:"VAT15",description:"Standard VAT",rate:"15",inputAccountCode:"1510",outputAccountCode:"2210",withholding:false});
 const [postForm,setPostForm]=useState({companyCode:"ZW01",documentType:"TAX_INVOICE",referenceNumber:"",currency:"USD",taxCode:"VAT15",inputOutput:"OUTPUT",taxableAmount:"",baseDebitAccount:"1200",baseCreditAccount:"4100",description:"Tax accounting",postingDate:new Date().toISOString().slice(0,10)});
 const load=async(c=company)=>{try{const [co,tc,tp]=await Promise.all([financeApi.companyCodes(),financeApi.taxCodes(),financeApi.taxPostings(c)]);setCompanies(co);setCodes(tc.filter((x:any)=>!x.companyCode||x.companyCode===c));setPostings(tp)}catch(e){setError(e instanceof Error?e.message:"Unable to load tax accounting.")}};
 useEffect(()=>{load()},[]);
 const saveCode=async()=>{setBusy(true);try{await financeApi.saveTaxCode({...codeForm,rate:Number(codeForm.rate)});await load(company);setError("")}catch(e){setError(e instanceof Error?e.message:"Unable to save tax code.")}finally{setBusy(false)}};
 const postTax=async()=>{setBusy(true);try{await financeApi.postTax({...postForm,taxableAmount:Number(postForm.taxableAmount)});await load(company);setError("")}catch(e){setError(e instanceof Error?e.message:"Unable to post tax.")}finally{setBusy(false)}};
 return <main className="content">
  <div className="page-head"><div><div className="eyebrow">FINANCE / TAX ACCOUNTING</div><h1>Tax Accounting</h1><p>Company-specific tax codes, input/output tax determination and integrated tax postings.</p></div><div className="actions"><button className="btn primary" onClick={()=>load()}>{busy?"Working…":"Refresh"}</button></div></div>
  {error&&<div className="alert error">{error}</div>}
  <div className="card" style={{marginBottom:18}}><div className="toolbar"><div className="filter-field"><label>Company Code</label><select className="filter" value={company} onChange={e=>{setCompany(e.target.value);setCodeForm({...codeForm,companyCode:e.target.value});setPostForm({...postForm,companyCode:e.target.value});load(e.target.value)}}>{companies.map(x=><option key={x.id}>{x.companyCode}</option>)}</select></div></div></div>
  <div className="grid" style={{gridTemplateColumns:"1fr 1fr",marginBottom:18}}>
   <section className="card form-card"><div className="section-title">Tax Code Setup</div><div className="form-grid">
    <div className="form-field"><label>Tax Code</label><input className="form-input" value={codeForm.taxCode} onChange={e=>setCodeForm({...codeForm,taxCode:e.target.value})}/></div>
    <div className="form-field"><label>Description</label><input className="form-input" value={codeForm.description} onChange={e=>setCodeForm({...codeForm,description:e.target.value})}/></div>
    <div className="form-field"><label>Rate %</label><input className="form-input" type="number" value={codeForm.rate} onChange={e=>setCodeForm({...codeForm,rate:e.target.value})}/></div>
    <div className="form-field"><label>Input Tax Account</label><input className="form-input" value={codeForm.inputAccountCode} onChange={e=>setCodeForm({...codeForm,inputAccountCode:e.target.value})}/></div>
    <div className="form-field"><label>Output Tax Account</label><input className="form-input" value={codeForm.outputAccountCode} onChange={e=>setCodeForm({...codeForm,outputAccountCode:e.target.value})}/></div>
    <div className="form-field"><label>&nbsp;</label><button className="btn primary" onClick={saveCode}>Save Tax Code</button></div>
   </div></section>
   <section className="card form-card"><div className="section-title">Integrated Tax Posting</div><div className="form-grid">
    <div className="form-field"><label>Reference Number</label><input className="form-input" value={postForm.referenceNumber} onChange={e=>setPostForm({...postForm,referenceNumber:e.target.value})}/></div>
    <div className="form-field"><label>Tax Code</label><select className="form-input" value={postForm.taxCode} onChange={e=>setPostForm({...postForm,taxCode:e.target.value})}>{codes.map(x=><option key={x.id}>{x.taxCode}</option>)}</select></div>
    <div className="form-field"><label>Direction</label><select className="form-input" value={postForm.inputOutput} onChange={e=>setPostForm({...postForm,inputOutput:e.target.value})}><option>OUTPUT</option><option>INPUT</option></select></div>
    <div className="form-field"><label>Taxable Amount</label><input className="form-input" type="number" value={postForm.taxableAmount} onChange={e=>setPostForm({...postForm,taxableAmount:e.target.value})}/></div>
    <div className="form-field"><label>Base Debit Account</label><input className="form-input" value={postForm.baseDebitAccount} onChange={e=>setPostForm({...postForm,baseDebitAccount:e.target.value})}/></div>
    <div className="form-field"><label>Base Credit Account</label><input className="form-input" value={postForm.baseCreditAccount} onChange={e=>setPostForm({...postForm,baseCreditAccount:e.target.value})}/></div>
    <div className="form-field"><label>Posting Date</label><input className="form-input" type="date" value={postForm.postingDate} onChange={e=>setPostForm({...postForm,postingDate:e.target.value})}/></div>
    <div className="form-field"><label>&nbsp;</label><button className="btn primary" onClick={postTax}>Post Tax Document</button></div>
   </div></section>
  </div>
  <section className="card"><div className="table-caption"><strong>Tax Postings</strong><span>{postings.length} records for {company}</span></div><div className="table-wrap"><table className="table"><thead><tr><th>Date</th><th>Tax Code</th><th>Type</th><th>Direction</th><th>Taxable Base</th><th>Tax</th><th>Recoverable</th><th>Account</th></tr></thead><tbody>{postings.map(x=><tr key={x.id}><td>{x.createdAt?.slice(0,10)}</td><td>{x.taxCode}</td><td>{x.taxType}</td><td>{x.inputOutput}</td><td>{Number(x.taxableBase||0).toFixed(2)}</td><td>{Number(x.taxAmount||0).toFixed(2)}</td><td>{Number(x.recoverableAmount||0).toFixed(2)}</td><td>{x.taxAccountCode}</td></tr>)}</tbody></table></div></section>
 </main>
}