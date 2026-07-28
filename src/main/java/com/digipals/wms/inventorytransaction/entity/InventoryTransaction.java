package com.digipals.wms.inventorytransaction.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.inventory.entity.Inventory;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "inventory_id",
            nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false,
            length = 50)
    private TransactionType transactionType;

    @Column(
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal quantity;

    @Column(
            name = "balance_after",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal balanceAfter;

    @Column(
            name = "reference_number",
            nullable = false,
            length = 50)
    private String referenceNumber;

    @Column(
            name = "reference_type",
            nullable = false,
            length = 50)
    private String referenceType;

    private BigDecimal unitCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(length = 500)
    private String remarks;

    @Column(name = "transaction_date", nullable = false)
    @Builder.Default  // <--- Tells Lombok's builder to use this default instead of null
    private LocalDateTime transactionDate = LocalDateTime.now(); 
}