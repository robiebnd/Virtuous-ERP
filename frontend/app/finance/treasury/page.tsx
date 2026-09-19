"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { financeApi } from "@/lib/api";

const money = (v: any) => Number(v || 0).toFixed(2);

export default function TreasuryPage() {
  const [cash, setCash] = useState<any>(null);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [tx, setTx] = useState<any[]>([]);
  const [forecasts, setForecasts] = useState<any[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const [bank, setBank] = useState({
    bankAccountNumber: "",
    transactionNumber: "",
    transactionDate: new Date().toISOString().slice(0, 10),
    valueDate: "",
    amount: "",
    direction: "OUT",
    reference: "",
    description: "",
  });

  const [rec, setRec] = useState({
    bankAccountNumber: "",
    transactionNumber: "",
    accountingDocumentNumber: "",
  });

  const [forecast, setForecast] = useState({
    forecastDate: new Date().toISOString().slice(0, 10),
    category: "",
    description: "",
    expectedInflow: "0",
    expectedOutflow: "0",
    currency: "USD",
  });

  async function load() {
    setLoading(true);
    setError("");
    try {
      const [c, a, t, f] = await Promise.all([
        financeApi.cashPosition(),
        financeApi.bankAccounts(),
        financeApi.bankTransactions(),
        financeApi.liquidityForecasts(),
      ]);
      setCash(c);
      setAccounts(a);
      setTx(t);
      setForecasts(f);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load treasury.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function capture(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    try {
      await financeApi.captureBankTransaction({
        ...bank,
        amount: Number(bank.amount),
        valueDate: bank.valueDate || undefined,
      });
      setBank((x) => ({
        ...x,
        transactionNumber: "",
        amount: "",
        reference: "",
        description: "",
      }));
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to capture bank transaction.");
    }
  }

  async function reconcile(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    try {
      await financeApi.reconcileBankTransaction(rec);
      setRec({
        bankAccountNumber: "",
        transactionNumber: "",
        accountingDocumentNumber: "",
      });
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to reconcile transaction.");
    }
  }

  async function createForecast(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    try {
      await financeApi.createLiquidityForecast({
        ...forecast,
        expectedInflow: Number(forecast.expectedInflow),
        expectedOutflow: Number(forecast.expectedOutflow),
      });
      setForecast((x) => ({
        ...x,
        category: "",
        description: "",
        expectedInflow: "0",
        expectedOutflow: "0",
      }));
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to create forecast.");
    }
  }

  return (
    <main className="content treasury-page">
      <div className="page-head treasury-head">
        <div>
          <div className="eyebrow">FINANCE / TREASURY</div>
          <h1>Treasury & Cash Management</h1>
          <p>Cash position, bank statement processing, reconciliation and liquidity forecasting.</p>
        </div>
        <div className="actions">
          <Link className="btn" href="/finance">Finance</Link>
          <button className="btn" onClick={load} disabled={loading}>
            {loading ? "Loading…" : "Refresh"}
          </button>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="grid stats treasury-stats">
        <div className="card stat">
          <div className="stat-label">Total Cash</div>
          <div className="stat-value">{money(cash?.totalCash)}</div>
          <div className="stat-foot">Posted bank GL balances</div>
        </div>
        <div className="card stat">
          <div className="stat-label">Bank Accounts</div>
          <div className="stat-value">{accounts.length}</div>
          <div className="stat-foot">Active treasury accounts</div>
        </div>
        <div className="card stat">
          <div className="stat-label">Unreconciled</div>
          <div className="stat-value">{tx.filter((x) => x.status === "UNRECONCILED").length}</div>
          <div className="stat-foot">Statement lines requiring matching</div>
        </div>
        <div className="card stat">
          <div className="stat-label">Forecast Items</div>
          <div className="stat-value">{forecasts.length}</div>
          <div className="stat-foot">Next 30 days</div>
        </div>
      </div>

      <nav className="treasury-tabs" aria-label="Treasury sections">
        <a className="treasury-tab active" href="#bank-statements">Bank Statements</a>
        <a className="treasury-tab" href="#reconciliation">Reconciliation</a>
        <a className="treasury-tab" href="#cash-position">Cash Position</a>
        <a className="treasury-tab" href="#liquidity">Liquidity Forecast</a>
        <a className="treasury-tab" href="#transactions">Transactions</a>
      </nav>

      <section id="bank-statements" className="card form-card treasury-form-card">
        <div className="section-title">Capture Bank Statement Line</div>
        <p className="treasury-help">Manually capture a bank statement transaction.</p>
        <form onSubmit={capture}>
          <div className="form-grid">
            <div className="form-field">
              <label>Bank Account</label>
              <select className="form-input" required value={bank.bankAccountNumber} onChange={(e) => setBank({ ...bank, bankAccountNumber: e.target.value })}>
                <option value="">Select account</option>
                {accounts.map((a) => (
                  <option key={a.accountNumber} value={a.accountNumber}>
                    {a.bankName} — {a.accountNumber}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-field">
              <label>Transaction Number</label>
              <input className="form-input" required value={bank.transactionNumber} onChange={(e) => setBank({ ...bank, transactionNumber: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Date</label>
              <input className="form-input" type="date" required value={bank.transactionDate} onChange={(e) => setBank({ ...bank, transactionDate: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Amount</label>
              <input className="form-input" type="number" min="0.01" step="0.01" required value={bank.amount} onChange={(e) => setBank({ ...bank, amount: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Direction</label>
              <select className="form-input" value={bank.direction} onChange={(e) => setBank({ ...bank, direction: e.target.value })}>
                <option value="IN">IN — Receipt</option>
                <option value="OUT">OUT — Payment</option>
              </select>
            </div>
            <div className="form-field">
              <label>Reference</label>
              <input className="form-input" value={bank.reference} onChange={(e) => setBank({ ...bank, reference: e.target.value })} />
            </div>
          </div>
          <div className="form-actions">
            <button className="btn primary">Capture Statement Line</button>
          </div>
        </form>
      </section>

      <section id="reconciliation" className="card form-card treasury-form-card">
        <div className="section-title">Reconcile Bank Transaction</div>
        <p className="treasury-help">Match a bank statement line to its accounting document.</p>
        <form onSubmit={reconcile}>
          <div className="form-grid">
            <div className="form-field">
              <label>Bank Account</label>
              <input className="form-input" required value={rec.bankAccountNumber} onChange={(e) => setRec({ ...rec, bankAccountNumber: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Transaction Number</label>
              <input className="form-input" required value={rec.transactionNumber} onChange={(e) => setRec({ ...rec, transactionNumber: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Accounting Document</label>
              <input className="form-input" required value={rec.accountingDocumentNumber} onChange={(e) => setRec({ ...rec, accountingDocumentNumber: e.target.value })} />
            </div>
          </div>
          <div className="form-actions">
            <button className="btn primary">Reconcile</button>
          </div>
        </form>
      </section>

      <section id="liquidity" className="card form-card treasury-form-card">
        <div className="section-title">Liquidity Forecast</div>
        <p className="treasury-help">Record expected inflows and outflows for cash planning.</p>
        <form onSubmit={createForecast}>
          <div className="form-grid">
            <div className="form-field">
              <label>Date</label>
              <input className="form-input" type="date" required value={forecast.forecastDate} onChange={(e) => setForecast({ ...forecast, forecastDate: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Category</label>
              <input className="form-input" required value={forecast.category} onChange={(e) => setForecast({ ...forecast, category: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Expected Inflow</label>
              <input className="form-input" type="number" min="0" step="0.01" value={forecast.expectedInflow} onChange={(e) => setForecast({ ...forecast, expectedInflow: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Expected Outflow</label>
              <input className="form-input" type="number" min="0" step="0.01" value={forecast.expectedOutflow} onChange={(e) => setForecast({ ...forecast, expectedOutflow: e.target.value })} />
            </div>
          </div>
          <div className="form-actions">
            <button className="btn primary">Add Forecast</button>
          </div>
        </form>
      </section>

      <section id="transactions" className="card treasury-table-card">
        <div className="section-title">Bank Statement Transactions</div>
        <div className="table-wrap">
          <table className="table">
            <thead><tr><th>Account</th><th>Transaction</th><th>Date</th><th>Direction</th><th>Amount</th><th>Status</th></tr></thead>
            <tbody>
              {tx.map((x) => (
                <tr key={x.id}>
                  <td>{x.bankAccount?.bankName} — {x.bankAccount?.accountNumber}</td>
                  <td>{x.transactionNumber}</td>
                  <td>{x.transactionDate}</td>
                  <td>{x.direction}</td>
                  <td>{money(x.amount)}</td>
                  <td>{x.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section id="cash-position" className="card treasury-table-card">
        <div className="section-title">Liquidity Forecast</div>
        <div className="table-wrap">
          <table className="table">
            <thead><tr><th>Date</th><th>Category</th><th>Inflow</th><th>Outflow</th><th>Net</th><th>Status</th></tr></thead>
            <tbody>
              {forecasts.map((x) => (
                <tr key={x.id}>
                  <td>{x.forecastDate}</td>
                  <td>{x.category}</td>
                  <td>{money(x.expectedInflow)}</td>
                  <td>{money(x.expectedOutflow)}</td>
                  <td>{money(Number(x.expectedInflow) - Number(x.expectedOutflow))}</td>
                  <td>{x.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}
