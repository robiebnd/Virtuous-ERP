"use client";

import {useEffect,useMemo,useState} from "react";
import Link from "next/link";
import {financeApi} from "@/lib/api";

type Line={accountCode:string;debit:string;credit:string;costCenter:string;profitCenter:string;lineText:string};

export default function JournalEntryPage(){
 const [accounts,setAccounts]=useState<any[]>([]);
 const [form,setForm]=useState({documentType:"SA",referenceNumber:"",currency:"USD",description:""});
 const [lines,setLines]=useState<Line[]>([{accountCode:"",debit:"0.00",credit:"0.00",costCenter:"",profitCenter:"",lineText:""}]);
 const [message,setMessage]=useState("");const [saving,setSaving]=useState(false);
 useEffect(()=>{financeApi.glAccounts().then(setAccounts).catch(()=>setAccounts([]));},[]);
 const setLine=(i:number,key:keyof Line,value:string)=>setLines(v=>v.map((x,n)=>n===i?{...x,[key]:value}:x));
 const addLine=()=>setLines(v=>[...v,{accountCode:"",debit:"0.00",credit:"0.00",costCenter:"",profitCenter:"",lineText:""}]);
 const removeLine=(i:number)=>setLines(v=>v.length>2?v.filter((_,n)=>n!==i):v);
 const debit=useMemo(()=>lines.reduce((s,x)=>s+(Number(x.debit)||0),0),[lines]);
 const credit=useMemo(()=>lines.reduce((s,x)=>s+(Number(x.credit)||0),0),[lines]);
 const balanced=Math.abs(debit-credit)<0.005 && debit>0;
 const submit=async()=>{setSaving(true);setMessage("");try{const r=await financeApi.postJournalEntry({...form,lines:lines.map(x=>({accountCode:x.accountCode,debit:Number(x.debit)||0,credit:Number(x.credit)||0,costCenter:x.costCenter||null,profitCenter:x.profitCenter||null,lineText:x.lineText||null}))});setMessage(`Accounting document ${r.documentNumber||""} posted successfully.`);setLines([{accountCode:"",debit:"0.00",credit:"0.00",costCenter:"",profitCenter:"",lineText:""}]);}catch(e:any){setMessage(e.message||"Unable to post journal entry.")}finally{setSaving(false)}};
 return <div className="content">
  <div className="document-head"><div><div className="eyebrow">FINANCE / FI-GL</div><h1>General Ledger Journal Entry</h1><p>Post a balanced accounting document with GL account and controlling dimensions.</p></div><div className="actions"><Link className="btn" href="/finance">Back</Link><button className="btn primary" disabled={saving||!balanced||lines.some(x=>!x.accountCode)} onClick={submit}>{saving?"Posting…":"Post Document"}</button></div></div>
  {message&&<div className="alert">{message}</div>}
  <section className="card form-card"><div className="section-title">Document Header</div><div className="form-grid">
   <div className="form-field"><label>Document Type *</label><select className="form-input" value={form.documentType} onChange={e=>setForm(v=>({...v,documentType:e.target.value}))}><option>SA</option><option>KR</option><option>DZ</option></select></div>
   <div className="form-field"><label>Reference</label><input className="form-input" value={form.referenceNumber} onChange={e=>setForm(v=>({...v,referenceNumber:e.target.value}))} placeholder="External reference"/></div>
   <div className="form-field"><label>Currency *</label><select className="form-input" value={form.currency} onChange={e=>setForm(v=>({...v,currency:e.target.value}))}><option>USD</option><option>ZWL</option></select></div>
   <div className="form-field" style={{gridColumn:"span 2"}}><label>Description *</label><input className="form-input" value={form.description} onChange={e=>setForm(v=>({...v,description:e.target.value}))} placeholder="Business purpose of the journal"/></div>
  </div></section>
  <section className="card form-card"><div className="section-title"><span>Journal Lines</span><button className="btn" onClick={addLine}>Add Line</button></div>
   <div className="table-wrap"><table className="table"><thead><tr><th>G/L Account</th><th>Debit</th><th>Credit</th><th>Cost Center</th><th>Profit Center</th><th>Text</th><th></th></tr></thead><tbody>
    {lines.map((x,i)=><tr key={i}><td><select className="form-input" value={x.accountCode} onChange={e=>setLine(i,"accountCode",e.target.value)}><option value="">Select account</option>{accounts.map(a=><option key={a.accountCode} value={a.accountCode}>{a.accountCode} — {a.accountName}</option>)}</select></td><td><input className="form-input" type="number" min="0" step="0.01" value={x.debit} onChange={e=>setLine(i,"debit",e.target.value)}/></td><td><input className="form-input" type="number" min="0" step="0.01" value={x.credit} onChange={e=>setLine(i,"credit",e.target.value)}/></td><td><input className="form-input" value={x.costCenter} onChange={e=>setLine(i,"costCenter",e.target.value)} placeholder="Optional"/></td><td><input className="form-input" value={x.profitCenter} onChange={e=>setLine(i,"profitCenter",e.target.value)} placeholder="Optional"/></td><td><input className="form-input" value={x.lineText} onChange={e=>setLine(i,"lineText",e.target.value)}/></td><td><button className="btn" disabled={lines.length<=2} onClick={()=>removeLine(i)}>Remove</button></td></tr>)}
   </tbody></table></div>
   <div style={{display:"flex",justifyContent:"flex-end",gap:24,padding:"16px 18px",fontWeight:650}}><span>Total Debit: {form.currency} {debit.toFixed(2)}</span><span>Total Credit: {form.currency} {credit.toFixed(2)}</span><span>{balanced?"BALANCED":"NOT BALANCED"}</span></div>
  </section>
 </div>;
}
