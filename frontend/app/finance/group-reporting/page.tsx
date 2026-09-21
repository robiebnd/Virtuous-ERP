"use client";

import { useEffect, useState } from "react";
import { financeApi } from "@/lib/api";

export default function GroupReportingPage() {
  const [companies, setCompanies] = useState<any[]>([]);
  const [groups, setGroups] = useState<any[]>([]);
  const [units, setUnits] = useState<any[]>([]);
  const [runs, setRuns] = useState<any[]>([]);
  const [balances, setBalances] = useState<any[]>([]);
  const [nci, setNci] = useState<any[]>([]);
  const [mappings, setMappings] = useState<any[]>([]);
  const [intercompany, setIntercompany] = useState<any[]>([]);
  const [journals, setJournals] = useState<any[]>([]);
  const [audit, setAudit] = useState<any[]>([]);
  const [groupId, setGroupId] = useState("");
  const [runId, setRunId] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const [companyForm, setCompanyForm] = useState({
    companyCode: "",
    companyName: "",
    countryCode: "ZWE",
    functionalCurrency: "USD",
    reportingCurrency: "USD"
  });

  const [unitForm, setUnitForm] = useState({
    unitCode: "",
    unitName: "",
    companyCode: "ZW01",
    localCurrency: "USD",
    ownershipPercent: "100"
  });

  const [groupForm, setGroupForm] = useState({
    groupCode: "",
    groupName: "",
    reportingCurrency: "USD"
  });

  const [runForm, setRunForm] = useState({
    fiscalYear: String(new Date().getFullYear()),
    periodNumber: "1"
  });

  const [mappingForm, setMappingForm] = useState({
    groupId: "",
    companyCode: "ZW01",
    localAccountCode: "",
    groupAccountCode: "",
    groupAccountName: "",
    groupAccountType: "ASSET"
  });

  const [icForm, setIcForm] = useState({
    transactionNumber: "",
    sourceCompanyCode: "ZW01",
    targetCompanyCode: "ZW01",
    transactionDate: new Date().toISOString().slice(0, 10),
    currency: "USD",
    amount: "",
    sourceDebitAccountCode: "120000",
    sourceCreditAccountCode: "400000",
    targetDebitAccountCode: "500000",
    targetCreditAccountCode: "210000",
    description: "Intercompany transaction"
  });

  const load = async () => {
    setBusy(true);
    try {
      const [companyList, groupList, unitList] = await Promise.all([
        financeApi.companyCodes(),
        financeApi.consolidationGroups(),
        financeApi.consolidationUnits()
      ]);
      setCompanies(companyList);
      setGroups(groupList);
      setUnits(unitList);
      setIntercompany(await financeApi.intercompanyTransactions());
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load group reporting.");
    } finally {
      setBusy(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const selectGroup = async (id: string) => {
    setGroupId(id);
    setRunId("");
    setBalances([]);
    setNci([]);
    setJournals([]);
    setAudit([]);

    if (!id) return;

    try {
      setRuns(await financeApi.groupReportingRuns(id));
      setMappings(await financeApi.groupAccountMappings(id));
      setMappingForm((current) => ({ ...current, groupId: id }));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load consolidation group.");
    }
  };

  const create = async (kind: "company" | "unit" | "group") => {
    setBusy(true);
    try {
      if (kind === "company") {
        await financeApi.createCompanyCode(companyForm);
      }
      if (kind === "unit") {
        await financeApi.saveConsolidationUnit({
          ...unitForm,
          ownershipPercent: Number(unitForm.ownershipPercent)
        });
      }
      if (kind === "group") {
        await financeApi.saveConsolidationGroup(groupForm);
      }
      await load();
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Save failed.");
    } finally {
      setBusy(false);
    }
  };

  const addUnit = async (unitId: string) => {
    if (!groupId) return;
    try {
      await financeApi.addConsolidationUnit(groupId, unitId);
      await selectGroup(groupId);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to add unit.");
    }
  };

  const postIntercompany = async () => {
    setBusy(true);
    try {
      await financeApi.postIntercompany({
        ...icForm,
        amount: Number(icForm.amount)
      });
      setIntercompany(await financeApi.intercompanyTransactions());
      setError("");
    } catch (e) {
      setError(
        e instanceof Error
          ? e.message
          : "Unable to post intercompany transaction."
      );
    } finally {
      setBusy(false);
    }
  };

  const saveMapping = async () => {
    if (!groupId) return;
    try {
      await financeApi.createGroupAccountMapping({
        ...mappingForm,
        groupId
      });
      setMappings(await financeApi.groupAccountMappings(groupId));
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to save account mapping.");
    }
  };

  const runConsolidation = async () => {
    if (!groupId) return;
    setBusy(true);
    try {
      const result = await financeApi.runGroupReporting(groupId, {
        fiscalYear: Number(runForm.fiscalYear),
        periodNumber: Number(runForm.periodNumber)
      });
      setRuns((current) => [result, ...current]);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to run consolidation.");
    } finally {
      setBusy(false);
    }
  };

  const showRun = async (id: string) => {
    try {
      setRunId(id);
      const [balanceList, nciList, journalList, auditList] = await Promise.all([
        financeApi.groupReportingBalances(id),
        financeApi.groupReportingNci(id),
        financeApi.consolidationJournals(id),
        financeApi.consolidationRunAudit(id)
      ]);
      setBalances(balanceList);
      setNci(nciList);
      setJournals(journalList);
      setAudit(auditList);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load consolidation run.");
    }
  };

  return (
    <main className="content">
      <div className="page-head">
        <div>
          <div className="eyebrow">FINANCE / GROUP REPORTING</div>
          <h1>Multi-Company and Group Reporting</h1>
          <p>
            Legal entities, consolidation scope, intercompany postings,
            currency translation and consolidated balances.
          </p>
        </div>
        <div className="actions">
          <button className="btn primary" onClick={load} disabled={busy}>
            {busy ? "Working..." : "Refresh"}
          </button>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div
        className="grid"
        style={{
          gridTemplateColumns: "repeat(3,minmax(0,1fr))",
          marginBottom: 18
        }}
      >
        <section className="card form-card">
          <div className="section-title">Company Code</div>
          <div className="form-grid" style={{ gridTemplateColumns: "1fr" }}>
            {Object.entries(companyForm).map(([key, value]) => (
              <div className="form-field" key={key}>
                <label>{key}</label>
                <input
                  className="form-input"
                  value={value}
                  onChange={(e) =>
                    setCompanyForm({
                      ...companyForm,
                      [key]: e.target.value
                    })
                  }
                />
              </div>
            ))}
            <button className="btn primary" onClick={() => create("company")}>
              Create Company
            </button>
          </div>
        </section>

        <section className="card form-card">
          <div className="section-title">Consolidation Unit</div>
          <div className="form-grid" style={{ gridTemplateColumns: "1fr" }}>
            {Object.entries(unitForm).map(([key, value]) => (
              <div className="form-field" key={key}>
                <label>{key}</label>
                <input
                  className="form-input"
                  value={value}
                  onChange={(e) =>
                    setUnitForm({
                      ...unitForm,
                      [key]: e.target.value
                    })
                  }
                />
              </div>
            ))}
            <button className="btn primary" onClick={() => create("unit")}>
              Create Unit
            </button>
          </div>
        </section>

        <section className="card form-card">
          <div className="section-title">Consolidation Group</div>
          <div className="form-grid" style={{ gridTemplateColumns: "1fr" }}>
            {Object.entries(groupForm).map(([key, value]) => (
              <div className="form-field" key={key}>
                <label>{key}</label>
                <input
                  className="form-input"
                  value={value}
                  onChange={(e) =>
                    setGroupForm({
                      ...groupForm,
                      [key]: e.target.value
                    })
                  }
                />
              </div>
            ))}
            <button className="btn primary" onClick={() => create("group")}>
              Create Group
            </button>
          </div>
        </section>
      </div>

      <section className="card" style={{ marginBottom: 18 }}>
        <div className="table-caption">
          <strong>Company Codes</strong>
          <span>{companies.length} active legal entities</span>
        </div>
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th>Country</th>
                <th>Functional Currency</th>
                <th>Reporting Currency</th>
              </tr>
            </thead>
            <tbody>
              {companies.map((company) => (
                <tr key={company.id}>
                  <td>{company.companyCode}</td>
                  <td>{company.companyName}</td>
                  <td>{company.countryCode}</td>
                  <td>{company.functionalCurrency}</td>
                  <td>{company.reportingCurrency}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card" style={{ marginBottom: 18 }}>
        <div className="table-caption">
          <strong>Consolidation Structure</strong>
          <span>
            {groups.length} groups and {units.length} units
          </span>
        </div>
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Group</th>
                <th>Reporting Currency</th>
                <th>Run</th>
              </tr>
            </thead>
            <tbody>
              {groups.map((group) => (
                <tr key={group.id}>
                  <td>
                    <strong>{group.groupCode}</strong> — {group.groupName}
                  </td>
                  <td>{group.reportingCurrency}</td>
                  <td>
                    <button className="btn" onClick={() => selectGroup(group.id)}>
                      Select
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card" style={{ marginBottom: 18 }}>
        <div className="table-caption">
          <strong>Consolidation Units</strong>
          <span>Add a legal entity to the selected group</span>
        </div>
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Unit</th>
                <th>Company</th>
                <th>Currency</th>
                <th>Ownership</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {units.map((unit) => (
                <tr key={unit.id}>
                  <td>
                    {unit.unitCode} — {unit.unitName}
                  </td>
                  <td>{unit.companyCode}</td>
                  <td>{unit.localCurrency}</td>
                  <td>{Number(unit.ownershipPercent || 0).toFixed(2)}%</td>
                  <td>
                    <button
                      className="btn"
                      disabled={!groupId}
                      onClick={() => addUnit(unit.id)}
                    >
                      Add to selected group
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card intercompany-card" style={{ marginBottom: 18 }}>
        <div className="section-title">Intercompany Transaction</div>
        <p className="intercompany-description">
          Post balanced source and target company entries with explicit elimination accounts.
        </p>
        <div className="form-grid intercompany-form-grid">
          {Object.entries(icForm).map(([key, value]) => (
            <div className="form-field" key={key}>
              <label>{key}</label>
              <input
                className="form-input"
                type={
                  key === "amount"
                    ? "number"
                    : key === "transactionDate"
                      ? "date"
                      : "text"
                }
                value={value}
                onChange={(e) =>
                  setIcForm({
                    ...icForm,
                    [key]: e.target.value
                  })
                }
              />
            </div>
          ))}
          <div className="form-field intercompany-action">
            <label>&nbsp;</label>
            <button className="btn primary" onClick={postIntercompany} disabled={busy}>
              Post Intercompany
            </button>
          </div>
        </div>

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Transaction</th>
                <th>Date</th>
                <th>Source</th>
                <th>Target</th>
                <th>Amount</th>
                <th>Currency</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {intercompany.map((transaction) => (
                <tr key={transaction.id}>
                  <td>{transaction.transactionNumber}</td>
                  <td>{transaction.transactionDate}</td>
                  <td>{transaction.sourceCompanyCode}</td>
                  <td>{transaction.targetCompanyCode}</td>
                  <td>{Number(transaction.amount || 0).toFixed(2)}</td>
                  <td>{transaction.currency}</td>
                  <td>{transaction.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {groupId && (
        <>
          <section className="card" style={{ marginBottom: 18 }}>
            <div className="section-title">Group Chart of Accounts Mapping</div>
            <p className="muted">
              Map local company accounts to a common group reporting account
              before consolidation.
            </p>
            <div className="form-grid">
              <div className="form-field">
                <label>Company</label>
                <select
                  className="form-input"
                  value={mappingForm.companyCode}
                  onChange={(e) =>
                    setMappingForm({
                      ...mappingForm,
                      companyCode: e.target.value
                    })
                  }
                >
                  {companies.map((company) => (
                    <option key={company.id} value={company.companyCode}>
                      {company.companyCode}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-field">
                <label>Local Account</label>
                <input
                  className="form-input"
                  value={mappingForm.localAccountCode}
                  onChange={(e) =>
                    setMappingForm({
                      ...mappingForm,
                      localAccountCode: e.target.value
                    })
                  }
                />
              </div>
              <div className="form-field">
                <label>Group Account</label>
                <input
                  className="form-input"
                  value={mappingForm.groupAccountCode}
                  onChange={(e) =>
                    setMappingForm({
                      ...mappingForm,
                      groupAccountCode: e.target.value
                    })
                  }
                />
              </div>
              <div className="form-field">
                <label>Group Name</label>
                <input
                  className="form-input"
                  value={mappingForm.groupAccountName}
                  onChange={(e) =>
                    setMappingForm({
                      ...mappingForm,
                      groupAccountName: e.target.value
                    })
                  }
                />
              </div>
              <div className="form-field">
                <label>Group Type</label>
                <select
                  className="form-input"
                  value={mappingForm.groupAccountType}
                  onChange={(e) =>
                    setMappingForm({
                      ...mappingForm,
                      groupAccountType: e.target.value
                    })
                  }
                >
                  {["ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE", "OTHER"].map(
                    (type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    )
                  )}
                </select>
              </div>
              <div className="form-field">
                <label>&nbsp;</label>
                <button className="btn primary" onClick={saveMapping}>
                  Add Mapping
                </button>
              </div>
            </div>

            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Company</th>
                    <th>Local</th>
                    <th>Group</th>
                    <th>Name</th>
                    <th>Type</th>
                  </tr>
                </thead>
                <tbody>
                  {mappings.map((mapping) => (
                    <tr key={mapping.id}>
                      <td>{mapping.companyCode}</td>
                      <td>{mapping.localAccountCode}</td>
                      <td>{mapping.groupAccountCode}</td>
                      <td>{mapping.groupAccountName}</td>
                      <td>{mapping.groupAccountType}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section className="card" style={{ marginBottom: 18 }}>
            <div className="section-title">Run Consolidation</div>
            <div className="form-grid">
              <div className="form-field">
                <label>Fiscal Year</label>
                <input
                  className="form-input"
                  value={runForm.fiscalYear}
                  onChange={(e) =>
                    setRunForm({ ...runForm, fiscalYear: e.target.value })
                  }
                />
              </div>
              <div className="form-field">
                <label>Period</label>
                <select
                  className="form-input"
                  value={runForm.periodNumber}
                  onChange={(e) =>
                    setRunForm({ ...runForm, periodNumber: e.target.value })
                  }
                >
                  {Array.from({ length: 12 }, (_, index) => (
                    <option key={index + 1} value={index + 1}>
                      {index + 1}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-field">
                <label>&nbsp;</label>
                <button
                  className="btn primary"
                  onClick={runConsolidation}
                  disabled={busy}
                >
                  Run Group Reporting
                </button>
              </div>
            </div>

            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Year</th>
                    <th>Period</th>
                    <th>Currency</th>
                    <th>Status</th>
                    <th>Debit</th>
                    <th>Credit</th>
                    <th>NCI</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {runs.map((run) => (
                    <tr key={run.id}>
                      <td>{run.fiscalYear}</td>
                      <td>{run.periodNumber}</td>
                      <td>{run.reportingCurrency}</td>
                      <td>{run.status}</td>
                      <td>{Number(run.totalDebit || 0).toFixed(2)}</td>
                      <td>{Number(run.totalCredit || 0).toFixed(2)}</td>
                      <td>{Number(run.nciAmount || 0).toFixed(2)}</td>
                      <td>
                        <button className="btn" onClick={() => showRun(run.id)}>
                          View balances
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}

      {runId && (
        <>
          <section className="card" style={{ marginBottom: 18 }}>
            <div className="table-caption">
              <strong>Consolidation Journals</strong>
              <span>{journals.length} journals</span>
            </div>
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Journal</th>
                    <th>Type</th>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Debit</th>
                    <th>Credit</th>
                  </tr>
                </thead>
                <tbody>
                  {journals.map((journal) => (
                    <tr key={journal.id}>
                      <td>{journal.journalNumber}</td>
                      <td>{journal.journalType}</td>
                      <td>{journal.postingDate}</td>
                      <td>{journal.status}</td>
                      <td>{Number(journal.totalDebit || 0).toFixed(2)}</td>
                      <td>{Number(journal.totalCredit || 0).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section className="card" style={{ marginBottom: 18 }}>
            <div className="table-caption">
              <strong>Consolidation Audit Trail</strong>
              <span>{audit.length} events</span>
            </div>
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Time</th>
                    <th>Event</th>
                    <th>Status</th>
                    <th>Actor</th>
                    <th>Reference</th>
                    <th>Details</th>
                  </tr>
                </thead>
                <tbody>
                  {audit.map((event) => (
                    <tr key={event.id}>
                      <td>{event.eventTime}</td>
                      <td>{event.eventType}</td>
                      <td>{event.eventStatus}</td>
                      <td>{event.actor}</td>
                      <td>{event.referenceNumber || "—"}</td>
                      <td>{event.details}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section className="card" style={{ marginBottom: 18 }}>
            <div className="table-caption">
              <strong>Non-Controlling Interest</strong>
              <span>{nci.length} minority interests</span>
            </div>
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Entity</th>
                    <th>Ownership</th>
                    <th>NCI %</th>
                    <th>Net Assets</th>
                    <th>NCI Amount</th>
                  </tr>
                </thead>
                <tbody>
                  {nci.map((item) => (
                    <tr key={item.id}>
                      <td>
                        {item.unit?.unitCode || "—"} —{" "}
                        {item.unit?.unitName || item.unit?.companyCode || "—"}
                      </td>
                      <td>{Number(item.ownershipPercent || 0).toFixed(2)}%</td>
                      <td>{Number(item.nciPercent || 0).toFixed(2)}%</td>
                      <td>{Number(item.netAssets || 0).toFixed(2)}</td>
                      <td>
                        <strong>{Number(item.nciAmount || 0).toFixed(2)}</strong>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section className="card">
            <div className="table-caption">
              <strong>Consolidated Trial Balance</strong>
              <span>Eliminations included</span>
            </div>
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>Company</th>
                    <th>Account</th>
                    <th>Type</th>
                    <th>FX</th>
                    <th>Translated Debit</th>
                    <th>Translated Credit</th>
                    <th>Elim Debit</th>
                    <th>Elim Credit</th>
                    <th>Final Debit</th>
                    <th>Final Credit</th>
                  </tr>
                </thead>
                <tbody>
                  {balances.map((balance) => (
                    <tr key={balance.id}>
                      <td>{balance.companyCode}</td>
                      <td>
                        {balance.accountCode} — {balance.accountName}
                      </td>
                      <td>{balance.accountType}</td>
                      <td>{Number(balance.fxRate || 1).toFixed(6)}</td>
                      <td>{Number(balance.translatedDebit || 0).toFixed(2)}</td>
                      <td>{Number(balance.translatedCredit || 0).toFixed(2)}</td>
                      <td>{Number(balance.eliminationDebit || 0).toFixed(2)}</td>
                      <td>{Number(balance.eliminationCredit || 0).toFixed(2)}</td>
                      <td>{Number(balance.finalDebit || 0).toFixed(2)}</td>
                      <td>{Number(balance.finalCredit || 0).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}
    </main>
  );
}
