package com.digipals.wms.inventorymovement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_movements", indexes = {
        @Index(name = "idx_inventory_movement_sku", columnList = "sku"),
        @Index(name = "idx_inventory_movement_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_inventory_movement_reference", columnList = "reference_type,reference_id"),
        @Index(name = "idx_inventory_movement_date", columnList = "movement_date")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "from_bin_id")
    private UUID fromBinId;

    @Column(name = "to_bin_id")
    private UUID toBinId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private InventoryMovementType movementType;

    @Column(name = "reference_type", nullable = false, length = 30)
    private String referenceType;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "performed_by_id")
    private UUID performedById;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (movementDate == null) movementDate = LocalDateTime.now();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
