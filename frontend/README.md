# Virtuous ERP Frontend

Next.js frontend for Virtuous ERP.

## Local development

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:3000` and uses `http://localhost:8081` as the default backend API. Override it with `NEXT_PUBLIC_API_BASE_URL` when required.

A JWT can be placed in browser local storage under `virtuous_token` for authenticated API requests.

## Design direction

The UI is intentionally inspired by the supplied SAP S/4HANA and SAP Business ByDesign screenshots: dense enterprise worklists, document headers, tabs, process-oriented navigation, warehouse monitor tree views and operational tables. It is an original Virtuous ERP interface and does not copy SAP branding/assets.
