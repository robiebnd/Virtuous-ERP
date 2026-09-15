const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8081";

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = typeof window !== "undefined" ? localStorage.getItem("virtuous_token") : null;
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData)) headers.set("Content-Type", "application/json");
  if (token) headers.set("Authorization", `Bearer ${token}`);
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers, cache: "no-store" });
  if (!response.ok) throw new Error((await response.text()) || `Request failed: ${response.status}`);
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export type PurchaseRequisition = { id:string; requisitionNumber?:string; status?:string; documentType?:string; purchasingGroup?:string; plantCode?:string; storageLocation?:string; accountAssignmentCategory?:string; itemCategory?:string; warehouseId?:string; supplierId?:string; currency?:string; remarks?:string; requestedDeliveryDate?:string; valuationPrice?:number; totalValue?:number; createdAt?:string; createdBy?:any; lines?:PurchaseRequisitionLine[] };
export type PurchaseRequisitionLine = { id?:string; productId?:string; product?:any; quantity?:number; estimatedUnitCost?:number; valuationPrice?:number; unitOfMeasure?:string; requestedDeliveryDate?:string; itemCategory?:string; accountAssignmentCategory?:string; remarks?:string };
const list = <T,>(path:string) => api<any>(path).then(r => Array.isArray(r) ? r : (r?.content ?? r?.data ?? [])) as Promise<T[]>;
export const procurementApi = {
 requisitions:()=>list<PurchaseRequisition>("/api/purchase-requisitions"),
 requisition:(id:string)=>api<PurchaseRequisition>(`/api/purchase-requisitions/${id}`),
 createRequisition:(body:any)=>api<PurchaseRequisition>("/api/purchase-requisitions",{method:"POST",body:JSON.stringify(body)}),
 addRequisitionLine:(id:string,body:any)=>api<any>(`/api/purchase-requisitions/${id}/lines`,{method:"POST",body:JSON.stringify(body)}),
 submitRequisition:(id:string)=>api<any>(`/api/purchase-requisitions/${id}/submit`,{method:"POST"}),
 approveRequisition:(id:string)=>api<any>(`/api/purchase-requisitions/${id}/approve`,{method:"PUT"}),
 rejectRequisition:(id:string,remarks:string)=>api<any>(`/api/purchase-requisitions/${id}/reject?remarks=${encodeURIComponent(remarks)}`,{method:"PUT"}),
 orders:()=>list<any>("/api/purchase-orders"),
 createOrderFromRequisition:(id:string)=>api<any>(`/api/purchase-orders/from-requisition/${id}`,{method:"POST"}),
 approveOrder:(id:string)=>api<any>(`/api/purchase-orders/${id}/approve`,{method:"PUT"}),
 goodsReceipts:()=>list<any>("/api/goods-receipts"),
 createGoodsReceiptFromOrderNumber:(poNumber:string)=>api<any>(`/api/goods-receipts/from-purchase-order/number/${encodeURIComponent(poNumber)}`,{method:"POST"}),
 loadPoLines:(id:string)=>api<any>(`/api/goods-receipts/${id}/load-po-lines`,{method:"POST"}),
 updateGoodsReceipt:(id:string,body:any)=>api<any>(`/api/goods-receipts/${id}`,{method:"PUT",body:JSON.stringify(body)}),
 approveGoodsReceipt:(id:string)=>api<any>(`/api/goods-receipts/${id}/approve`,{method:"PUT"}),
 vendorInvoices:()=>list<any>("/api/vendor-invoices")
};
