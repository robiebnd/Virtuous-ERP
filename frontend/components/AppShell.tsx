"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const groups = [
  { title: "Workspace", items: [{label:"Dashboard",href:"/",icon:"⌂"}] },
  { title: "Procurement", items: [
    {label:"Purchase Requisitions",href:"/procurement/purchase-requisitions",icon:"▤"},
    {label:"Purchase Orders",href:"/procurement/purchase-orders",icon:"▥"},
    {label:"Goods Receipts",href:"/procurement/goods-receipts",icon:"⇩"},
    {label:"Vendor Invoices",href:"/procurement/vendor-invoices",icon:"▣"},
    {label:"Vendor Payments",href:"/procurement/vendor-payments",icon:"$"},
  ]},
  { title: "Warehouse", items: [
    {label:"Warehouse Monitor",href:"/warehouse/monitor",icon:"⌘"},
    {label:"Products",href:"/warehouse/products",icon:"□"},
    {label:"Stock & Bins",href:"/warehouse/stock",icon:"▦"},
  ]},
  { title: "Master Data", items: [
    {label:"Suppliers",href:"/master-data/suppliers",icon:"♙"},
    {label:"Warehouses",href:"/master-data/warehouses",icon:"⌂"},
  ]},
];

export function AppShell({children}:{children:React.ReactNode}){
  const pathname=usePathname();
  return <div className="app">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark">VE</div><div className="brand-text">Virtuous ERP<small>WAREHOUSE & PROCUREMENT</small></div></div>
      <nav className="nav">
        {groups.map(group=><div key={group.title}><div className="nav-section">{group.title}</div>{group.items.map(item=><Link key={item.href} href={item.href} className={`nav-link ${pathname===item.href || (item.href!=="/"&&pathname.startsWith(item.href))?"active":""}`}><span className="nav-icon">{item.icon}</span><span>{item.label}</span></Link>)}</div>)}
      </nav>
    </aside>
    <main className="main">
      <header className="topbar"><div className="crumb">Virtuous ERP / {pathname==="/"?"Dashboard":pathname.split("/").filter(Boolean).map(x=>x.replaceAll("-"," ")).join(" / ")}</div><input className="search" placeholder="Search in Virtuous ERP" /><div className="top-actions"><button className="icon-btn" aria-label="notifications">♢</button><button className="icon-btn" aria-label="help">?</button><div className="avatar">RB</div></div></header>
      {children}
    </main>
  </div>;
}
