package com.digipals.wms.goodsreceiving.repository;

import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoodsReceiptLineRepository
        extends JpaRepository<GoodsReceiptLine, UUID> {

    List<GoodsReceiptLine> findByGoodsReceiptId(
            UUID goodsReceiptId);

    boolean existsByGoodsReceiptId(UUID goodsReceiptId);

    List<GoodsReceiptLine> findByPurchaseOrderLineId(
            UUID purchaseOrderLineId);

    boolean existsByGoodsReceiptIdAndProductId(
            UUID goodsReceiptId,
            UUID productId);
}