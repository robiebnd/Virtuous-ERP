"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const groups = [
  { title: "Workspace", items: [{ label: "Dashboard", href: "/", icon: "⌂" }] },
  { title: "Procurement", items: [
    { label: "Purchase Requisitions", href: "/procurement/purchase-requisitions", icon: "▤" },
    { label: "Purchase Orders", href: "/procurement/purchase-orders", icon: "▥" },
    { label: "Goods Receipts", href: "/procurement/goods-receipts", icon: "⇩" },
    { label: "Vendor Invoices", href: "/procurement/vendor-invoices", icon: "▣" },
    { label: "Vendor Payments", href: "/procurement/vendor-payments", icon: "$" },
  ] },
  { title: "Warehouse", items: [
    { label: "Warehouse Monitor", href: "/warehouse/monitor", icon: "⌘" },
    { label: "Products", href: "/warehouse/products", icon: "□" },
    { label: "Stock & Bins", href: "/warehouse/stock", icon: "▦" },
  ] },
  { title: "Master Data", items: [
    { label: "Suppliers", href: "/master-data/suppliers", icon: "♙" },
    { label: "Warehouses", href: "/master-data/warehouses", icon: "⌂" },
  ] },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const crumbs = pathname === "/"
    ? "Dashboard"
    : pathname.split("/").filter(Boolean).map((x) => x.replaceAll("-", " ")).join(" / ");

  return <div className="app">
    <aside className="sidebar">
      <div className="brand">
        <Link href="/" className="brand-logo-link" aria-label="Virtuous ERP home">
          <img src="/virtuous-logo.png" alt="Virtuous ERP" className="brand-logo" />
        </Link>
      </div>
      <nav className="nav" aria-label="Main navigation">
        {groups.map((group) => <div className="nav-group" key={group.title}>
          <div className="nav-section">{group.title}</div>
          {group.items.map((item) => {
            const active = pathname === item.href || (item.href !== "/" && pathname.startsWith(item.href));
            return <Link key={item.href} href={item.href} className={`nav-link ${active ? "active" : ""}`}>
              <span className="nav-icon" aria-hidden="true">{item.icon}</span><span>{item.label}</span>
            </Link>;
          })}
        </div>)}
      </nav>
      <div className="sidebar-footer"><span>Virtuous ERP</span><small>Warehouse & Procurement</small></div>
    </aside>

    <main className="main">
      <header className="topbar">
        <button className="mobile-menu" aria-label="Open navigation">☰</button>
        <div className="crumb">Virtuous ERP <span>/</span> {crumbs}</div>
        <div className="global-search"><span aria-hidden="true">⌕</span><input aria-label="Search" placeholder="Search in Virtuous ERP" /><kbd>Ctrl K</kbd></div>
        <div className="top-actions">
          <button className="icon-btn" aria-label="Notifications">♢</button>
          <button className="icon-btn" aria-label="Help">?</button>
          <button className="avatar" aria-label="User profile">RB</button>
        </div>
      </header>
      {children}
    </main>
  </div>;
}
