"use client";
import Link from "next/link";
import {useEffect,useMemo,useState} from "react";
import {procurementApi} from "@/lib/api";

const demo=[
  {id:"1",paymentNumber:"VP-000007",invoiceNumber:"VINV-000019",supplierName:"Supplier A",amount:2500,currency:"USD",paymentMethod:"BANK_TRANSFER",status:"DRAFT"},
  {id:"2",paymentNumber:"VP-000006",invoiceNumber:"VINV-000017",supplierName:"Supplier B",amount:1250,currency:"USD",paymentMethod:"BANK_TRANSFER",status:"PAID"}
];

const statusClass=(s?:string)=>{
  const value=String(s||"DRAFT").toLowerCase();
  if(value.includes("paid")) return "approved";
  if(value.includes("approved")) return "ready";
  if(value.includes("cancelled")||value.includes("blocked")) return "blocked";
  return "draft";
};
const statusLabel=(s?:string)=>String(s||"DRAFT").toLowerCase().split("_").map((word,i)=>i===0?word.charAt(0).toUpperCase()+word.slice(1):word).join(" ");

export default function VendorPayments(){
  const [rows,setRows]=useState<any[]>(demo); const [query,setQuery]=useState(""); const [status,setStatus]=useState("ALL"); const [loading,setLoading]=useState(true);
  const load=()=>{setLoading(true); procurementApi.vendorPayments().then(r=>setRows(r.length?r:[])).catch(()=>setRows([])).finally(()=>setLoading(false));};
  useEffect(load,[]);
  const filtered=useMemo(()=>rows.filter(r=>{const text=JSON.stringify(r).toLowerCase(); return (!query||text.includes(query.toLowerCase()))&&(status==="ALL"||String(r.status||"")===status)}),[rows,query,status]);
  const approve=async(id:string)=>{await procurementApi.approveVendorPayment(id);load();};
  const pay=async(id:string)=>{await procurementApi.payVendorPayment(id);load();};

  return <div className="content">
    <div className="page-head"><div><div className="eyebrow">PROCUREMENT / ACCOUNTS PAYABLE</div><h1>Vendor Payments</h1><p>Accounts payable settlement for matched and posted supplier invoices.</p></div><div className="actions"><Link className="btn primary" href="/procurement/vendor-payments/new">Create Vendor Payment</Link></div></div>
    <section className="card"><div className="toolbar"><input className="filter" style={{flex:1,minWidth:240}} value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search payment, invoice or supplier..."/><select className="filter" value={status} onChange={e=>setStatus(e.target.value)}><option value="ALL">All statuses</option><option>DRAFT</option><option>APPROVED</option><option>PAID</option><option>CANCELLED</option></select></div>
      <div className="table-caption"><strong>Vendor Payments ({filtered.length})</strong><span>{loading?"Loading supplier settlements…":"Live AP data"}</span></div>
      <div className="table-wrap"><table className="table"><thead><tr><th>Payment</th><th>Invoice</th><th>Supplier</th><th>Date</th><th>Amount</th><th>Method</th><th>Status</th><th>Actions</th></tr></thead><tbody>
      {filtered.map(r=><tr key={r.id}><td className="link">{r.paymentNumber||r.id}</td><td>{r.invoiceNumber||"—"}</td><td>{r.supplierName||r.supplier?.name||"—"}</td><td>{r.paymentDate?new Date(r.paymentDate).toLocaleDateString():"—"}</td><td>{r.currency||"USD"} {Number(r.amount||0).toFixed(2)}</td><td>{String(r.paymentMethod||"—").replaceAll("_"," ")}</td><td><span className={`status ${statusClass(r.status)}`}>{statusLabel(r.status)}</span></td><td>{r.status==="DRAFT"?<button className="btn" onClick={()=>approve(r.id)}>Approve</button>:r.status==="APPROVED"?<button className="btn mustard" onClick={()=>pay(r.id)}>Pay</button>:<span className="muted">—</span>}</td></tr>)}
      {!filtered.length&&!loading&&<tr><td colSpan={8} className="empty">No vendor payments found.</td></tr>}
      </tbody></table></div></section>
  </div>
}
