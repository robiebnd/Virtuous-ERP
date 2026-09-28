"use client";

import { useEffect, useState } from "react";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";
import { customerApi } from "@/lib/api";

export default function Customers() {
  const [customers, setCustomers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    customerApi.list().then(setCustomers).catch(() => setCustomers([])).finally(() => setLoading(false));
  }, []);

  const rows = customers.map(c => ({
    Customer: c.customerNumber || c.id,
    Name: c.name,
    Currency: "USD",
    Terms: c.paymentTerms || "—",
    Credit: c.creditLimit == null ? "—" : `USD ${Number(c.creditLimit).toLocaleString()}`,
    Status: c.creditBlocked ? "BLOCKED" : (c.active === false ? "INACTIVE" : "ACTIVE")
  }));

  return <ModuleWorkspace
    eyebrow="ORDER TO CASH / MASTER DATA"
    title="Customers"
    description="Maintain customer accounts used by sales orders, deliveries, billing and receivables."
    createHref="/order-to-cash/customers/new"
    createLabel="Create Customer"
    stats={[
      {label:"Active Customers",value:String(customers.filter(c => c.active !== false && !c.creditBlocked).length),foot:"Live customer master"},
      {label:"Credit Exposure",value:`USD ${customers.reduce((s,c)=>s+Number(c.creditLimit||0),0).toLocaleString()}`,foot:"Configured credit limits"},
      {label:"On Hold",value:String(customers.filter(c => c.creditBlocked).length),foot:"Credit blocked",tone:"warning"},
      {label:"New This Month",value:String(customers.filter(c => c.createdAt && new Date(c.createdAt).getMonth() === new Date().getMonth()).length),foot:"Customer onboarding",tone:"success"}
    ]}
    process={[
      {label:"Account",detail:"Customer master",status:"Active",tone:"approved"},
      {label:"Credit",detail:"Limit & terms",status:"Configured",tone:"ready"},
      {label:"Order",detail:"Sales processing",status:"Available",tone:"ready"}
    ]}
    columns={["Customer","Name","Currency","Terms","Credit","Status"]}
    rows={rows}
    loading={loading}
    searchPlaceholder="Search customer account or name..."
  />;
}