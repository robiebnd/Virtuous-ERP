"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useRef, useState } from "react";

const groups = [
  { title: "Workspace", items: [{ label: "Dashboard", href: "/", icon: "⌂" }] },
  { title: "Order to Cash", items: [
    { label: "O2C Overview", href: "/order-to-cash", icon: "↗" },
    { label: "Sales Orders", href: "/order-to-cash/sales-orders", icon: "▥" },
    { label: "Deliveries", href: "/order-to-cash/deliveries", icon: "⇧" },
    { label: "Customer Invoices", href: "/order-to-cash/customer-invoices", icon: "▣" },
    { label: "Accounts Receivable", href: "/order-to-cash/accounts-receivable", icon: "$" },
    { label: "Customers", href: "/order-to-cash/customers", icon: "♙" },
  ] },
  { title: "Procurement", items: [
    { label: "Purchase Requisitions", href: "/procurement/purchase-requisitions", icon: "▤" },
    { label: "Purchase Orders", href: "/procurement/purchase-orders", icon: "▥" },
    { label: "Goods Receipts", href: "/procurement/goods-receipts", icon: "⇩" },
    { label: "Cycle Closure", href: "/procurement/cycle-closure", icon: "↻" },
    { label: "Goods Issues", href: "/procurement/goods-issues", icon: "⇧" },
    { label: "GR/IR Reconciliation", href: "/procurement/gr-ir-reconciliation", icon: "≡" },
    { label: "Vendor Invoices", href: "/procurement/vendor-invoices", icon: "▣" },
    { label: "Vendor Payments", href: "/procurement/vendor-payments", icon: "$" },
    { label: "Vendor Evaluation", href: "/procurement/vendor-evaluation", icon: "★" },
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

  const crumbs = pathname === "/"
    ? "Dashboard"
    : pathname.split("/").filter(Boolean).map((x) => x.replaceAll("-", " ")).join(" / ");

  return <div className="app">
    <aside className="sidebar">
      <div className="brand">
        <Link href="/" className="brand-logo-link" aria-label="Virtuous ERP home">
          <img src="https://raw.githubusercontent.com/robiebnd/Virtuous-ERP/main/frontend/public/virtuous-logo.png" alt="Virtuous ERP" className="brand-logo" />
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
      <div className="sidebar-footer"><span>Virtuous ERP</span><small>Warehouse, Procurement & Order to Cash</small></div>
    </aside>

    <main className="main">
      <header className="topbar">
        <button className="mobile-menu" aria-label="Open navigation">☰</button>
        <div className="crumb">Virtuous ERP <span>/</span> {crumbs}</div>
        <div className="global-search"><span aria-hidden="true">⌕</span><input aria-label="Search" placeholder="Search in Virtuous ERP" /><kbd>Ctrl K</kbd></div>
        <div className="top-actions">
          <button className="icon-btn" aria-label="Notifications">♢</button>
          <button className="icon-btn" aria-label="Help">?</button>

          <div className="profile-wrap" ref={profileRef}>
            <button
              className={`avatar ${profileOpen ? "avatar-open" : ""}`}
              aria-label="User profile"
              aria-haspopup="menu"
              aria-expanded={profileOpen}
              onClick={() => setProfileOpen((open) => !open)}
            >
              RB
            </button>

            {profileOpen && (
              <div className="profile-menu" role="menu" aria-label="User profile menu">
                <div className="profile-header">
                  <div className="profile-avatar">RB</div>
                  <div className="profile-identity">
                    <strong>RB</strong>
                    <span>Virtuous ERP User</span>
                  </div>
                </div>

                <div className="profile-divider" />

                <button className="profile-menu-item" role="menuitem" onClick={() => setProfileOpen(false)}>
                  <span className="profile-menu-icon">◉</span>
                  <span>My Profile</span>
                </button>
                <button className="profile-menu-item" role="menuitem" onClick={() => setProfileOpen(false)}>
                  <span className="profile-menu-icon">⚙</span>
                  <span>Settings</span>
                </button>

                <div className="profile-divider" />

                <button className="profile-menu-item profile-signout" role="menuitem" onClick={() => setProfileOpen(false)}>
                  <span className="profile-menu-icon">↪</span>
                  <span>Sign out</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </header>
      {children}
    </main>
  </div>;
}
