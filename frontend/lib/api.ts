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
 vendorInvoices:()=>list<any>("/api/vendor-invoices"),
 vendorPayments:()=>list<any>("/api/procurement/vendor-payments"),
 createVendorPayment:(body:any)=>api<any>("/api/procurement/vendor-payments",{method:"POST",body:JSON.stringify(body)}),
 approveVendorPayment:(id:string)=>api<any>(`/api/procurement/vendor-payments/${id}/approve`,{method:"PUT"}),
 payVendorPayment:(id:string)=>api<any>(`/api/procurement/vendor-payments/${id}/pay`,{method:"PUT"}),
 vendorEvaluations:(supplierId:string)=>list<any>(`/api/procurement/vendor-evaluations/supplier/${supplierId}`),
 grIr:(purchaseOrderId:string)=>api<any>(`/api/procurement/gr-ir/purchase-order/${purchaseOrderId}`),
 closeGrIr:(purchaseOrderId:string)=>api<any>(`/api/procurement/gr-ir/purchase-order/${purchaseOrderId}/close`,{method:"POST"})
};

export const orderToCashApi = {
 salesOrders:()=>list<any>("/api/sales-orders"),
 salesOrder:(id:string)=>api<any>(`/api/sales-orders/${id}`),
 createSalesOrder:(body:any)=>api<any>("/api/sales-orders",{method:"POST",body:JSON.stringify(body)}),
 deliveries:()=>list<any>("/api/outbound-deliveries"),
 delivery:(id:string)=>api<any>(`/api/outbound-deliveries/${id}`),
 createDelivery:(body:any)=>api<any>("/api/outbound-deliveries",{method:"POST",body:JSON.stringify(body)}),
 startPicking:(id:string)=>api<any>(`/api/outbound-deliveries/${id}/start-picking`,{method:"POST"}),
 confirmPicking:(id:string)=>api<any>(`/api/outbound-deliveries/${id}/confirm-picking`,{method:"POST"}),
 confirmPacking:(id:string)=>api<any>(`/api/outbound-deliveries/${id}/confirm-packing`,{method:"POST"}),
 postGoodsIssue:(id:string)=>api<any>(`/api/outbound-deliveries/${id}/post-goods-issue`,{method:"POST"}),
 billingDocuments:()=>list<any>("/api/billing-documents"),
 billingDocument:(id:string)=>api<any>(`/api/billing-documents/${id}`),
 createBillingDocument:(body:any)=>api<any>("/api/billing-documents",{method:"POST",body:JSON.stringify(body)}),
 postBillingDocument:(id:string)=>api<any>(`/api/billing-documents/${id}/post`,{method:"POST"}),
 incomingPayments:()=>list<any>("/api/incoming-payments"),
 receiveIncomingPayment:(body:any)=>api<any>("/api/incoming-payments",{method:"POST",body:JSON.stringify(body)}),
 documentFlow:(salesOrderId:string)=>api<any>(`/api/document-flow/sales-orders/${salesOrderId}`)
};

export const financeApi = {
 glAccounts:()=>list<any>("/api/finance/gl-accounts"),
 accountingDocuments:()=>list<any>("/api/finance/accounting-documents"),
 accountingDocument:(id:string)=>api<any>(`/api/finance/accounting-documents/${id}`),
 trialBalance:()=>list<any>("/api/finance/trial-balance"),
 openItemsAp:()=>list<any>("/api/finance/open-items/ap"),
 openItemsAr:()=>list<any>("/api/finance/open-items/ar"),
 ageing:(type:"AP"|"AR"="AR")=>api<any>(`/api/finance/ageing?type=${type}`),
 inventoryValuation:(warehouseId?:string)=>list<any>(warehouseId ? `/api/finance/inventory-valuation?warehouseId=${encodeURIComponent(warehouseId)}` : "/api/finance/inventory-valuation")
};
