package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class SdFoundationEntities {
    private SdFoundationEntities() {}

    @Entity @Table(name="sales_organizations") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class SalesOrganization extends BaseEntity { @Column(nullable=false,unique=true,length=20) private String code; @Column(nullable=false) private String name; @Column(length=3) private String currency; private String country; }

    @Entity @Table(name="distribution_channels") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DistributionChannel extends BaseEntity { @Column(nullable=false,unique=true,length=20) private String code; @Column(nullable=false) private String name; }

    @Entity @Table(name="divisions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class Division extends BaseEntity { @Column(nullable=false,unique=true,length=20) private String code; @Column(nullable=false) private String name; }

    @Entity @Table(name="sales_areas", uniqueConstraints=@UniqueConstraint(columnNames={"sales_organization_id","distribution_channel_id","division_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class SalesArea extends BaseEntity { @Column(nullable=false,unique=true,length=60) private String code; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_organization_id",nullable=false) private SalesOrganization salesOrganization; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="distribution_channel_id",nullable=false) private DistributionChannel distributionChannel; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="division_id",nullable=false) private Division division; }

    @Entity @Table(name="shipping_points") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ShippingPoint extends BaseEntity { @Column(nullable=false,unique=true,length=30) private String code; @Column(nullable=false) private String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="warehouse_id",nullable=false) private Warehouse warehouse; private String address; }

    @Entity @Table(name="sales_offices") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class SalesOffice extends BaseEntity { @Column(nullable=false,unique=true,length=30) private String code; @Column(nullable=false) private String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_organization_id",nullable=false) private SalesOrganization salesOrganization; }

    @Entity @Table(name="sales_groups") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class SalesGroup extends BaseEntity { @Column(nullable=false,unique=true,length=30) private String code; @Column(nullable=false) private String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_office_id",nullable=false) private SalesOffice salesOffice; }

    @Entity @Table(name="customer_sales_areas",uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","sales_area_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CustomerSalesArea extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) private Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id",nullable=false) private SalesArea salesArea; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_office_id") private SalesOffice salesOffice; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_group_id") private SalesGroup salesGroup; private String paymentTerms; private String customerPricingGroup; private Integer deliveryPriority; private String shippingCondition; private BigDecimal creditLimit; @Builder.Default private BigDecimal creditExposure=BigDecimal.ZERO; @Builder.Default private Boolean creditBlocked=false; }

    @Entity @Table(name="product_sales_data",uniqueConstraints=@UniqueConstraint(columnNames={"product_id","sales_area_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ProductSalesData extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id",nullable=false) private SalesArea salesArea; private String salesUnit; private String taxClassification; private String itemCategoryGroup; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="delivering_plant_id") private Warehouse deliveringPlant; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="shipping_point_id") private ShippingPoint shippingPoint; @Builder.Default private BigDecimal minimumOrderQuantity=BigDecimal.ZERO; @Builder.Default private String salesStatus="ACTIVE"; }

    @Entity @Table(name="customer_material_info",uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","product_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CustomerMaterialInfo extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) private Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product; @Column(nullable=false) private String customerMaterialNumber; private String customerDescription; private String customerUnit; }

    @Entity @Table(name="pricing_conditions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class PricingCondition extends BaseEntity { @Column(nullable=false) private String conditionType; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id") private SalesArea salesArea; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id") private Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id") private Product product; @Column(nullable=false) private LocalDateTime validFrom; @Column(nullable=false) private LocalDateTime validTo; @Column(nullable=false,precision=18,scale=6) private BigDecimal rate; @Column(nullable=false) private String rateType; private String currency; @Builder.Default private Integer priority=100; }
}
