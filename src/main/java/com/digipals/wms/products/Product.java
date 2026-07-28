package com.digipals.wms.products;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.category.entity.ProductCategory;
import com.digipals.wms.uom.entity.UnitOfMeasure;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    private BigDecimal costPrice;

    private BigDecimal sellingPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "uom_id")
    private UnitOfMeasure unitOfMeasure;
}