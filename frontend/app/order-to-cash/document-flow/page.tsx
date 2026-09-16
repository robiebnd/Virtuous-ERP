"use client";

import { useState } from "react";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";
import { orderToCashApi } from "@/lib/api";

type FlowEntry = { documentType?: string; documentNumber?: string; status?: string; relationship?: string };

type FlowResponse = { rootDocumentNumber?: string; customerCode?: string; flow?: FlowEntry[] };

export default function DocumentFlowPage() {
  const [salesOrderId, setSalesOrderId] = useState("");
  const [data, setData] = useState<FlowResponse | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function lookup() {
    if (!salesOrderId.trim()) return;
    setLoading(true); setError("");
    try {
      setData(await orderToCashApi.documentFlow(salesOrderId.trim()));
    } catch (e) {
      setData(null);
      setError(e instanceof Error ? e.message : "Unable to load document flow");
    } finally { setLoading(false); }
  }

  const rows = (data?.flow ?? []).map((x) => ({
    Document: x.documentNumber ?? "—",
    Type: (x.documentType ?? "—").replaceAll("_", " "),
    Relationship: x.relationship ?? "—",
    Status: x.status ?? "—",
  }));

  return <div>
    <div className="page-header">
      <div><div className="eyebrow">ORDER TO CASH / DOCUMENT FLOW</div><h1>Document Flow</h1><p>Trace the SAP-style commercial document chain from sales order through delivery, billing, settlement and follow-up.</p></div>
    </div>
    <section className="panel" style={{ marginBottom: 18 }}>
      <div className="panel-title">Document Flow Explorer</div>
      <div className="form-grid">
        <label>Sales Order ID<input value={salesOrderId} onChange={e => setSalesOrderId(e.target.value)} placeholder="UUID of the sales order" /></label>
        <div className="form-actions"><button className="btn primary" onClick={lookup} disabled={loading}>{loading ? "Loading..." : "Display Flow"}</button></div>
      </div>
      {error && <div className="alert error">{error}</div>}
      {data && <div className="process-strip" style={{ marginTop: 18 }}>
        <div><small>ROOT DOCUMENT</small><strong>{data.rootDocumentNumber ?? "—"}</strong></div>
        <div><small>CUSTOMER</small><strong>{data.customerCode ?? "—"}</strong></div>
        <div><small>DOCUMENTS</small><strong>{data.flow?.length ?? 0}</strong></div>
      </div>}
    </section>
    <ModuleWorkspace eyebrow="ORDER TO CASH / TRACEABILITY" title="Commercial Document Chain" description="Use document flow to trace predecessor and settlement relationships without losing the original business document context." columns={["Document","Type","Relationship","Status"]} rows={rows} searchPlaceholder="Search document number or type..." />
  </div>;
}
