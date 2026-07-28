package com.digipals.wms.goodsreceiving.repository;

import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoodsReceiptRepository
        extends JpaRepository<GoodsReceipt, UUID> {

    Optional<GoodsReceipt> findByGrnNumber(
            String grnNumber);

    boolean existsByGrnNumber(
            String grnNumber);

    List<GoodsReceipt> findByStatus(
            ReceiptStatus status);

    List<GoodsReceipt> findByPurchaseOrderId(
            UUID purchaseOrderId);

    List<GoodsReceipt> findByWarehouseId(
            UUID warehouseId);
}