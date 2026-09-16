"use client";
import {useEffect,useState} from "react";
import {ModuleWorkspace} from "@/components/ModuleWorkspace";
import {orderToCashApi} from "@/lib/api";

export default function SalesOrders(){
  const [orders,setOrders]=useState<any[]>([]); const [loading,setLoading]=useState(true);
  useEffect(()=>{orderToCashApi.salesOrders().then(setOrders).catch(()=>setOrders([])).finally(()=>setLoading(false));},[]);
  const rows=orders.map(o=>({Document:o.orderNumber||o.sapOrderNumber||o.id,Customer:o.customerCode||"—",Items:String(o.items?.length??0),Value:`USD ${Number(o.totalAmount||0).toFixed(2)}`,Status:o.status||"DRAFT",Delivery:o.orderDate?new Date(o.orderDate).toLocaleDateString():"—"}));
  const open=orders.filter(o=>!String(o.status).includes("CANCEL")).length;
  const value=orders.reduce((sum,o)=>sum+Number(o.totalAmount||0),0);
  return <ModuleWorkspace eyebrow="ORDER TO CASH / SALES" title="Sales Orders" description="Capture customer demand, apply pricing and payment terms, create orders and follow the downstream document flow." createHref="/order-to-cash/sales-orders/new" createLabel="Create Sales Order" stats={[{label:"Sales Orders",value:String(open),foot:loading?"Loading live orders":"Live backend records"},{label:"Order Value",value:`USD ${value.toFixed(2)}`,foot:"Returned by sales-order API"},{label:"SAP Pending",value:String(orders.filter(o=>String(o.status)==="PENDING_SAP").length),foot:"Awaiting SAP response",tone:"warning"},{label:"Created",value:String(orders.filter(o=>String(o.status)==="CREATED").length),foot:"Created orders",tone:"success"}]} process={[{label:"Create",detail:"Customer & items",status:"API",tone:"approved"},{label:"SAP",detail:"Optional integration",status:"Configured",tone:"ready"},{label:"Deliver",detail:"Outbound delivery",status:"Next",tone:"pending"},{label:"Bill",detail:"Customer invoice",status:"Next",tone:"draft"}]} columns={["Document","Customer","Items","Value","Status","Delivery"]} rows={rows} searchPlaceholder="Search sales order or customer..." />;
}
