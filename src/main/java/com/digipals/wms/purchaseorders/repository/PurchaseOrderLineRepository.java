package com.digipals.wms.purchaseorders.repository;

import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderLineRepository
        extends JpaRepository<PurchaseOrderLine, UUID> {

    List<PurchaseOrderLine> findByPurchaseOrderId(UUID purchaseOrderId);

    boolean existsByPurchaseOrderIdAndProductId(
            UUID purchaseOrderId,
            UUID productId);

    /**
     * Returns the most recently used purchase price for a supplier/product pair.
     * Cancelled orders are excluded so that stale or voided prices do not drive
     * new procurement recommendations.
     */
    @Query("""
            select line.unitPrice
            from PurchaseOrderLine line
            join line.purchaseOrder po
            where po.supplier.id = :supplierId
              and line.product.id = :productId
              and po.status <> com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus.CANCELLED
              and line.unitPrice is not null
            order by po.orderDate desc
            """)
    List<BigDecimal> findRecentUnitPrices(
            @Param("supplierId") UUID supplierId,
            @Param("productId") UUID productId,
            org.springframework.data.domain.Pageable pageable);

    default Optional<BigDecimal> findLatestUnitPrice(UUID supplierId, UUID productId) {
        return findRecentUnitPrices(
                supplierId,
                productId,
                org.springframework.data.domain.PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }
}
