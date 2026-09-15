"use client";

import { useEffect, useState } from "react";
import { procurementApi, PurchaseRequisition } from "@/lib/api";

export default function RequisitionDetail({params}:{params:Promise<{id:string}>}){
 const [id,setId]=useState(""); const [data,setData]=useState<PurchaseRequisition|null>(null);
 useEffect(()=>{params.then(p=>{setId(p.id);procurementApi.requisition(p.id).then(setData).catch(()=>setData({id:p.id,requisitionNumber:`PR-${p.id}`,status:"PENDING_APPROVAL",documentType:"NB",purchasingGroup:"PURCHASING",plantCode:"MAIN",storageLocation:"MAIN",currency:"USD",totalValue:0}))})},[params]);
 if(!data)return <div className="content"><div className="empty">Loading requisition…</div></div>;
 const field=(label:string,value:any)=><div style={{padding:"12px 0",borderBottom:"1px solid #e7e9e5"}}><div className="muted">{label}</div><div style={{marginTop:5,fontSize:14}}>{value||"—"}</div></div>;
 return <div className="content"><div className="page-head"><div><p>Purchase Requisition</p><h1>{data.requisitionNumber||id}</h1><p>Procurement document • {data.documentType||"NB"}</p></div><div className="actions"><button className="btn">Edit</button><button className="btn mustard">Submit for Approval</button></div></div>
  <div className="card"><div className="toolbar"><span className={`status ${data.status?.toLowerCase().includes("approved")?"approved":"pending"}`}>{(data.status||"DRAFT").replaceAll("_"," ")}</span><span className="muted">Created procurement requirement</span></div>
   <div style={{padding:"20px 24px"}}><h2 style={{fontSize:17,fontWeight:600}}>General Data</h2><div className="grid" style={{gridTemplateColumns:"repeat(3,1fr)",gap:"0 35px"}}>{field("Document Type",data.documentType)}{field("Purchasing Group",data.purchasingGroup)}{field("Plant",data.plantCode)}{field("Storage Location",data.storageLocation)}{field("Currency",data.currency)}{field("Requested Delivery",data.requestedDeliveryDate&&new Date(data.requestedDeliveryDate).toLocaleString())}{field("Valuation Price",data.valuationPrice?.toFixed(2))}{field("Total Value",data.totalValue?.toFixed(2))}</div><h2 style={{fontSize:17,fontWeight:600,marginTop:30}}>Process Flow</h2><div className="list-row"><span>Purchase Requisition</span><span className="status approved">Created</span></div><div className="list-row"><span>Approval</span><span className={`status ${data.status?.includes("APPROVED")?"approved":"pending"}`}>{data.status?.includes("APPROVED")?"Approved":"Pending"}</span></div><div className="list-row"><span>Purchase Order</span><span className="muted">Created from approved PR</span></div><div className="list-row"><span>Goods Receipt</span><span className="muted">Load PO lines when PO exists</span></div></div>
  </div></div>
}
