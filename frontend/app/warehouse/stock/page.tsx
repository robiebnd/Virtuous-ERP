"use client";

import {useEffect,useState} from "react";
import {ModuleWorkspace} from "@/components/ModuleWorkspace";
import {warehouseExecutionApi} from "@/lib/api";

export default function Stock(){
 const [rows,setRows]=useState<any[]>([]); const [loading,setLoading]=useState(true);
 useEffect(()=>{warehouseExecutionApi.inventory().then(data=>setRows(data.map((i:any)=>({Product:i.product?.sku||i.product?.name||"—",Warehouse:i.warehouse?.code||i.warehouse?.name||"—",Bin:i.bin?.code||i.bin?.name||"—",OnHand:Number(i.quantityOnHand||0),Reserved:Number(i.quantityReserved||0),Quality:Number(i.quantityQuality||0),Blocked:Number(i.quantityBlocked||0)})))).catch(()=>setRows([])).finally(()=>setLoading(false))},[]);
 return <ModuleWorkspace eyebrow="WAREHOUSE / INVENTORY" title="Stock & Bins" description="Monitor unrestricted, reserved, quality and blocked stock by warehouse and bin." stats={[
  {label:"Inventory Records",value:String(rows.length),foot:"Live warehouse balances"},
  {label:"On Hand",value:String(rows.reduce((s,r)=>s+r.OnHand,0)),foot:"Unrestricted quantity"},
  {label:"Quality Stock",value:String(rows.reduce((s,r)=>s+r.Quality,0)),foot:"Awaiting QM",tone:"warning"},
  {label:"Blocked Stock",value:String(rows.reduce((s,r)=>s+r.Blocked,0)),foot:"Quality blocked",tone:"warning"}
 ]} process={[
  {label:"Receive",detail:"Goods receipt",status:"Live",tone:"approved"},
  {label:"Reserve",detail:"Sales / operational allocation",status:"Live",tone:"ready"},
  {label:"Quality",detail:"Inspection stock",status:"Integrated",tone:"ready"},
  {label:"Transfer",detail:"Bin movement",status:"Live",tone:"ready"}
 ]} columns={["Product","Warehouse","Bin","OnHand","Reserved","Quality","Blocked"]} rows={rows} loading={loading} searchPlaceholder="Search product, warehouse or bin..." />;
}