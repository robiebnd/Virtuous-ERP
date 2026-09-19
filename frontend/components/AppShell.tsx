"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useRef, useState } from "react";

type IconName = "home" | "refresh" | "users" | "bag" | "money" | "list" | "document" | "hierarchy" | "clock" | "flag" | "star" | "tag" | "warehouse" | "cycleClosure" | "goodsIssue";

function Icon({ name }: { name: IconName }) {
  const common = { width: 19, height: 19, viewBox: "0 0 24 24", fill: "none", stroke: "currentColor", strokeWidth: 1.8, strokeLinecap: "round" as const, strokeLinejoin: "round" as const, "aria-hidden": true };
  const paths: Record<IconName, React.ReactNode> = {
    home: <><path d="m3 10 9-7 9 7"/><path d="M5 9v11h14V9"/><path d="M9 20v-6h6v6"/></>,
    refresh: <><path d="M20 11a8 8 0 0 0-14.8-3L3 10"/><path d="M3 5v5h5"/><path d="M4 13a8 8 0 0 0 14.8 3L21 14"/><path d="M21 19v-5h-5"/></>,
    cycleClosure: <><path d="M20 11a8 8 0 0 0-14.8-3L3 10"/><path d="M3 5v5h5"/><path d="M4 13a8 8 0 0 0 14.8 3L21 14"/><path d="M21 19v-5h-5"/><path d="m8.5 12 2.1 2.1 4.9-5"/></>,
    goodsIssue: <><path d="M3 17h13V7H8l-2 4H3z"/><path d="M16 11h3l2 3v3h-5z"/><circle cx="7" cy="18" r="2"/><circle cx="18" cy="18" r="2"/><path d="M9 18h7"/><path d="M8 7v4h4"/></>,
    users: <><circle cx="9" cy="8" r="3"/><path d="M3 20c.6-3.3 2.5-5 6-5s5.4 1.7 6 5"/><path d="M16 5.5a3 3 0 0 1 0 5.8"/><path d="M18 15c1.8.7 2.8 2.3 3 5"/></>,
    bag: <><path d="M4 8h16v12H4z"/><path d="M8 8V5h8v3"/><path d="M4 12h16"/></>,
    money: <><circle cx="12" cy="12" r="8"/><path d="M12 7v10M15 9.5c-.8-1-4-1.2-4.3.5-.3 1.7 4.5 1.3 4.3 3.2-.2 1.8-3.7 1.9-4.7.6"/></>,
    list: <><path d="M8 6h12M8 12h12M8 18h12"/><path d="M4 6h.01M4 12h.01M4 18h.01"/></>,
    document: <><path d="M6 3h9l3 3v15H6z"/><path d="M15 3v4h4M9 12h6M9 16h6"/></>,
    hierarchy: <><rect x="9" y="3" width="6" height="4" rx="1"/><rect x="3" y="17" width="6" height="4" rx="1"/><rect x="15" y="17" width="6" height="4" rx="1"/><path d="M12 7v5M6 17v-3h12v3"/></>,
    clock: <><circle cx="12" cy="12" r="8"/><path d="M12 7v5l3 2"/></>,
    flag: <><path d="M6 21V4"/><path d="M6 5c4-3 7 3 12 0v8c-5 3-8-3-12 0"/></>,
    star: <path d="m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2-5.6-3-5.6 3 1.1-6.2L3 9.6l6.2-.9z"/>,
    tag: <><path d="M4 5v6l9 9 7-7-9-9z"/><circle cx="9" cy="9" r="1"/></>,
    warehouse: <><path d="m3 10 9-6 9 6v10H3z"/><path d="M7 20v-6h10v6M9 10h6"/></>,
  };
  return <svg {...common}>{paths[name]}</svg>;
}

const groups = [
  { title: "Workspace", items: [{ label: "Dashboard", href: "/", icon: "home" as IconName }] },
  { title: "Finance", items: [
    { label: "Finance Overview", href: "/finance", icon: "money" as IconName },
    { label: "Chart of Accounts", href: "/finance/chart-of-accounts", icon: "list" as IconName },
    { label: "Accounting Documents", href: "/finance/accounting-documents", icon: "document" as IconName },
    { label: "Trial Balance", href: "/finance/trial-balance", icon: "hierarchy" as IconName },
    { label: "Journal Entry", href: "/finance/journal-entry", icon: "document" as IconName },
    { label: "Finance Controls", href: "/finance/controls", icon: "hierarchy" as IconName },
    { label: "Accounts Payable", href: "/finance#accounts-payable", icon: "money" as IconName },
    { label: "Accounts Receivable", href: "/finance#accounts-receivable", icon: "money" as IconName },
  ] },
  { title: "Outbound Operations", items: [
    { label: "O2C Overview", href: "/order-to-cash", icon: "refresh" as IconName },
    { label: "Sales Orders", href: "/order-to-cash/sales-orders", icon: "list" as IconName },
    { label: "Deliveries", href: "/order-to-cash/deliveries", icon: "refresh" as IconName },
    { label: "Customer Invoices", href: "/order-to-cash/customer-invoices", icon: "document" as IconName },
    { label: "Accounts Receivable", href: "/order-to-cash/accounts-receivable", icon: "money" as IconName },
    { label: "Document Flow", href: "/order-to-cash/document-flow", icon: "hierarchy" as IconName },
    { label: "Customers", href: "/order-to-cash/customers", icon: "users" as IconName },
  ] },
  { title: "Procurement", items: [
    { label: "Purchase Requisitions", href: "/procurement/purchase-requisitions", icon: "document" as IconName },
    { label: "Purchase Orders", href: "/procurement/purchase-orders", icon: "bag" as IconName },
    { label: "Goods Receipts", href: "/procurement/goods-receipts", icon: "refresh" as IconName },
    { label: "Cycle Closure", href: "/procurement/cycle-closure", icon: "cycleClosure" as IconName },
    { label: "Goods Issues", href: "/procurement/goods-issues", icon: "goodsIssue" as IconName },
    { label: "GR/IR Reconciliation", href: "/procurement/gr-ir-reconciliation", icon: "hierarchy" as IconName },
    { label: "Vendor Invoices", href: "/procurement/vendor-invoices", icon: "money" as IconName },
    { label: "Vendor Payments", href: "/procurement/vendor-payments", icon: "money" as IconName },
    { label: "Vendor Evaluation", href: "/procurement/vendor-evaluation", icon: "star" as IconName },
  ] },
  { title: "Warehouse", items: [
    { label: "Warehouse Monitor", href: "/warehouse/monitor", icon: "warehouse" as IconName },
    { label: "Products", href: "/warehouse/products", icon: "list" as IconName },
    { label: "Stock & Bins", href: "/warehouse/stock", icon: "hierarchy" as IconName },
  ] },
  { title: "Master Data", items: [
    { label: "Suppliers", href: "/master-data/suppliers", icon: "users" as IconName },
    { label: "Warehouses", href: "/master-data/warehouses", icon: "warehouse" as IconName },
  ] },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const [profileOpen, setProfileOpen] = useState(false);
  const profileRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleOutsideClick = (event: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(event.target as Node)) {
        setProfileOpen(false);
      }
    };
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") setProfileOpen(false);
    };
    document.addEventListener("mousedown", handleOutsideClick);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
      document.removeEventListener("keydown", handleEscape);
    };
  }, []);
  const crumbs = pathname === "/" ? "Dashboard" : pathname.split("/").filter(Boolean).map((x) => x.replaceAll("-", " ")).join(" / ");
  return <div className="app"><aside className="sidebar"><div className="brand"><Link href="/" className="brand-logo-link" aria-label="Virtuous ERP home"><img src="/virtuous-logo.png" alt="Virtuous ERP" className="brand-logo" /></Link></div><nav className="nav" aria-label="Main navigation">{groups.map((group) => <div className="nav-group" key={group.title}><div className="nav-section">{group.title}</div>{group.items.map((item) => { const active = pathname === item.href || (item.href !== "/" && item.href.indexOf("#") === -1 && pathname.startsWith(item.href)); return <Link key={item.href} href={item.href} className={`nav-link ${active ? "active" : ""}`}><span className="nav-icon"><Icon name={item.icon} /></span><span>{item.label}</span></Link>; })}</div>)}</nav><div className="sidebar-footer"><span>Virtuous ERP</span><small>Finance, Warehouse, Procurement & Outbound Operations</small></div></aside><main className="main"><header className="topbar"><button className="mobile-menu" aria-label="Open navigation">☰</button><div className="crumb">Virtuous ERP <span>/</span> {crumbs}</div><div className="global-search"><span aria-hidden="true">⌕</span><input aria-label="Search" placeholder="Search in Virtuous ERP" /><kbd>Ctrl K</kbd></div><div className="top-actions"><button className="icon-btn" aria-label="Notifications"><Icon name="flag" /></button><button className="icon-btn" aria-label="Help">?</button><div className="profile-wrap" ref={profileRef}>
  <button className={`avatar ${profileOpen ? "avatar-open" : ""}`} aria-label="User profile" aria-haspopup="menu" aria-expanded={profileOpen} onClick={() => setProfileOpen((open) => !open)}>RB</button>
  {profileOpen && <div className="profile-menu" role="menu" aria-label="User profile menu">
    <div className="profile-header"><div className="profile-avatar">RB</div><div className="profile-identity"><strong>RB</strong><span>Virtuous ERP User</span></div></div>
    <div className="profile-divider" />
    <button className="profile-menu-item" role="menuitem" onClick={() => setProfileOpen(false)}><span className="profile-menu-icon">◉</span><span>My Profile</span></button>
    <button className="profile-menu-item" role="menuitem" onClick={() => setProfileOpen(false)}><span className="profile-menu-icon">⚙</span><span>Settings</span></button>
    <div className="profile-divider" />
    <button className="profile-menu-item profile-signout" role="menuitem" onClick={() => setProfileOpen(false)}><span className="profile-menu-icon">↪</span><span>Sign out</span></button>
  </div>}
</div></div></header>{children}</main></div>;
}
