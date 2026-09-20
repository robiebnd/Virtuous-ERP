"use client";

import { useEffect, useState } from "react";
import { financeApi } from "@/lib/api";

type Filing = {
  taxType: string;
  periodStart: string;
  periodEnd: string;
  filingReference: string;
  notes: string;
  filedBy: string;
};

export default function TaxAccountingPage() {
  const [companies, setCompanies] = useState<any[]>([]);
  const [codes, setCodes] = useState<any[]>([]);
  const [postings, setPostings] = useState<any[]>([]);
  const [filings, setFilings] = useState<any[]>([]);
  const [report, setReport] = useState<any>(null);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [company, setCompany] = useState("ZW01");

  const today = new Date().toISOString().slice(0, 10);
  const monthStart = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
    .toISOString()
    .slice(0, 10);

  const [reportStart, setReportStart] = useState(monthStart);
  const [reportEnd, setReportEnd] = useState(today);

  const [filing, setFiling] = useState<Filing>({
    taxType: "VAT",
    periodStart: "",
    periodEnd: "",
    filingReference: "",
    notes: "",
    filedBy: ""
  });

  const [codeForm, setCodeForm] = useState<any>({
    companyCode: "ZW01",
    taxCode: "VAT15",
    description: "Standard VAT",
    rate: "15",
    taxType: "VAT",
    inputAccountCode: "1510",
    outputAccountCode: "2210",
    recoverablePercent: "100",
    withholding: false
  });

  const [postForm, setPostForm] = useState<any>({
    companyCode: "ZW01",
    documentType: "TAX_INVOICE",
    referenceNumber: "",
    currency: "USD",
    taxCode: "VAT15",
    inputOutput: "OUTPUT",
    taxableAmount: "",
    baseDebitAccount: "1200",
    baseCreditAccount: "4100",
    description: "Tax accounting",
    postingDate: today
  });

  const load = async (selectedCompany = company) => {
    try {
      const [companyList, taxCodeList, taxPostingList, filingList] = await Promise.all([
        financeApi.companyCodes(),
        financeApi.taxCodes(),
        financeApi.taxPostings(selectedCompany),
        financeApi.taxFilings(selectedCompany)
      ]);

      setCompanies(companyList);
      setCodes(
        taxCodeList.filter(
          (x: any) => !x.companyCode || x.companyCode === selectedCompany
        )
      );
      setPostings(taxPostingList);
      setFilings(filingList);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load tax accounting.");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const changeCompany = (value: string) => {
    setCompany(value);
    setCodeForm((current: any) => ({ ...current, companyCode: value }));
    setPostForm((current: any) => ({ ...current, companyCode: value }));
    load(value);
  };

  const saveCode = async () => {
    setBusy(true);
    try {
      await financeApi.saveTaxCode({
        ...codeForm,
        rate: Number(codeForm.rate),
        recoverablePercent: Number(codeForm.recoverablePercent ?? 100)
      });
      await load(company);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to save tax code.");
    } finally {
      setBusy(false);
    }
  };

  const postTax = async () => {
    setBusy(true);
    try {
      await financeApi.postTax({
        ...postForm,
        taxableAmount: Number(postForm.taxableAmount)
      });
      await load(company);
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to post tax document.");
    } finally {
      setBusy(false);
    }
  };

  const runReport = async () => {
    try {
      setReport(await financeApi.taxReport(company, reportStart, reportEnd));
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load tax report.");
    }
  };

  const prepareFiling = async () => {
    setBusy(true);
    try {
      await financeApi.prepareTaxFiling({
        ...filing,
        companyCode: company,
        periodStart: filing.periodStart || reportStart,
        periodEnd: filing.periodEnd || reportEnd
      });
      setFilings(await financeApi.taxFilings(company));
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to prepare filing.");
    } finally {
      setBusy(false);
    }
  };

  const fileFiling = async () => {
    setBusy(true);
    try {
      await financeApi.fileTaxFiling({
        ...filing,
        companyCode: company,
        periodStart: filing.periodStart || reportStart,
        periodEnd: filing.periodEnd || reportEnd
      });
      setFilings(await financeApi.taxFilings(company));
      setError("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to file tax return.");
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="content">
      <div className="page-head">
        <div>
          <div className="eyebrow">FINANCE / TAX ACCOUNTING</div>
          <h1>Tax Accounting</h1>
          <p>
            Company-specific tax codes, input/output tax determination and
            integrated tax postings.
          </p>
        </div>
        <div className="actions">
          <button className="btn primary" onClick={() => load()} disabled={busy}>
            {busy ? "Working..." : "Refresh"}
          </button>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="card" style={{ marginBottom: 18 }}>
        <div className="toolbar">
          <div className="filter-field">
            <label>Company Code</label>
            <select
              className="filter"
              value={company}
              onChange={(e) => changeCompany(e.target.value)}
            >
              {companies.map((x: any) => (
                <option key={x.id} value={x.companyCode}>
                  {x.companyCode}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className="grid tax-form-grid" style={{ marginBottom: 18 }}>
        <section className="card form-card">
          <div className="section-title">Tax Code Setup</div>
          <div className="form-grid">
            <div className="form-field">
              <label>Tax Code</label>
              <input
                className="form-input"
                value={codeForm.taxCode}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, taxCode: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Description</label>
              <input
                className="form-input"
                value={codeForm.description}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, description: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Rate %</label>
              <input
                className="form-input"
                type="number"
                value={codeForm.rate}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, rate: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Input Tax Account</label>
              <input
                className="form-input"
                value={codeForm.inputAccountCode}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, inputAccountCode: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Output Tax Account</label>
              <input
                className="form-input"
                value={codeForm.outputAccountCode}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, outputAccountCode: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Tax Type</label>
              <input
                className="form-input"
                value={codeForm.taxType}
                onChange={(e) =>
                  setCodeForm({ ...codeForm, taxType: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Recoverable %</label>
              <input
                className="form-input"
                type="number"
                value={codeForm.recoverablePercent}
                onChange={(e) =>
                  setCodeForm({
                    ...codeForm,
                    recoverablePercent: e.target.value
                  })
                }
              />
            </div>
            <div className="form-field">
              <label>&nbsp;</label>
              <button className="btn primary" onClick={saveCode} disabled={busy}>
                Save Tax Code
              </button>
            </div>
          </div>
        </section>

        <section className="card form-card">
          <div className="section-title">Integrated Tax Posting</div>
          <div className="form-grid">
            <div className="form-field">
              <label>Reference Number</label>
              <input
                className="form-input"
                value={postForm.referenceNumber}
                onChange={(e) =>
                  setPostForm({ ...postForm, referenceNumber: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Tax Code</label>
              <select
                className="form-input"
                value={postForm.taxCode}
                onChange={(e) =>
                  setPostForm({ ...postForm, taxCode: e.target.value })
                }
              >
                {codes.map((x: any) => (
                  <option key={x.id} value={x.taxCode}>
                    {x.taxCode}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-field">
              <label>Direction</label>
              <select
                className="form-input"
                value={postForm.inputOutput}
                onChange={(e) =>
                  setPostForm({ ...postForm, inputOutput: e.target.value })
                }
              >
                <option value="OUTPUT">OUTPUT</option>
                <option value="INPUT">INPUT</option>
              </select>
            </div>
            <div className="form-field">
              <label>Taxable Amount</label>
              <input
                className="form-input"
                type="number"
                value={postForm.taxableAmount}
                onChange={(e) =>
                  setPostForm({ ...postForm, taxableAmount: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Base Debit Account</label>
              <input
                className="form-input"
                value={postForm.baseDebitAccount}
                onChange={(e) =>
                  setPostForm({ ...postForm, baseDebitAccount: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Base Credit Account</label>
              <input
                className="form-input"
                value={postForm.baseCreditAccount}
                onChange={(e) =>
                  setPostForm({ ...postForm, baseCreditAccount: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>Posting Date</label>
              <input
                className="form-input"
                type="date"
                value={postForm.postingDate}
                onChange={(e) =>
                  setPostForm({ ...postForm, postingDate: e.target.value })
                }
              />
            </div>
            <div className="form-field">
              <label>&nbsp;</label>
              <button className="btn primary" onClick={postTax} disabled={busy}>
                Post Tax Document
              </button>
            </div>
          </div>
        </section>
      </div>

      <section className="card" style={{ marginBottom: 18 }}>
        <div className="table-caption">
          <strong>Tax Report</strong>
          <span>Period summary and filing base</span>
        </div>
        <div className="toolbar">
          <div className="filter-field">
            <label>From</label>
            <input
              className="filter"
              type="date"
              value={reportStart}
              onChange={(e) => setReportStart(e.target.value)}
            />
          </div>
          <div className="filter-field">
            <label>To</label>
            <input
              className="filter"
              type="date"
              value={reportEnd}
              onChange={(e) => setReportEnd(e.target.value)}
            />
          </div>
          <div className="filter-field">
            <label>&nbsp;</label>
            <button className="btn primary" onClick={runReport}>
              Run Tax Report
            </button>
          </div>
        </div>

        {report && (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Output Tax</th>
                  <th>Input Tax</th>
                  <th>Recoverable Input</th>
                  <th>Net Tax</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{Number(report.outputTax || 0).toFixed(2)}</td>
                  <td>{Number(report.inputTax || 0).toFixed(2)}</td>
                  <td>{Number(report.recoverableInputTax || 0).toFixed(2)}</td>
                  <td>
                    <strong>{Number(report.netTax || 0).toFixed(2)}</strong>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        )}
      </section>

      <section className="card" style={{ marginBottom: 18 }}>
        <div className="table-caption">
          <strong>Statutory Tax Filing Control</strong>
          <span>Prepare, review and lock a filing period</span>
        </div>

        <div className="form-grid">
          <div className="form-field">
            <label>Tax Type</label>
            <input
              className="form-input"
              value={filing.taxType}
              onChange={(e) =>
                setFiling({ ...filing, taxType: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>Period Start</label>
            <input
              className="form-input"
              type="date"
              value={filing.periodStart || reportStart}
              onChange={(e) =>
                setFiling({ ...filing, periodStart: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>Period End</label>
            <input
              className="form-input"
              type="date"
              value={filing.periodEnd || reportEnd}
              onChange={(e) =>
                setFiling({ ...filing, periodEnd: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>Filing Reference</label>
            <input
              className="form-input"
              value={filing.filingReference}
              onChange={(e) =>
                setFiling({ ...filing, filingReference: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>Filed By</label>
            <input
              className="form-input"
              value={filing.filedBy}
              onChange={(e) =>
                setFiling({ ...filing, filedBy: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>Notes</label>
            <input
              className="form-input"
              value={filing.notes}
              onChange={(e) =>
                setFiling({ ...filing, notes: e.target.value })
              }
            />
          </div>
          <div className="form-field">
            <label>&nbsp;</label>
            <div className="actions">
              <button className="btn" onClick={prepareFiling} disabled={busy}>
                Prepare Filing
              </button>
              <button className="btn primary" onClick={fileFiling} disabled={busy}>
                File and Lock
              </button>
            </div>
          </div>
        </div>

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Tax Type</th>
                <th>Period</th>
                <th>Status</th>
                <th>Output</th>
                <th>Recoverable Input</th>
                <th>Net Tax</th>
                <th>Reference</th>
              </tr>
            </thead>
            <tbody>
              {filings.map((x: any) => (
                <tr key={x.id}>
                  <td>{x.taxType}</td>
                  <td>
                    {x.periodStart} — {x.periodEnd}
                  </td>
                  <td>{x.status}</td>
                  <td>{Number(x.outputTax || 0).toFixed(2)}</td>
                  <td>{Number(x.recoverableInputTax || 0).toFixed(2)}</td>
                  <td>{Number(x.netTax || 0).toFixed(2)}</td>
                  <td>{x.filingReference || "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card">
        <div className="table-caption">
          <strong>Tax Postings</strong>
          <span>{postings.length} records for {company}</span>
        </div>
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Tax Code</th>
                <th>Type</th>
                <th>Direction</th>
                <th>Taxable Base</th>
                <th>Tax</th>
                <th>Recoverable</th>
                <th>Account</th>
              </tr>
            </thead>
            <tbody>
              {postings.map((x: any) => (
                <tr key={x.id}>
                  <td>{x.createdAt?.slice(0, 10)}</td>
                  <td>{x.taxCode}</td>
                  <td>{x.taxType}</td>
                  <td>{x.inputOutput}</td>
                  <td>{Number(x.taxableBase || 0).toFixed(2)}</td>
                  <td>{Number(x.taxAmount || 0).toFixed(2)}</td>
                  <td>{Number(x.recoverableAmount || 0).toFixed(2)}</td>
                  <td>{x.taxAccountCode}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}
