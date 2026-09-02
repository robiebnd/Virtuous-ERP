package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name="sales_organizations") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SalesOrganization extends BaseEntity { @Column(nullable=false,unique=true,length=20) String code; @Column(nullable=false) String name; @Column(length=3) String currency; String country; }

@Entity @Table(name="distribution_channels") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class DistributionChannel extends BaseEntity { @Column(nullable=false,unique=true,length=20) String code; @Column(nullable=false) String name; }

@Entity @Table(name="divisions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class Division extends BaseEntity { @Column(nullable=false,unique=true,length=20) String code; @Column(nullable=false) String name; }

@Entity @Table(name="sales_areas", uniqueConstraints=@UniqueConstraint(columnNames={"sales_organization_id","distribution_channel_id","division_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SalesArea extends BaseEntity { @Column(nullable=false,unique=true,length=60) String code; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_organization_id",nullable=false) SalesOrganization salesOrganization; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="distribution_channel_id",nullable=false) DistributionChannel distributionChannel; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="division_id",nullable=false) Division division; }

@Entity @Table(name="shipping_points") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class ShippingPoint extends BaseEntity { @Column(nullable=false,unique=true,length=30) String code; @Column(nullable=false) String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="warehouse_id",nullable=false) Warehouse warehouse; String address; }

@Entity @Table(name="sales_offices") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SalesOffice extends BaseEntity { @Column(nullable=false,unique=true,length=30) String code; @Column(nullable=false) String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_organization_id",nullable=false) SalesOrganization salesOrganization; }

@Entity @Table(name="sales_groups") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SalesGroup extends BaseEntity { @Column(nullable=false,unique=true,length=30) String code; @Column(nullable=false) String name; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_office_id",nullable=false) SalesOffice salesOffice; }

@Entity @Table(name="customer_sales_areas",uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","sales_area_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class CustomerSalesArea extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id",nullable=false) SalesArea salesArea; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_office_id") SalesOffice salesOffice; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_group_id") SalesGroup salesGroup; String paymentTerms; String customerPricingGroup; Integer deliveryPriority; String shippingCondition; BigDecimal creditLimit; @Builder.Default BigDecimal creditExposure=BigDecimal.ZERO; @Builder.Default Boolean creditBlocked=false; }

@Entity @Table(name="product_sales_data",uniqueConstraints=@UniqueConstraint(columnNames={"product_id","sales_area_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class ProductSalesData extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) Product product; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id",nullable=false) SalesArea salesArea; String salesUnit; String taxClassification; String itemCategoryGroup; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="delivering_plant_id") Warehouse deliveringPlant; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="shipping_point_id") ShippingPoint shippingPoint; @Builder.Default BigDecimal minimumOrderQuantity=BigDecimal.ZERO; @Builder.Default String salesStatus="ACTIVE"; }

@Entity @Table(name="customer_material_info",uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","product_id"})) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class CustomerMaterialInfo extends BaseEntity { @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) Product product; @Column(nullable=false) String customerMaterialNumber; String customerDescription; String customerUnit; }

@Entity @Table(name="pricing_conditions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class PricingCondition extends BaseEntity { @Column(nullable=false) String conditionType; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_area_id") SalesArea salesArea; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id") Customer customer; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id") Product product; @Column(nullable=false) LocalDateTime validFrom; @Column(nullable=false) LocalDateTime validTo; @Column(nullable=false,precision=18,scale=6) BigDecimal rate; @Column(nullable=false) String rateType; String currency; @Builder.Default Integer priority=100; }
