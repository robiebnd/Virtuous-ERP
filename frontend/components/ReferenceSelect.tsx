"use client";

import { useEffect, useState } from "react";
import { masterDataApi } from "@/lib/api";

type Option = { id: string; label: string; meta?: string };

type Props = {
  type: "supplier" | "warehouse";
  value: string;
  onChange: (value: string) => void;
  required?: boolean;
  disabled?: boolean;
  placeholder?: string;
};

export function ReferenceSelect({ type, value, onChange, required, disabled, placeholder }: Props) {
  const [options, setOptions] = useState<Option[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    const loader = type === "supplier" ? masterDataApi.suppliers() : masterDataApi.warehouses();
    loader.then((rows: any[]) => {
      if (cancelled) return;
      setOptions(rows
        .filter((row: any) => row?.active !== false)
        .map((row: any) => ({
          id: row.id,
          label: type === "supplier"
            ? `${row.code || "—"} — ${row.name || "Unnamed supplier"}`
            : `${row.code || "—"} — ${row.name || "Unnamed warehouse"}`,
          meta: type === "supplier" ? row.city || row.country : row.city || row.country
        })));
    }).catch(() => {
      if (!cancelled) setOptions([]);
    }).finally(() => {
      if (!cancelled) setLoading(false);
    });
    return () => { cancelled = true; };
  }, [type]);

  return (
    <select
      className="form-input"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      disabled={disabled || loading}
      required={required}
    >
      <option value="">{loading ? "Loading…" : placeholder || `Select ${type}`}</option>
      {options.map((option) => (
        <option key={option.id} value={option.id}>{option.label}{option.meta ? ` · ${option.meta}` : ""}</option>
      ))}
    </select>
  );
}
