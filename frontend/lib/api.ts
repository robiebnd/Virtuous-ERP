const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8081";

export async function api<T>(path:string, options:RequestInit={}) : Promise<T>{
  const token = typeof window !== "undefined" ? localStorage.getItem("virtuous_token") : null;
  const headers = new Headers(options.headers);
  headers.set("Content-Type","application/json");
  if(token) headers.set("Authorization",`Bearer ${token}`);
  const response=await fetch(`${API_BASE}${path}`,{...options,headers,cache:"no-store"});
  if(!response.ok){const message=await response.text();throw new Error(message||`Request failed: ${response.status}`)}
  if(response.status===204)return undefined as T;
  return response.json() as Promise<T>;
}

export type PurchaseRequisition={
  id:string; requisitionNumber?:string; status?:string; documentType?:string;
  purchasingGroup?:string; plantCode?:string; storageLocation?:string;
  currency?:string; remarks?:string; requestedDeliveryDate?:string;
  valuationPrice?:number; totalValue?:number; createdAt?:string;
};

export const procurementApi={
  requisitions:()=>api<PurchaseRequisition[]>("/api/purchase-requisitions"),
  requisition:(id:string)=>api<PurchaseRequisition>(`/api/purchase-requisitions/${id}`),
  orders:()=>api<any[]>("/api/purchase-orders"),
  goodsReceipts:()=>api<any[]>("/api/goods-receipts"),
  vendorInvoices:()=>api<any[]>("/api/vendor-invoices"),
};
