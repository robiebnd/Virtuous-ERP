"use client";

import Link from "next/link";
import { useMemo, useState } from "react";

type Row = Record<string, string | number>;

type ModuleWorkspaceProps = {
  eyebrow?: string;
  title: string;
  description?: string;
  subtitle?: string;
  createHref?: string;
  createLabel?: string;
  stats?: Array<{ label: string; value: string; foot: string; tone?: "normal" | "warning" | "success" }>;
  process?: Array<{ label: string; detail: string; status: string; tone: "approved" | "pending" | "ready" | "blocked" | "draft" }>;
  columns: Array<string | { key: string; label: string }>;
  rows?: Row[];
  searchPlaceholder?: string;
  emptyText?: string;
  children?: React.ReactNode;
};

const statusClass = (value: string) => {
  const x = value.toLowerCase();
  if (x.includes("approved") || x.includes("matched") || x.includes("paid") || x.includes("closed") || x.includes("complete")) return "approved";
  if (x.includes("pending") || x.includes("due") || x.includes("open")) return "pending";
  if (x.includes("ready") || x.includes("released") || x.includes("posted")) return "ready";
  if (x.includes("blocked") || x.includes("overdue") || x.includes("exception")) return "blocked";
  return "draft";
};

export function ModuleWorkspace({ eyebrow = "WORKSPACE", title, description, subtitle, createHref, createLabel = "Create", stats = [], process = [], columns, rows = [], searchPlaceholder = "Search...", emptyText = "No records found.", children }: ModuleWorkspaceProps) {
  const [query, setQuery] = useState("");
  const [activeStatus, setActiveStatus] = useState("ALL");
  const filtered = useMemo(() => rows.filter((row) => {
    const values = Object.values(row).join(" ").toLowerCase();
    const status = String(row.Status ?? row.status ?? "").toUpperCase();
    return (!query || values.includes(query.toLowerCase())) && (activeStatus === "ALL" || status === activeStatus);
  }), [rows, query, activeStatus]);
  const statuses = Array.from(new Set(rows.map((r) => String(r.Status ?? r.status ?? "")).filter(Boolean)));
  const columnDefs = columns.map((column) => typeof column === "string" ? { key: column, label: column } : column);

  return <div className="content">
    <div className="page-head">
      <div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{description ?? subtitle ?? ""}</p></div>
      {createHref && <div className="actions"><Link className="btn primary" href={createHref}>{createLabel}</Link></div>}
    </div>

    <div className="grid stats">
      {stats.map((s) => <div className="card stat" key={s.label}><div className="stat-label">{s.label}</div><div className="stat-value">{s.value}</div><div className={`stat-foot ${s.tone === "warning" ? "warning-text" : s.tone === "success" ? "success-text" : ""}`}>{s.foot}</div></div>)}
    </div>

    {children}

    <section className="card" style={{ marginBottom: 18 }}>
      <div className="section-title"><span>Process Monitor</span><span className="section-meta">Current status</span></div>
      <div className="process">
        {process.map((step, index) => <div className="process-step" key={step.label}>
          <b className={step.tone === "approved" || step.tone === "ready" ? "active" : ""}>{index + 1}</b>
          <span>{step.label}</span><small>{step.detail}</small>
          <span className={`status ${step.tone}`}>{step.status}</span>
        </div>)}
      </div>
    </section>

    <section className="card">
      <div className="toolbar">
        <input className="filter" style={{ flex: 1, minWidth: 260 }} value={query} onChange={(e) => setQuery(e.target.value)} placeholder={searchPlaceholder} />
        <select className="filter" value={activeStatus} onChange={(e) => setActiveStatus(e.target.value)}>
          <option value="ALL">All statuses</option>
          {statuses.map((s) => <option key={s} value={s}>{s.replaceAll("_", " ")}</option>)}
        </select>
      </div>
      <div className="table-caption"><strong>Records ({filtered.length})</strong><span>Operational workspace</span></div>
      <div className="table-wrap"><table className="table sap-table"><thead><tr>{columnDefs.map((c) => <th key={c.key}>{c.label}</th>)}</tr></thead><tbody>
        {filtered.map((row, index) => <tr key={`${String(row[columnDefs[0]?.key ?? ""] )}-${index}`}>{columnDefs.map(({ key: column }) => {
          const value = String(row[column] ?? "—");
          return <td key={column}>{column.toLowerCase() === "status" ? <span className={`status ${statusClass(value)}`}>{value.replaceAll("_", " ")}</span> : value}</td>;
        })}</tr>)}
        {!filtered.length && <tr><td colSpan={columnDefs.length} className="empty">{emptyText}</td></tr>}
      </tbody></table></div>
    </section>
  </div>;
}
