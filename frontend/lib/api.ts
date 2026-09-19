const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8081";

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = typeof window !== "undefined" ? localStorage.getItem("virtuous_token") : null;
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData)) headers.set("Content-Type", "application/json");
  if (token) headers.set("Authorization", `Bearer ${token}`);
  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers, cache: "no-store" });
  } catch (error) {
    throw new Error(`Backend unavailable at ${API_BASE}. Start the Spring Boot backend and try again.`);
  }
  if (!response.ok) {
    const body = await response.text();
    throw new Error(body || `Request failed: ${response.status}`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export type PurchaseRequisition = { id:string; requisitionNumber?:string; status?:string; documentType?:string; purchasingGroup?:string; plantCode?:string; storageLocation?:string; accountAssignmentCategory?:string; itemCategory?:string; warehouseId?:string; supplierId?:string; currency?:string; remarks?:string; requestedDeliveryDate?:string; valuationPrice?:number; totalValue?:number; createdAt?:string; createdBy?:any; lines?:PurchaseRequisitionLine[] };
export type PurchaseRequisitionLine = { id?:string; productId?:string; product?:any; quantity?:number; estimatedUnitCost?:number; valuationPrice?:number; unitOfMeasure?:string; requestedDeliveryDate?:string; itemCategory?:string; accountAssignmentCategory?:string; remarks?:string };
const list = <T,>(path:string) => api<any>(path).then(r => Array.isArray(r) ? r : (r?.content ?? r?.data ?? [])) as Promise<T[]>;

export const masterDataApi = {
 suppliers:()=>list<any>("/api/suppliers/active"),
 warehouses:()=>list<any>("/api/warehouses")
};

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
 documentFlow:(orderNumber:string)=>api<any>(`/api/document-flow/sales-orders/number/${encodeURIComponent(orderNumber)}`)
};

export const financeApi = {
 glAccounts:()=>list<any>("/api/finance/gl-accounts"),
 accountingDocuments:()=>list<any>("/api/finance/accounting-documents"),
 accountingDocument:(id:string)=>api<any>(`/api/finance/accounting-documents/${id}`),
 trialBalance:()=>list<any>("/api/finance/trial-balance"),
 openItemsAp:()=>list<any>("/api/finance/open-items/ap"),
 openItemsAr:()=>list<any>("/api/finance/open-items/ar"),
 ageing:(type:"AP"|"AR"="AR")=>api<any>(`/api/finance/ageing?type=${type}`),
 inventoryValuation:(warehouseId?:string)=>list<any>(warehouseId ? `/api/finance/inventory-valuation?warehouseId=${encodeURIComponent(warehouseId)}` : "/api/finance/inventory-valuation"),
 inventoryReconciliation:()=>api<any>("/api/finance/inventory-reconciliation"),
 postJournalEntry:(body:any)=>api<any>("/api/finance/journal-entries",{method:"POST",body:JSON.stringify(body)}),
 costCenters:()=>list<any>("/api/finance/cost-centers"),
 createCostCenter:(body:any)=>api<any>("/api/finance/cost-centers",{method:"POST",body:JSON.stringify(body)}),
 bankAccounts:()=>list<any>("/api/finance/bank-accounts"),
 createBankAccount:(body:any)=>api<any>("/api/finance/bank-accounts",{method:"POST",body:JSON.stringify(body)}),
 fixedAssets:()=>list<any>("/api/finance/fixed-assets"),
 createFixedAsset:(body:any)=>api<any>("/api/finance/fixed-assets",{method:"POST",body:JSON.stringify(body)}),
 depreciateAsset:(id:string,months=1)=>api<any>(`/api/finance/fixed-assets/${id}/depreciate?months=${months}`,{method:"POST"}),
  managementCostCenterActuals:(fiscalYear:number)=>list<any>(`/api/finance/management-accounting/cost-centers/actuals?fiscalYear=${fiscalYear}`),
  saveCostCenterBudget:(body:any)=>api<any>("/api/finance/management-accounting/cost-center-budgets",{method:"POST",body:JSON.stringify(body)}),
  internalOrders:()=>list<any>("/api/finance/management-accounting/internal-orders"),
  internalOrderActuals:()=>list<any>("/api/finance/management-accounting/internal-orders/actuals"),
  createInternalOrder:(body:any)=>api<any>("/api/finance/management-accounting/internal-orders",{method:"POST",body:JSON.stringify(body)}),
  allocateCostCenter:(body:any)=>api<any>("/api/finance/management-accounting/allocations",{method:"POST",body:JSON.stringify(body)}),
  cashPosition:()=>api<any>("/api/finance/treasury/cash-position"),
  bankTransactions:(bankAccountNumber?:string)=>list<any>(bankAccountNumber?"/api/finance/treasury/bank-transactions?bankAccountNumber="+encodeURIComponent(bankAccountNumber):"/api/finance/treasury/bank-transactions"),
  captureBankTransaction:(body:any)=>api<any>("/api/finance/treasury/bank-transactions",{method:"POST",body:JSON.stringify(body)}),
  reconcileBankTransaction:(body:any)=>api<any>("/api/finance/treasury/bank-reconciliation",{method:"POST",body:JSON.stringify(body)}),
  liquidityForecasts:(from?:string,to?:string)=>list<any>(`/api/finance/treasury/liquidity-forecasts?from=${from||""}&to=${to||""}`),
  createLiquidityForecast:(body:any)=>api<any>("/api/finance/treasury/liquidity-forecasts",{method:"POST",body:JSON.stringify(body)}),
  advancedCredits:()=>list<any>("/api/finance/advanced/credit-profiles"),
  saveCredit:(body:any)=>api<any>("/api/finance/advanced/credit-profiles",{method:"POST",body:JSON.stringify(body)}),
  collections:(status?:string)=>list<any>(status?"/api/finance/advanced/collections?status="+encodeURIComponent(status):"/api/finance/advanced/collections"),
  createCollection:(body:any)=>api<any>("/api/finance/advanced/collections",{method:"POST",body:JSON.stringify(body)}),
  disputes:(status?:string)=>list<any>(status?"/api/finance/advanced/disputes?status="+encodeURIComponent(status):"/api/finance/advanced/disputes"),
  createDispute:(body:any)=>api<any>("/api/finance/advanced/disputes",{method:"POST",body:JSON.stringify(body)}),
  revenueContracts:()=>list<any>("/api/finance/advanced/revenue-contracts"),
  createRevenueContract:(body:any)=>api<any>("/api/finance/advanced/revenue-contracts",{method:"POST",body:JSON.stringify(body)}),
  revenueObligations:(id:string)=>list<any>(`/api/finance/advanced/revenue-obligations/${id}`),
  createRevenueObligation:(body:any)=>api<any>("/api/finance/advanced/revenue-obligations",{method:"POST",body:JSON.stringify(body)}),
  allocateRevenue:(body:any)=>api<any>("/api/finance/advanced/revenue-allocation",{method:"POST",body:JSON.stringify(body)}),
  recognizeRevenue:(body:any)=>api<any>("/api/finance/advanced/revenue-recognition",{method:"POST",body:JSON.stringify(body)}),
  taxCodes:()=>list<any>("/api/finance/advanced/tax-codes"),
  saveTaxCode:(body:any)=>api<any>("/api/finance/advanced/tax-codes",{method:"POST",body:JSON.stringify(body)}),
  fxRates:(date?:string)=>list<any>(date?"/api/finance/advanced/fx-rates?date="+date:"/api/finance/advanced/fx-rates"),
  saveFxRate:(body:any)=>api<any>("/api/finance/advanced/fx-rates",{method:"POST",body:JSON.stringify(body)}),
  fxValuation:(body:any)=>api<any>("/api/finance/advanced/fx-valuation",{method:"POST",body:JSON.stringify(body)}),
  funds:()=>list<any>("/api/finance/advanced/funds"),
  saveFund:(body:any)=>api<any>("/api/finance/advanced/funds",{method:"POST",body:JSON.stringify(body)}),
  commitFund:(body:any)=>api<any>("/api/finance/advanced/fund-commitments",{method:"POST",body:JSON.stringify(body)}),
  consolidationUnits:()=>list<any>("/api/finance/advanced/consolidation/units"),
  saveConsolidationUnit:(body:any)=>api<any>("/api/finance/advanced/consolidation/units",{method:"POST",body:JSON.stringify(body)}),
  consolidationGroups:()=>list<any>("/api/finance/advanced/consolidation/groups"),
  saveConsolidationGroup:(body:any)=>api<any>("/api/finance/advanced/consolidation/groups",{method:"POST",body:JSON.stringify(body)}),
  addConsolidationUnit:(groupId:string,unitId:string)=>api<any>(`/api/finance/advanced/consolidation/groups/${groupId}/units/${unitId}`,{method:"POST"}),
  consolidationAdjustments:(groupId:string)=>list<any>(`/api/finance/advanced/consolidation/groups/${groupId}/adjustments`),
  saveConsolidationAdjustment:(body:any)=>api<any>("/api/finance/advanced/consolidation/adjustments",{method:"POST",body:JSON.stringify(body)}),
  productCosts:(code:string)=>list<any>(`/api/finance/advanced/product-costing/${encodeURIComponent(code)}`),
  createProductCost:(body:any)=>api<any>("/api/finance/advanced/product-costing",{method:"POST",body:JSON.stringify(body)}),
  releaseProductCost:(id:string)=>api<any>(`/api/finance/advanced/product-costing/${id}/release`,{method:"POST"}),
  accrualTemplates:()=>list<any>("/api/finance/advanced/accrual-templates"),
  createAccrualTemplate:(body:any)=>api<any>("/api/finance/advanced/accrual-templates",{method:"POST",body:JSON.stringify(body)}),
  runAccrual:(id:string)=>api<any>(`/api/finance/advanced/accrual-templates/${id}/run`,{method:"POST"}),,
  settleInternalOrder:(body:any)=>api<any>("/api/finance/advanced/internal-orders/settlement",{method:"POST",body:JSON.stringify(body)})
  materialLedger:(code:string)=>list<any>(`/api/finance/advanced/material-ledger/${encodeURIComponent(code)}`),
  saveMaterialLedger:(body:any)=>api<any>("/api/finance/advanced/material-ledger",{method:"POST",body:JSON.stringify(body)})
};


export const warehouseExecutionApi = {
  inventory:()=>list<any>("/api/warehouse-execution/inventory"),
  warehouses:()=>list<any>("/api/warehouse-execution/warehouses"),
  bins:(warehouseId:string)=>list<any>(`/api/warehouse-execution/warehouses/${warehouseId}/bins`),
  products:()=>list<any>("/api/warehouse-execution/products"),
  adjust:(id:string,quantity:number)=>api<any>(`/api/warehouse-execution/inventory/${id}/adjust?quantity=${quantity}`,{method:"POST"}),
  reserve:(id:string,quantity:number)=>api<any>(`/api/warehouse-execution/inventory/${id}/reserve?quantity=${quantity}`,{method:"POST"}),
  release:(id:string,quantity:number)=>api<any>(`/api/warehouse-execution/inventory/${id}/release?quantity=${quantity}`,{method:"POST"}),
  transfer:(body:any)=>api<any>("/api/warehouse-execution/inventory/transfer",{method:"POST",body:JSON.stringify(body)}),
  startPicking:(id:string)=>api<any>(`/api/warehouse-execution/outbound/${id}/start-picking`,{method:"POST"}),
  confirmPicking:(id:string)=>api<any>(`/api/warehouse-execution/outbound/${id}/confirm-picking`,{method:"POST"}),
  confirmPacking:(id:string)=>api<any>(`/api/warehouse-execution/outbound/${id}/confirm-packing`,{method:"POST"}),
  postGoodsIssue:(id:string)=>api<any>(`/api/warehouse-execution/outbound/${id}/post-goods-issue`,{method:"POST"})
};


export const productsApi = {
  list:()=>list<any>("/api/products"),
  active:()=>list<any>("/api/products/active"),
  create:(body:any)=>api<any>("/api/products",{method:"POST",body:JSON.stringify(body)}),
  update:(id:string,body:any)=>api<any>(`/api/products/${id}`,{method:"PUT",body:JSON.stringify(body)}),
  remove:(id:string)=>api<any>(`/api/products/${id}`,{method:"DELETE"})
};
export const productMasterDataApi = {
  categories:()=>list<any>("/api/product-categories/active"),
  units:()=>list<any>("/api/uom/active")
};
