package com.digipals.wms.goodsreceiving.service;

import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptRequest;

import java.util.List;
import java.util.UUID;

public interface GoodsReceiptService {

    GoodsReceiptResponse create(CreateGoodsReceiptRequest request);

    GoodsReceiptResponse update(UUID id, UpdateGoodsReceiptRequest request);

    GoodsReceiptResponse approve(UUID id);

    GoodsReceiptResponse findById(UUID id);

    GoodsReceiptResponse findByNumber(String grnNumber);

    GoodsReceiptResponse loadPurchaseOrderLines(UUID goodsReceiptId);

    List<GoodsReceiptResponse> findAll();

    List<GoodsReceiptResponse> findByPurchaseOrder(UUID purchaseOrderId);

    void delete(UUID id);
}
