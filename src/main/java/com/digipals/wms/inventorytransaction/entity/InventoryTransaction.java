package com.digipals.wms.inventorytransaction.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryTransaction extends BaseEntity {

    /**
     * Inventory record affected by this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "inventory_bin_id",
            nullable = false)
    private InventoryBin inventoryBin;

    /**
     * Type of inventory movement.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false,
            length = 50)
    private TransactionType transactionType;

    /**
     * Positive = stock in
     * Negative = stock out
     */
    @Column(
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal quantity;

    /**
     * Quantity remaining after the transaction.
     */
    @Column(
            name = "balance_after",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal balanceAfter;

    /**
     * Source document number.
     * Example:
     * BT000001
     * SA000004
     * ST000003
     * GRN000012
     */
    @Column(
            name = "reference_number",
            nullable = false,
            length = 50)
    private String referenceNumber;

    /**
     * BIN_TRANSFER
     * STOCK_TRANSFER
     * STOCK_ADJUSTMENT
     * STOCK_COUNT
     * GOODS_RECEIPT
     */
    @Column(
            name = "reference_type",
            nullable = false,
            length = 50)
    private String referenceType;

    /**
     * Optional unit cost.
     */
    @Column(
            name = "unit_cost",
            precision = 18,
            scale = 2)
    private BigDecimal unitCost;

    /**
     * User who performed the transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    /**
     * Source Bin.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bin_id")
    private Bin fromBin;

    /**
     * Destination Bin.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bin_id")
    private Bin toBin;

    /**
     * Optional remarks.
     */
    @Column(length = 500)
    private String remarks;

    /**
     * Transaction timestamp.
     */
    @Builder.Default
    @Column(
            name = "transaction_date",
            nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
}