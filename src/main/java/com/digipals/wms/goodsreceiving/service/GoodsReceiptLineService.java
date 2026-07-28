package com.digipals.wms.goodsreceiving.service;

import com.digipals.wms.goodsreceiving.dto.CreateGoodsReceiptLineRequest;
import com.digipals.wms.goodsreceiving.dto.GoodsReceiptLineResponse;
import com.digipals.wms.goodsreceiving.dto.UpdateGoodsReceiptLineRequest;

import java.util.List;
import java.util.UUID;

public interface GoodsReceiptLineService {

    GoodsReceiptLineResponse create(
            CreateGoodsReceiptLineRequest request);

    GoodsReceiptLineResponse update(
            UUID id,
            UpdateGoodsReceiptLineRequest request);

    GoodsReceiptLineResponse findById(

            UUID id);


    List<GoodsReceiptLineResponse> findAll();

    List<GoodsReceiptLineResponse> findByGoodsReceipt(
            UUID goodsReceiptId);

    void delete(
            UUID id);
}