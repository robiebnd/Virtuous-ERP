"use client";
import {useEffect,useState} from "react";
import {api} from "@/lib/api";

const demo=[
  {id:"1",paymentNumber:"VPAY-000007",invoiceNumber:"VINV-000019",supplier:"Supplier A",amount:2500,currency:"USD",method:"BANK_TRANSFER",status:"DRAFT"},
  {id:"2",paymentNumber:"VPAY-000006",invoiceNumber:"VINV-000017",supplier:"Supplier B",amount:1250,currency:"USD",method:"BANK_TRANSFER",status:"PAID"}
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
  const [rows,setRows]=useState<any[]>(demo);
  useEffect(()=>{api<any[]>("/api/vendor-payments").then(r=>setRows(r.length?r:demo)).catch(()=>{})},[]);

  return <div className="content">
    <div className="page-head">
      <div><div className="eyebrow">PROCUREMENT / ACCOUNTS PAYABLE</div><h1>Vendor Payments</h1><p>Accounts payable settlement for verified supplier invoices.</p></div>
      <button className="btn primary">Create Vendor Payment</button>
    </div>
    <section className="card">
      <div className="toolbar">
        <input className="filter" style={{flex:1,minWidth:240}} placeholder="Search payment or invoice..."/>
        <select className="filter"><option>All statuses</option><option>DRAFT</option><option>APPROVED</option><option>PAID</option><option>CANCELLED</option></select>
      </div>
      <div className="table-caption"><strong>Vendor Payments ({rows.length})</strong><span>Supplier settlement</span></div>
      <div className="table-wrap"><table className="table">
        <thead><tr><th>Payment</th><th>Invoice</th><th>Supplier</th><th>Date</th><th>Amount</th><th>Method</th><th>Status</th></tr></thead>
        <tbody>{rows.map(r=><tr key={r.id}>
          <td className="link">{r.paymentNumber||r.number||r.id}</td>
          <td>{r.vendorInvoice?.invoiceNumber||r.invoiceNumber||"—"}</td>
          <td>{r.supplier?.name||r.supplier||"—"}</td>
          <td>{r.paymentDate?new Date(r.paymentDate).toLocaleDateString():"—"}</td>
          <td>{r.currency||"USD"} {Number(r.amount||0).toFixed(2)}</td>
          <td>{String(r.paymentMethod||r.method||"—").replaceAll("_"," ")}</td>
          <td><span className={`status ${statusClass(r.status)}`}>{statusLabel(r.status)}</span></td>
        </tr>)}</tbody>
      </table></div>
    </section>
  </div>
}
